package com.friss.service.post.mapper;

import com.friss.service.post.Post;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements ResultSetMapper<Post> {
	public Post map(int index, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new Post().setId(resultSet.getInt("ID"))
				.setTime(resultSet.getLong("TIME"))
				.setImage(resultSet.getString("IMAGE"))
				.setVote(resultSet.getInt("VOTE"))
				.setComment(resultSet.getString("COMMENT"))
				.setTimeout(resultSet.getLong("TIMEOUT"))
				.setLongitude(resultSet.getFloat("LONGITUDE"))
				.setLatitude(resultSet.getFloat("LATITUDE"))
				.setUsername(resultSet.getString("USERNAME"));
	}
}
