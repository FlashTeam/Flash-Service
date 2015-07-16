package com.friss.service.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import com.sun.jersey.api.Responses;

@SuppressWarnings("serial")
public class ObjectNotFoundException extends WebApplicationException {

	public ObjectNotFoundException() {
		super(Responses.notFound().build());
	}

	public ObjectNotFoundException(JSONObject message) {
		super(Response.status(Responses.NOT_FOUND).entity(message)
				.type("application/json").build());
	}

}
