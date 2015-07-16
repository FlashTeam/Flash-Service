package com.friss.flashservice.tests;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.jackson.Jackson;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friss.service.comment.Comment;
import com.friss.service.post.Post;

public class PostTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void serializesToJSON() throws Exception {
		final List<Comment> comments = Collections.emptyList();
		final Post post = new Post()
				.setId(1)
				.setTime((long) 1424328272)
				.setImage("image.jpg")
				.setVote(1)
				.setComment("Test post")
				.setTimeout((long) 0)
				.setLongitude((float) 0)
				.setLatitude((float) 0)
				.setUsername("Tester")
				.setComments(comments);

		assertThat(MAPPER.writeValueAsString(post)).isEqualTo(
				fixture("fixtures/post.json"));
	}

	@Test
	public void deserializesFromJSON() throws Exception {
		final List<Comment> comments = Collections.emptyList();
		final Post post = new Post()
				.setId(1)
				.setTime((long) 1424328272)
				.setImage("image.jpg")
				.setVote(1)
				.setComment("Test post")
				.setTimeout((long) 0)
				.setLongitude((float) 0)
				.setLatitude((float) 0)
				.setUsername("Tester")
				.setComments(comments);
		assertThat(MAPPER.readValue(fixture("fixtures/post.json"), Post.class))
				.isEqualTo(post);
	}
	
	/**
	 * Test the comments can be empty and cannot be massive
	 */
	@Test
	public void testComment(){
		String comment = "";
		/* build a massive string to check */
		for(int i = 0; i < 10000; i++){
			comment += "derp";
		}
		Post p = new Post();
		p.setComment(comment);
		/* check that the huge comment wasnt made */
		assertThat(p.getComment()).isNotEqualTo(comment);
	}
}
