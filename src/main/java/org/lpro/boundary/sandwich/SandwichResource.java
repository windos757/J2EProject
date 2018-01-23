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

//    @GET
//    public Response getSandwichs() {
//        JsonObject json = Json.createObjectBuilder()
//                .add("type", "collection")
//                .add("sandwichs", getSandwichsList())
//                .build();
//        return Response.ok(json).build();
//    }
    
    @GET
    @Path("{id}")
    public Response getSandwich(@PathParam("id") long id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(sandwichManager.findById(id))
                .map(c -> Response.ok(c).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getSandwichs(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int nbPerPage,
            @QueryParam("t") String ptype,
            @DefaultValue("0") @QueryParam("img") int img
    ) {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("meta", this.sandwichManager.getMetaPerPage(-1, ptype, img, page, nbPerPage))
                .add("sandwichs", this.getSandwichsList(ptype,img, page, nbPerPage))
                .build();
        return Response.ok(json).build();
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
        this.sandwichManager.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    public Sandwich updateSandwich(@PathParam("id") long id, Sandwich sandwich) {
        sandwich.setId(id);
        return this.sandwichManager.save(sandwich);
    }


    private JsonArray getSandwichsList(String ptype, int img, int page, int nbPerPage) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.sandwichManager.find(ptype, img,true, page, nbPerPage).forEach((c) -> {
            jab.add(buildJson(c));
        });
        return jab.build();
    }


    private JsonObject buildJson(Sandwich s) {
        JsonObject details = Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("description", s.getDescr())
                .add("pain", s.getType_pain())
                .build();

        JsonObject href = Json.createObjectBuilder()
                .add("href", ((s.getImg() == null) ? "" : s.getImg()))
                .build();

        JsonObject self = Json.createObjectBuilder()
                .add("self", href)
                .build();

        return Json.createObjectBuilder()
                .add("sandwich", details)
                .add("links", self)
                .build();
    }

    private JsonObject sandwich2Json(Sandwich s) {
        return Json.createObjectBuilder()
                .add("type", "resource")
                .add("sandwich", Json.createObjectBuilder()
                        .add("id", s.getId())
                        .add("nom", s.getNom())
                        .add("description", s.getDescr())
                        .add("pain", s.getType_pain())
                        .build())
                .add("links", Json.createObjectBuilder()
                        .add("self", Json.createObjectBuilder()
                                .add("href", ((s.getImg() == null) ? "" : s.getImg()))
                                .build())
                        .build())
                .build();
    }
}