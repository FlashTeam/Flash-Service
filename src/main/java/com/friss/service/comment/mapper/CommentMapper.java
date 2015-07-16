package com.friss.service.comment.mapper;

import com.friss.service.comment.Comment;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentMapper implements ResultSetMapper<Comment> {
	public Comment map(int index, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new Comment().setId(resultSet.getInt("ID"))
				.setTime(resultSet.getLong("TIME"))
				.setPid(resultSet.getInt("PID"))
				.setComment(resultSet.getString("COMMENT"));
	}
}
