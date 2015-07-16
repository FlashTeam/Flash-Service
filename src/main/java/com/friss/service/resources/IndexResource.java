package com.friss.service.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.wordnik.swagger.annotations.Api;

@Path("/")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Api("/")
public class IndexResource {

	@SuppressWarnings("unchecked")
	@GET
	public Response index() {
		JSONObject obj = new JSONObject();
		obj.put("status", "ok");
		obj.put("msg", "Service online");
		return Response.ok().entity(obj).type("application/json").build();
	}

}
