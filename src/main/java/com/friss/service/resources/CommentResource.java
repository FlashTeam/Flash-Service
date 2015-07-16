package com.friss.service.resources;

import com.codahale.metrics.annotation.Timed;
import com.friss.service.comment.Comment;
import com.friss.service.dao.CommentDAO;
import com.friss.service.dao.PostDAO;
import com.friss.service.exceptions.BadRequestException;
import com.friss.service.exceptions.ObjectNotFoundException;
import com.friss.service.post.Post;
import com.sun.jersey.multipart.FormDataParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import java.util.List;

@Path("/comments")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Api("/comments")
public class CommentResource {

	CommentDAO commentDAO;
	PostDAO postDAO;

	public CommentResource(CommentDAO commentDAO, PostDAO postDAO) {
		this.commentDAO = commentDAO;
		this.postDAO = postDAO;
	}

	@GET
	@Timed
	@ApiOperation("Get all comments")
	public List<Comment> getAll() {
		return commentDAO.getAll();
	}

	@GET
	@Timed
	@Path("/search/{query}")
	@ApiOperation("Search comments by path param")
	public List<Comment> search(@PathParam("query") String query) {
		return commentDAO.search(query);
	}

	@SuppressWarnings("unchecked")
	@GET
	@Timed
	@Path("/{id}")
	@ApiOperation(value = "Get comment by id", notes = "Get comment by id value", response = Comment.class)
	public Comment get(@PathParam("id") Integer id) {
		Comment comment = commentDAO.findById(id);
		if (comment != null) {
			return comment;
		} else {
			JSONObject obj = new JSONObject();
			obj.put("id", id);
			obj.put("status", "error");
			obj.put("errorMsg", "Comment not found.");
			throw new ObjectNotFoundException(obj);
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Timed
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create comment", notes = "Create comment", response = Comment.class)
	public Comment add(@FormDataParam("pid") final String pid,
			@FormDataParam("comment") final String commentString ) {
		int parsedPid = Integer.parseInt(pid);
		Comment comment = new Comment()
			.setComment(commentString)
			.setPid(parsedPid)
			.setTime(System.currentTimeMillis());
		Post post = postDAO.findById(parsedPid);
		if (post != null){
			int newId = commentDAO.insert(comment);
			return comment.setId(newId);
		}else{
			JSONObject obj = new JSONObject();
			obj.put("status", "error");
			obj.put("errorMsg", "Post does not exist.");
			throw new BadRequestException(obj);
		}
		
	}

	@PUT
	@Timed
	@Path("/{id}")
	@ApiOperation(value = "Update comment", notes = "Update comment", response = Comment.class)
	public Comment update(@PathParam("id") Integer id, @Valid Comment comment) {
		comment = comment.setId(id);
		commentDAO.update(comment);
		return comment;
	}

	@DELETE
	@Timed
	@Path("/{id}")
	@ApiOperation("Delete comment")
	public void delete(@PathParam("id") Integer id) {
		commentDAO.deleteById(id);
	}
}
