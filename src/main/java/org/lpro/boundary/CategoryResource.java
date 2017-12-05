package org.lpro.boundary;

import org.lpro.entity.Category;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;

@Stateless
@Path("categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryManager categoryManager;

    @GET
    public Response getCategories() {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("categories", getCategoriesList())
                .build();
        return Response.ok(json).build();
    }

    @GET
    @Path("{id}")
    public Response getCategory(@PathParam("id") long id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(categoryManager.findById(id))
                //.map(c -> Response.ok(category2Json(c)).build())
                .map(c -> Response.ok(c).build())
                //.orElseThrow(() -> new CategoryNotFound("Ressource non disponible " + uriInfo.getPath()))
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createCategory(@Valid Category category, @Context UriInfo uriInfo) {
        Category newOne = this.categoryManager.save(category);
        long id = newOne.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCategory(@PathParam("id") long id) {
        this.categoryManager.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Category updateCategory(@PathParam("id") long id, Category category) {
        category.setId(id);
        return this.categoryManager.save(category);
    }

    private JsonObject category2Json(Category category) {
        return Json.createObjectBuilder()
                .add("type", "resource")
                .add("category", buildJson(category))
                .build();
    }

    private JsonArray getCategoriesList() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.categoryManager.findAll().forEach((c) -> {
            jab.add(buildJson(c));
        });
        return jab.build();
    }

    private JsonObject buildJson(Category category) {
        return Json.createObjectBuilder()
                .add("id", category.getId())
                .add("nom", category.getNom())
                .add("descr", category.getDescr())
                .build();
    }
}