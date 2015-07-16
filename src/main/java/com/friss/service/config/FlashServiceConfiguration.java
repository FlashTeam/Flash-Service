package com.friss.service.config;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlashServiceConfiguration extends Configuration {
	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();
	
	@Valid
	@NotNull
	@JsonProperty
	private String imagePath;
	
	@Valid
	@NotNull
	@JsonProperty
	private String s3Bucket;
	
	@Valid
	@NotNull
	@JsonProperty
	private String awsAccessKey;
	
	@Valid
	@NotNull
	@JsonProperty
	private String awsSecretKey;
	
	@Valid
	@NotNull
	@JsonProperty
	private Integer minTimeout;
	
	@Valid
	@NotNull
	@JsonProperty
	private Integer maxTimeout;
	
	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}
	
	/**
	 * @return the s3Bucket
	 */
	public String getS3Bucket() {
		return s3Bucket;
	}
	
	/**
	 * @return the awsAccessKey
	 */
	public String getAWSAccessKey() {
		return awsAccessKey;
	}
	
	/**
	 * @return the awsSecretKey
	 */
	public String getAWSSecretKey() {
		return awsSecretKey;
	}

	/**
	 * @return the minTimeout
	 */
	public Integer getMinTimeout() {
		return minTimeout;
	}

	/**
	 * @return the maxTimeout
	 */
	public Integer getMaxTimeout() {
		return maxTimeout;
	}

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
}
