package com.friss.service.post;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

/*
 * This class abstracts out the building of a post.  It will take all
 * parameters exactly how they are received through the API.  It will
 * then parse them, verify them, upload the image, and return the
 * post object.
 */
public class PostBuilder {

	/* set in config.yml */
	public static String ImagePath;
	public static long MinTimeout;
	public static long MaxTimeout;
	public static String S3Bucket;
	private static AmazonS3 s3client = new AmazonS3Client(
			new SystemPropertiesCredentialsProvider());

	/* Define a set of valid mime types that can be allowed */
	private static final String[] RAW_VALID_MIME_TYPES = { "image/gif",
			"image/jpeg", "image/png" };
	public static final HashSet<String> VALID_MIME_TYPES = new HashSet<String>(
			Arrays.asList(RAW_VALID_MIME_TYPES));

	/* data is stored in this Post object as it is parsed */
	private static Post product = new Post();
	/* error messages are stored here during construction */
	private static ArrayList<String> errors = new ArrayList<String>();

	/*
	 * =========================================================================
	 * Builder Methods
	 * =======================================================================
	 */

	/**
	 * Initializes the data necessary for the builder to start fresh. This
	 * should be called AFTER building any post
	 */
	public static void init() {
		errors.clear();
	}

	public static Post Build(String timeout, String longitude, String latitude,
			String comment, String username, InputStream file, String mimeType) {
		init();
		Post result = null;
		/* init values that require no logic */
		product.setTime(System.currentTimeMillis()).setVote(0)
				.setComment(comment).setUsername(username);
		/* Init values that must be parsed and verified */
		if (setTimeout(timeout) && setLongitude(longitude)
				&& setLatitude(latitude) && setFile(file, mimeType)) {
			result = product;
		}
		return result;
	}

	/*
	 * =========================================================================
	 * Builder Sub-methods (Setters/Parsers/Verifiers)
	 * ========================================================================
	 */

	/**
	 * Parse the timeout and make it an absolute value (not a difference).
	 * 
	 * @param timeout
	 * @return true if timeout is valid
	 */
	private static boolean setTimeout(String timeout) {
		try {
			Long value = Long.parseLong(timeout);
			/* if timeout is in bounds, store it and return */
			if (MinTimeout <= value && value <= MaxTimeout) {
				/* make the timeout value absolute rather than relative */
				value += System.currentTimeMillis();
				product.setTimeout(value);
				return true;
			} else {
				logError("timeout value " + value.toString()
						+ " is not in range [" + MinTimeout + "," + MaxTimeout
						+ "]");
				return false;
			}

		} catch (NumberFormatException e) {
			logError("numberFormatException in timeout");
			return false;
		}
	}

	/**
	 * Parse the longitude and verify it is valid (-180 <= long <= 180).
	 * 
	 * @param longitude
	 * @return true if longitude is valid
	 */
	private static boolean setLongitude(String longitude) {
		try {
			Float value = Float.parseFloat(longitude);
			/* If the value is in bounds, store the value and return */
			if (Math.abs(value) <= 180) {
				product.setLongitude(value);
				return true;
			} else {
				logError("Longitude value " + value.toString()
						+ " is not in range[-180,180]");
				return false;
			}
		} catch (NumberFormatException e) {
			logError("NumberFormatException in longitude");
			return false;
		}
	}

	/**
	 * Parse the latitude and verify -90 <= lat <= 90.
	 * 
	 * @param latitude
	 * @return true if latitude is valid
	 */
	private static boolean setLatitude(String latitude) {
		try {
			Float value = Float.parseFloat(latitude);
			/* If the value is in bounds, store the value and return */
			if (Math.abs(value) <= 90) {
				product.setLatitude(value);
				return true;
			} else {
				logError("Latitude value " + value.toString()
						+ " is not in range [-90,90]");
				return false;
			}
		} catch (NumberFormatException e) {
			logError("NumberFormatException in latitude");
			return false;
		}
	}

	/**
	 * Verify the file is an allowed type. Find a unique name for the file, and
	 * save the file.
	 * 
	 * @param fileInputStream
	 * @param MimeType
	 * @return
	 */
	private static boolean setFile(InputStream file, String MimeType) {
		if (VALID_MIME_TYPES.contains(MimeType)) {
			String extension = MimeType.substring(MimeType.indexOf('/') + 1,
					MimeType.length());
			String filePath = saveImage(file, extension);
			if (filePath.length() == 0) {
				// Error is logged in saveImage
				return false;
			}
			product.setImage(filePath);
		} else {
			logError("Invalid MimeType: " + MimeType);
			return false;
		}
		return true;
	}

	/*
	 * =========================================================================
	 * Helper Functions
	 * =======================================================================
	 */

	private static void logError(String error) {
		errors.add(error);
	}

	public static ArrayList<String> getErrors() {
		return errors;
	}

	/**
	 * Assume the file is good and save it to the server.
	 * 
	 * @param uploadedInputStream
	 * @return filePath
	 * @return path to the new file on success
	 * @return empty string on failure.
	 */
	private static String saveImage(InputStream inputStream, String ext) {

		File outFile = null;
		try {
			/* create file with unique path */
			File directory = new File(ImagePath);/* save images here */
			outFile = File.createTempFile("post_", "." + ext, directory);
			OutputStream outputStream = new FileOutputStream(outFile);

			/* init data structures */
			int read = 0;
			byte[] bytes = new byte[1024];// 1gb max?

			/* read in the file */
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			/* end the stream */
			outputStream.flush();
			outputStream.close();

			PutObjectRequest newFile = new PutObjectRequest(S3Bucket,
					outFile.getName(), outFile);
			newFile.setCannedAcl(CannedAccessControlList.PublicRead);
			s3client.putObject(newFile);

			/* return the file path */
			return outFile.getName();
		} catch (IOException e) {
			logError("In SaveImage: " + e.toString());
			if ((outFile != null) && outFile.exists()) {
				outFile.delete();
			}
			return "";
		}
	}

}
