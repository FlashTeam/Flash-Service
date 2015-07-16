package com.friss.service.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import com.sun.jersey.api.Responses;

@SuppressWarnings("serial")
public class BadRequestException extends WebApplicationException {

	public BadRequestException() {
		super(Responses.notFound().build());
	}

	public BadRequestException(JSONObject message) {
		super(Response.status(Responses.CLIENT_ERROR).entity(message)
				.type("application/json").build());
	}

}
