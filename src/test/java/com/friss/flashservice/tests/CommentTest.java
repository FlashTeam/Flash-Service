package com.friss.flashservice.tests;

import static io.dropwizard.testing.FixtureHelpers.*;
import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.jackson.Jackson;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friss.service.comment.Comment;

public class CommentTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void serializesToJSON() throws Exception {
		final Comment comment = new Comment().setId(1).setTime((long) 1424328285)
				.setPid(1).setComment("Test comment.");
		assertThat(MAPPER.writeValueAsString(comment)).isEqualTo(
				fixture("fixtures/comment.json"));
	}

	@Test
	public void deserializesFromJSON() throws Exception {
		final Comment comment = new Comment().setId(1).setTime((long) 1424328285)
				.setPid(1).setComment("Test comment.");
		assertThat(
				MAPPER.readValue(fixture("fixtures/comment.json"),
						Comment.class)).isEqualTo(comment);
	}
}
