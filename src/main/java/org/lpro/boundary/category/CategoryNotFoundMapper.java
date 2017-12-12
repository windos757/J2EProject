package org.lpro.boundary.category;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CategoryNotFoundMapper implements ExceptionMapper<CategoryNotFound> {

    public Response toResponse(CategoryNotFound exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorJson(exception))
                .build();
    }

    private JsonObject errorJson(CategoryNotFound exception) {
        return Json.createObjectBuilder()
                .add("error", Response.Status.NOT_FOUND.getStatusCode())
                .add("message", exception.getMessage())
                .build();
    }

}
