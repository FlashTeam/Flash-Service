package com.friss.flashservice.tests;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.DropwizardAppRule;

import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friss.service.FlashService;
import com.friss.service.config.FlashServiceConfiguration;
import com.friss.service.post.Post;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

public class PostResourceTest {

	/* Constants ----- */
	private static final String TestImageName = "test.jpg";
	private static final MediaType TestImageType = new MediaType("image","jpeg");
	private static final Long thirtySeconds = (long) 30000;

	@ClassRule
	public static final DropwizardAppRule<FlashServiceConfiguration> RULE = new DropwizardAppRule<FlashServiceConfiguration>(
			FlashService.class, "config.yml");
	
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	/* Test Initializers *****************************************************/

	/**
	 * Build a new post object
	 * 
	 * @return Post a default post
	 */
	public static Post BuildPost() {
		Long startTime = System.currentTimeMillis();
		return new Post()
			.setTime(startTime)
			.setImage(TestImageName)
			.setVote(1)
			.setComment("Test post")
			.setTimeout(thirtySeconds)
			.setLongitude((float) 0)
			.setLatitude((float) 0)
			.setUsername("Tester");
	}
	
	/**
	 * Add a post passed in as a Post object to the database
	 * @param p Post the post to add
	 * @return
	 */
	private ClientResponse addPost(Post p, MediaType type){
		return addPost(
				p.getTimeout().toString(),
				p.getLongitude().toString(),
				p.getLatitude().toString(),
				p.getComment(),
				p.getUsername(),
				p.getImage(),
				type);
			
	}
	
	/**
	 * Add a post passed in via string varaibles to the database
	 * @param timeout
	 * @param longitude
	 * @param latitude
	 * @param comment
	 * @param username
	 * @param image
	 * @param imageSubType The specific Mime Sub-type. Valid values are:
	 * 			jpeg, gif, png
	 * @return
	 */
	private ClientResponse addPost(
			String timeout,
			String longitude,
			String latitude,
			String comment,
			String username,
			String image,
			MediaType type) {
		
		FormDataMultiPart part = new FormDataMultiPart();
		part.field("timeout", timeout);
		part.field("longitude", longitude);
		part.field("latitude", latitude);
		part.field("comment", comment);
		part.field("username", username);

		/* Create the form data content disposition */
		final String value = "file";
		final FormDataContentDisposition dispo = FormDataContentDisposition//
				.name(value)//
				.fileName(image)//
				.size(value.getBytes().length)//
				.build();

		/* attach disposition to form body */
		final FormDataBodyPart bodyPart = new FormDataBodyPart(dispo, value);
		bodyPart.type(type);
		part.bodyPart(bodyPart);

		Client client = new Client();

		/* Send the form to endpoint */
		ClientResponse response = client
				.resource(
						String.format("http://localhost:%d/posts",
								RULE.getLocalPort()))
				.type(MediaType.MULTIPART_FORM_DATA_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, part);

		/* Return the response */
		return response;
	}

	/*-------------------------------------------------------------------------
	 * Tests
	 * 	- Use the following to retrive info from the db: 
	 * 		resources.client().resource("path/to/enpoint").get(ReturnType))
	 *-------------------------------------------------------------------------*/
	@Test
	public void testServerStarts() {
		Client client = new Client();

		ClientResponse response = client.resource(
				String.format("http://localhost:%d/", RULE.getLocalPort()))
				.get(ClientResponse.class);

		assertThat(response.getStatus()).isEqualTo(200);
	}

	/**
	 * Method: POST, Endpoint: /posts
	 * Tests adding a post through the api.
	 */
	@Test
	public void testAddPost() {
		ClientResponse response = addPost(BuildPost(), TestImageType);
		System.out.println(response.getEntity(String.class));
		assertThat(response.getStatus()).isEqualTo(200);
	}
	
	/**
	 * Method: GET, Endpoint: /posts/{id}
	 * Tests retrieving a post 
	 */
	@Test
	public void testGetPost(){
		Post p1 = BuildPost();//build default post
		p1.setComment("PostResourceTest.testGetPost 0123456789");//give post unique comment
		ClientResponse response = addPost(p1, TestImageType);
		/* If the post was added */
		if(response.getStatus() == 200){
			Post p2 = null;
			/* Get the post from the response */
			try {
				p2 = MAPPER.readValue(response.getEntity(String.class), Post.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			assertThat(p2).isNotNull();
			/* set p1 id to p2 and verify the rest of the data is equal */
			assertThat(p2.getComment()).isEqualTo(p1.getComment());
			/* Check the other parts of the data to verify they were parsed correctly */
			/* image should now have a unique name */
			assertThat(p2.getImage()).isNotEqualTo(p1.getImage());
			assertThat(p2.getLatitude()).isEqualTo(p1.getLatitude());
			assertThat(p2.getLongitude()).isEqualTo(p1.getLongitude());
			/* p2.timeout = p2.time + p1.timeout */
			assertThat(p2.getTimeout()).isEqualTo(p2.getTime() + p1.getTimeout());
			assertThat(p2.getUsername()).isEqualTo(p1.getUsername());
		}
	}
	
	/**
	 * METHOD: Post, Endpoint: /posts/{id}/vote/{up,down}
	 * tests voting on a post.
	 */
	@Test
	public void testVote(){
		Client client = new Client();
		Post p = BuildPost();
		Post p1 = null, p2 = null;
		ClientResponse response = addPost(p, TestImageType);
		if(response.getStatus() == 200)/* if the post was added */
		{
			/* find the id of the original post */
			try {
				p = MAPPER.readValue(response.getEntity(String.class), Post.class);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(response.toString());
			}
			/* try up voting the post ---------------------------------------*/
			/* post to the end point */
			response = client
				.resource(
				String.format("http://localhost:%d/posts/%d/vote/up",
					RULE.getLocalPort(), p.getId()))
						.post(ClientResponse.class, null);
			/* Get the post from the response */
			
			try {
				p1 = MAPPER.readValue(response.getEntity(String.class), Post.class);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(response.toString());
			}
			assertThat(p1).isNotNull();
			assertThat(p1.getVote()).isEqualTo(p.getVote()+1);
			
			/* try down voting the post -------------------------------------*/
			/* post to the end point */
			response = client
				.resource(
				String.format("http://localhost:%d/posts/%d/vote/down",
					RULE.getLocalPort(), p.getId()))
						.post(ClientResponse.class, null);
			/* Get the post from the response */
			
			try {
				p2 = MAPPER.readValue(response.getEntity(String.class), Post.class);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(response.toString());
			}
			assertThat(p2).isNotNull();
			assertThat(p2.getVote()).isEqualTo(p.getVote());//post should be p1.vote - 1 (or p.vote)
		}
	}
	
	/**
	 * Tests to check posts that are expired are not returned,
	 * and posts that are NOT expired ARE returned. Also,
	 * test edge cases 
	 */
	@Test
	public void testTimeout(){
		Post p = BuildPost();
		ClientResponse response = null;
		
		/* First, verify a post w/ timeout = 0 isnt returned */
		response = addPost(
				"0",
				p.getLongitude().toString(),
				p.getLatitude().toString(),
				p.getComment(),
				p.getUsername(),
				p.getImage(),
				TestImageType);
		assertThat(response.getStatus()).isNotEqualTo(200);
		
		/* Check a not timed out image IS returned */
		response = addPost(
				"30000",
				p.getLongitude().toString(),
				p.getLatitude().toString(),
				p.getComment(),
				p.getUsername(),
				p.getImage(),
				TestImageType);
		assertThat(response.getStatus()).isEqualTo(200);
		
		/* Check that a - timeout is not returned */
		response = addPost(
				"-30000",
				p.getLongitude().toString(),
				p.getLatitude().toString(),
				p.getComment(),
				p.getUsername(),
				p.getImage(),
				TestImageType);
		assertThat(response.getStatus()).isNotEqualTo(200);
		
		/* check if an absurdly large timeout is not entered (it should be out of range */
		response = addPost(
				"9999999999999999999999999999999999999999999999999999999999999999999999",
				p.getLongitude().toString(),
				p.getLatitude().toString(),
				p.getComment(),
				p.getUsername(),
				p.getImage(),
				TestImageType);
		assertThat(response.getStatus()).isNotEqualTo(200);
	}
	
	/**
	 * Test the edge cases of longitude
	 * -180 <= longitude <= 180
	 */
	@Test
	public void testLongitude(){
		Post p = BuildPost();
		
		/* Check longitude cant be > 180 */
		p.setLongitude((float)1000);
		assertThat(addPost(p, TestImageType).getStatus()).isNotEqualTo(200);
		
		/* Check longitude cant be < 180 */
		p.setLongitude((float)-1000);
		assertThat(addPost(p, TestImageType).getStatus()).isNotEqualTo(200);
		
		/* Check longitude CAN be 180 */
		p.setLongitude((float)180);
		assertThat(addPost(p, TestImageType).getStatus()).isEqualTo(200);
		
		/* Check longitude CAN be -180 */
		p.setLongitude((float)-180);
		assertThat(addPost(p, TestImageType).getStatus()).isEqualTo(200);
	}
	
	/**
	 * Test latitude is in range [-90,90]
	 */
	@Test
	public void testLatitude(){
		Post p = BuildPost();
		
		/* Check longitude cant be > 90 */
		p.setLatitude((float)1000);
		assertThat(addPost(p, TestImageType).getStatus()).isNotEqualTo(200);
		
		/* Check longitude cant be < 90 */
		p.setLatitude((float)-1000);
		assertThat(addPost(p, TestImageType).getStatus()).isNotEqualTo(200);
		
		/* Check longitude CAN be 90 */
		p.setLatitude((float)90);
		assertThat(addPost(p, TestImageType).getStatus()).isEqualTo(200);
		
		/* Check longitude CAN be -90 */
		p.setLatitude((float)-90);
		assertThat(addPost(p, TestImageType).getStatus()).isEqualTo(200);
	}

	
	/**
	 * Tests that invalid file types can't be uploaded.
	 */
	@Test
	public void testImageUpload(){
		Post p = BuildPost();
		/* try to upload a text file instead of an image */
		p.setImage("invalid.txt");
		assertThat(addPost(p,MediaType.TEXT_PLAIN_TYPE).getStatus()).isNotEqualTo(200);
	}
	
	
}
