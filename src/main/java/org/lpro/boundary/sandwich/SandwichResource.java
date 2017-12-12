package org.lpro.boundary.sandwich;

import org.lpro.entity.Sandwich;

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
@Path("sandwichs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SandwichResource {

    @Inject
    SandwichManager sandwichManager;

    @GET
    public Response getSandwichs() {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("sandwichs", getSandwichsList())
                .build();
        return Response.ok(json).build();
    }

    @GET
    @Path("{id}")
    public Response getSandwich(@PathParam("id") long id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(sandwichManager.findById(id))
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getSandwichs(@QueryParam("type") String ptype) {
        return Optional.ofNullable(sandwichManager.findByType(ptype))
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createSandwich(@Valid Sandwich sandwich, @Context UriInfo uriInfo) {
        Sandwich newOne = this.sandwichManager.save(sandwich);
        long id = newOne.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteSandwich(@PathParam("id") long id) {
        this.SandwichManager.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Sandwich updateSandwich(@PathParam("id") long id, Sandwich sandwich) {
        sandwich.setId(id);
        return this.sandwichManager.save(sandwich);
    }

    private JsonObject sandwich2Json(Sandwich sandwich) {
        return Json.createObjectBuilder()
                .add("type", "resource")
                .add("sandwich", buildJson(sandwich))
                .build();
    }

    private JsonArray getSandwichsList() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.sandwichManager.findAll().forEach((c) -> {
            jab.add(buildJson(c));
        });
        return jab.build();
    }



    private JsonObject buildJson(Sandwich sandwich) {
        return Json.createObjectBuilder()
                .add("id", sandwich.getId())
                .add("nom", sandwich.getNom())
                .add("descr", sandwich.getDescr())
                .add("type_pain", sandwich.getType_pain())
                .add("img", sandwich.getImg())
                .build();
    }
}