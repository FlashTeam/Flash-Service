package com.friss.service.resources;

import java.io.InputStream;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.codahale.metrics.annotation.Timed;
import com.friss.service.comment.Comment;
import com.friss.service.dao.CommentDAO;
import com.friss.service.dao.PostDAO;
import com.friss.service.exceptions.BadRequestException;
import com.friss.service.exceptions.ObjectNotFoundException;
import com.friss.service.post.Post;
import com.friss.service.post.PostBuilder;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/posts")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Api("/posts")
public class PostResource {

	PostDAO postDAO;
	CommentDAO commentDAO;

	/*=======================================================================
	 * Resource Functions
	 *=======================================================================*/
	
	public PostResource(PostDAO postDAO, CommentDAO commentDAO) {
		this.postDAO = postDAO;
		this.commentDAO = commentDAO;
	}

	/**
	 * Deletes all expired posts from the db and returns the rest.
	 * 
	 * @return all non-expired posts
	 */
	@GET
	@Timed
	@ApiOperation("Get all posts")
	public List<Post> getAll() {
		return postDAO.getAll(System.currentTimeMillis());
	}

	/**
	 * Delete all expired posts from the db and return the posts that fit the
	 * query
	 * 
	 * @param query
	 * @return the list of non-expired posts that fit the query
	 */
	@GET
	@Timed
	@Path("/search/{query}")
	@ApiOperation("Search posts by path param")
	public List<Post> search(@PathParam("query") String query) {
		postDAO.deleteOld(System.currentTimeMillis());
		return postDAO.search(query);
	}
	
	/**
	 * Up vote this post if it is valid. If it is expired, delete the post.
	 * @param id
	 * @return the post that was updated
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Timed
	@Path("/{id}/vote/up")
	@ApiOperation("Up vote this post")
	public Post upVote(@PathParam("id") Integer id){
		Post post = postDAO.findById(id);
		if(post != null && post.getTimeout() > System.currentTimeMillis())
		{
			post.upVote();
			postDAO.update(post);
			return post;
		}
		else if(post != null)/* timeout exceeded */
		{
			postDAO.deleteById(post.getId());
		}
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("status", "error");
		obj.put("errorMsg", "Post not found.");
		throw new ObjectNotFoundException(obj);
	}
	
	/**
	 * Down vote this post if it is valid. If it is expired, delete it.
	 * @param id
	 * @return the post that was voted on
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Timed
	@Path("/{id}/vote/down")
	@ApiOperation("Down vote this post")
	public Post downVote(@PathParam("id") Integer id){
		Post post = postDAO.findById(id);
		if(post != null && post.getTimeout() > System.currentTimeMillis())
		{
			post.downVote();
			postDAO.update(post);
			return post;
		}
		else if(post != null)/* timeout exceeded */
		{
			postDAO.deleteById(post.getId());
		}
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("status", "error");
		obj.put("errorMsg", "Post not found.");
		throw new ObjectNotFoundException(obj);
	}

	/**
	 * Gets a post and if it is not expired, returns it. If it is expired, the
	 * post gets deleted.
	 * 
	 * @param id
	 * @return non-expired post or null.
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Timed
	@Path("/{id}")
	@ApiOperation(value = "Get post by id", notes = "Get post by id value", response = Post.class)
	public Post get(@PathParam("id") Integer id) {
		Post post = postDAO.findById(id);

		if (post != null && post.getTimeout() > System.currentTimeMillis()) {
			List<Comment> comments = commentDAO.getAllPostComments(id);
			post.setComments(comments);
			return post;
		} else {
			if (post != null) {
				postDAO.deleteById(post.getId());
			}
			JSONObject obj = new JSONObject();
			obj.put("id", id);
			obj.put("status", "error");
			obj.put("errorMsg", "Post not found.");
			throw new ObjectNotFoundException(obj);
		}
	}

	@PUT
	@Timed
	@Path("/{id}")
	@ApiOperation(value = "Update post", notes = "Update post", response = Post.class)
	public Post update(@PathParam("id") Integer id, @Valid Post post) {
		post = post.setId(id);
		postDAO.update(post);
		return post;
	}

	@DELETE
	@Timed
	@Path("/{id}")
	@ApiOperation("Delete post")
	public void delete(@PathParam("id") Integer id) {
		postDAO.deleteById(id);
	}

	/**
	 * Add form fields here as FormDataParams. Save the image, create a new
	 * post, and return it.
	 * 
	 * @param timeoutString
	 *            - timeout is transformed from a difference to an absolute time
	 * @param longitudeString
	 * @param latitudeString
	 * @param comment
	 * @param username
	 * @param fileInputStream
	 * @param contentDispositionHeader
	 * @return post the post that was created
	 * @return null returns null on failure
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Timed
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Create post", notes = "Create post", response = Post.class)
	public Post add(
			/* Form Inputs */
			@FormDataParam("timeout") final String timeout,
			@FormDataParam("longitude") final String longitude,
			@FormDataParam("latitude") final String latitude,
			@FormDataParam("comment") final String comment,
			@FormDataParam("username") final String username,
			@FormDataParam("file") final InputStream file,
			@FormDataParam("file") final FormDataBodyPart body) 
	{
		/* Use the builder to try constructing a post from the inputs */
		String mimeType = body.getMediaType().toString();
		Post post = PostBuilder.Build(timeout, longitude, latitude,
				comment, username, file, mimeType);
		
		/* If the post is valid, save it */
		if(post != null)
		{
			int id = postDAO.insert(post);
			post.setId(id);
		}
		else/* throw exception error if there is no post */
		{
			System.out.println(PostBuilder.getErrors().toString());
			JSONObject obj = new JSONObject();
			obj.put("status", "error");
			/* add all errors */
			Integer i = 1;
			for(String error : PostBuilder.getErrors())
			{
				obj.put("Error " + i.toString(), error);
				i++;
			}
			/* throw the exception */
			throw new BadRequestException(obj);
		}
		return post;
	}
		


}
