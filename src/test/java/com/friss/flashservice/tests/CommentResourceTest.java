package com.friss.flashservice.tests;



import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.DropwizardAppRule;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friss.service.FlashService;
import com.friss.service.comment.Comment;
import com.friss.service.config.FlashServiceConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.multipart.FormDataMultiPart;

/*
 * Test the endpoints of the CommentResource class
 */
public class CommentResourceTest {
	
	/* A default post id to use to test CommentResource */
	private static Integer PID = 1;//TODO: Always have index 0 in db to test
	
	@ClassRule
	public static final DropwizardAppRule<FlashServiceConfiguration> RULE = new DropwizardAppRule<FlashServiceConfiguration>(
			FlashService.class, "config.yml");
	
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	/* Test Initializers *****************************************************/
	
	/**
	 * Build a comment with default parameters
	 * @param p
	 * @return
	 */
	private static Comment BuildComment(){
		return new Comment()
			.setComment("Test Comment")
			.setPid(PID)
			.setTime(System.currentTimeMillis());
	}
	
	/**
	 * Add the comment to the db through the api
	 * @param c
	 * @return
	 */
	private static ClientResponse addComment(Comment c){
		FormDataMultiPart part = new FormDataMultiPart();
		part.field("pid", c.getPid().toString());
		part.field("comment", c.getComment());
		
		Client client = new Client();
		
		/* Send the form to endpoint */
		ClientResponse response = client
				.resource(
						String.format("http://localhost:%d/comments",
								RULE.getLocalPort()))
				.type(MediaType.MULTIPART_FORM_DATA_TYPE)
				.accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, part);

		/* Return the response */
		return response;
	}
	
	/**************************************************************************
	 * Tests
	 *************************************************************************/
	 
	@Test
	public void testAddComment(){
		/* Create a comment */		
		Comment c = BuildComment();
		ClientResponse response = addComment(c);
		/* If status is 200 then it was added */
		assertThat(response.getStatus()).isEqualTo(200);
	}
	
	@Test
	public void testGetComment(){
		/* Create comment */
		Comment c = BuildComment();
		c.setComment("testing getComment");
		ClientResponse response = addComment(c);
		
		/* Check the information */
		Comment c2 = null;
		try {
			c2 = MAPPER.readValue(response.getEntity(String.class), Comment.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Check the status, and compare the sent vs recieved comments */
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(c2).isNotNull();
		assertThat(c2.getComment()).isEqualTo(c.getComment());
		assertThat(c2.getPid()).isEqualTo(c.getPid());
	}

}
