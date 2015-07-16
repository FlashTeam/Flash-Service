package com.friss.service.dao;

import com.friss.service.comment.Comment;
import com.friss.service.comment.mapper.CommentMapper;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(CommentMapper.class)
public interface CommentDAO {

	@SqlQuery("select * from COMMENTS")
	List<Comment> getAll();

	@SqlQuery("select * from COMMENTS where ID = :id")
	Comment findById(@Bind("id") int id);

	@SqlUpdate("delete from COMMENTS where ID = :id")
	int deleteById(@Bind("id") int id);

	@SqlUpdate("update COMMENTS set PID = :pid, TIME = :time, COMMENT = :comment WHERE id = :id")
	int update(@BindBean Comment comment);

	@SqlUpdate("insert into COMMENTS (TIME, PID, COMMENT) values (:time, :pid, :comment)")
	@GetGeneratedKeys
	int insert(@BindBean Comment comment);

	@SqlQuery("select * from COMMENTS where PID = :pid")
	List<Comment> getAllPostComments(@Bind("pid") int pid);

	@SqlQuery("select * from COMMENTS where comment LIKE concat('%',:query,'%')")
	List<Comment> search(@Bind("query") String query);
}
