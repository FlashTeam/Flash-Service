package com.friss.service.post;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.friss.service.comment.Comment;

import javax.validation.constraints.NotNull;

public class Post {
	
	private final int MAX_COMMENT_LENGTH = 1000;
	private final int MAX_USERNAME_LENGTH = 60;

	@JsonProperty
	private Integer id;

	@NotNull
	@JsonProperty
	private Long time;

	@JsonProperty
	private Long timeout;

	@JsonProperty
	private String image;

	@NotNull
	@JsonProperty
	private Integer vote;

	@JsonProperty
	private String comment;

	@JsonProperty
	private Float longitude;

	@JsonProperty
	private Float latitude;

	@JsonProperty
	private String username;
	
	@JsonProperty
	private List<Comment> comments = Collections.emptyList();
	
	/*=========================================================================
	 * Standard Java Overrides
	 *=======================================================================*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Post [id=" + id + ", time=" + time + ", timeout=" + timeout
				+ ", image=" + image + ", vote=" + vote + ", comment="
				+ comment + ", longitude=" + longitude + ", latitude="
				+ latitude + ", username=" + username + ", comments="
				+ comments + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((vote == null) ? 0 : vote.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (timeout == null) {
			if (other.timeout != null)
				return false;
		} else if (!timeout.equals(other.timeout))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (vote == null) {
			if (other.vote != null)
				return false;
		} else if (!vote.equals(other.vote))
			return false;
		return true;
	}
	
	/*=========================================================================
	 * Getters/Setters
	 *=======================================================================*/

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public Post setId(Integer id) {
		this.id = id;
		return this;
	}

	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public Post setTime(Long time) {
		this.time = time;
		return this;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param blob
	 *            the image to set
	 */
	public Post setImage(String image) {
		this.image = image;
		return this;
	}

	/**
	 * @return the vote
	 */
	public Integer getVote() {
		return vote;
	}

	/**
	 * @param vote
	 *            the vote to set
	 */
	public Post setVote(Integer vote) {
		this.vote = vote;
		return this;
	}
	
	/**
	 * Increases vote by 1
	 */
	public Post upVote(){
		this.vote++;
		return this;
	}
	
	/**
	 * Decreases vote by 1
	 */
	public Post downVote(){
		this.vote--;
		return this;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public Post setComment(String comment) {
		if(comment.length() > MAX_COMMENT_LENGTH){
			comment = comment.substring(0, MAX_COMMENT_LENGTH);
		}
		this.comment = comment;
		return this;
	}

	/**
	 * @return the timeout
	 */
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public Post setTimeout(Long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return the longitude
	 */
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public Post setLongitude(Float longitude) {
		this.longitude = longitude;
		return this;
	}

	/**
	 * @return the latitude
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public Post setLatitude(Float latitude) {
		this.latitude = latitude;
		return this;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public Post setUsername(String username) {
		if(username.length() > MAX_USERNAME_LENGTH){
			username = username.substring(0, MAX_USERNAME_LENGTH);
		}
		this.username = username;
		return this;
	}

	/**
	 * @return the comments
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public Post setComments(List<Comment> comments) {
		this.comments = comments;
		return this;
	}
	
	

}
