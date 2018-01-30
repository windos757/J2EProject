package org.lpro.boundary.sandwich;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.lpro.entity.Sandwich;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;
import org.lpro.entity.Category;
import org.lpro.entity.Taille;

@Stateless
@Path("sandwichs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "SANDWICH")
public class SandwichResource {

    @Inject
    SandwichManager sandwichManager;

    
    @GET
    @Path("{id}")
    @ApiOperation(value = "Récupère un sandwich", notes = "Renvoie le JSON associé au sandwich")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getSandwich(@PathParam("id") long id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(sandwichManager.findById(id))
                .map(c -> Response.ok(buildJson(c)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("{id}/categories")
    public Response getCategories(@PathParam("id") long id) {
        return Optional.ofNullable(this.sandwichManager.findById(id))
                .map(s -> Response.ok(buildCategories(s)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    private JsonArrayBuilder buildArrayCategories(Sandwich s){
        JsonArrayBuilder categories = Json.createArrayBuilder();
        s.getCategory().forEach((c) -> {
            categories.add(buildJsonForCategory(c));
        });
        return categories;
    }

    private JsonObject buildCategories(Sandwich s) {
        JsonArrayBuilder categories = this.buildArrayCategories(s);
        return Json.createObjectBuilder()
                .add("categories", categories.build())
                .build();
    }

    private JsonObject buildJsonForCategory(Category category) {
        return Json.createObjectBuilder()
                .add("id", category.getId())
                .add("nom", category.getNom())
                .add("descr", category.getDescr())
                .build();
    }

    @GET
    @Path("{id}/tailles")
    public Response getTailles(@PathParam("id") long id) {
        return Optional.ofNullable(this.sandwichManager.findById(id))
                .map(s -> Response.ok(buildTailles(s)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    private JsonArrayBuilder buildArrayTailles(Sandwich s){
        JsonArrayBuilder tailles = Json.createArrayBuilder();
        s.getTailles().forEach((t) -> {
            tailles.add(buildJsonForTaille(t));
        });
        return tailles;
    }

    private JsonObject buildTailles(Sandwich s) {
        JsonArrayBuilder tailles = this.buildArrayTailles(s);
        return Json.createObjectBuilder()
                .add("tailles", tailles.build())
                .build();
    }

    private JsonObject buildJsonForTaille(Taille taille) {
        return Json.createObjectBuilder()
                .add("id", taille.getId())
                .add("nom", taille.getNom())
                .add("prix", taille.getPrix())
                .build();
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
                .add("img", ((s.getImg() == null) ? "" : s.getImg()))
                .add("tailles", buildArrayTailles(s).build())
                .add("categories", buildArrayCategories(s).build())
                .build();

        return Json.createObjectBuilder()
                .add("sandwich", details)
                .add("links", Json.createObjectBuilder()
                        .add("self", Json.createObjectBuilder()
                                .add("href", "/sandwichs/"+s.getId())
                                .build())
                        .add("tailles", Json.createObjectBuilder()
                                .add("href","/sandwichs/"+s.getId()+"/tailles")
                                .build())
                        .add("categories", Json.createObjectBuilder()
                                .add("href","/sandwichs/"+s.getId()+"/categories")
                                .build())
                        .build())
                .build();
    }

}