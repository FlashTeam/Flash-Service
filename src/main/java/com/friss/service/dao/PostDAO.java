package com.friss.service.dao;

import com.friss.service.post.Post;
import com.friss.service.post.mapper.PostMapper;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(PostMapper.class)
public interface PostDAO {

	@SqlQuery("select * from POSTS where TIMEOUT > :now ORDER BY TIME DESC")
	List<Post> getAll(@Bind("now") long currentTime);

	@SqlQuery("select * from POSTS where ID = :id")
	Post findById(@Bind("id") int id);

	@SqlUpdate("delete from POSTS where ID = :id")
	int deleteById(@Bind("id") int id);

	@SqlUpdate("delete from POSTS where TIMEOUT <= :now")
	int deleteOld(@Bind("now") long currentTime);

	@SqlUpdate("update POSTS set TIME = :time, IMAGE = :image, VOTE = :vote, COMMENT = :comment, TIMEOUT = :timeout, LONGITUDE = :longitude, LATITUDE = :latitude, USERNAME = :username  WHERE id = :id")
	int update(@BindBean Post post);

	@SqlUpdate("insert into POSTS (TIME, IMAGE, VOTE, COMMENT, TIMEOUT, LONGITUDE, LATITUDE, USERNAME) values (:time, :image, :vote, :comment, :timeout, :longitude, :latitude, :username)")
	@GetGeneratedKeys
	int insert(@BindBean Post post);

	@SqlQuery("select * from POSTS where comment LIKE concat('%',:query,'%') or USERNAME LIKE concat('%',:query,'%')")
	List<Post> search(@Bind("query") String query);
}
