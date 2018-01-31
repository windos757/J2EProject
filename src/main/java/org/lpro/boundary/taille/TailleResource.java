package org.lpro.boundary.taille;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.lpro.entity.Taille;

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
import java.util.Set;
import org.lpro.entity.Sandwich;

@Stateless
@Path("tailles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "TAILLE")
public class TailleResource {

    @Inject
    TailleManager TailleManager;

    
    
    @GET
    @ApiOperation(value = "Récupère toutes les tailles", notes = "Renvoie le JSON associé à la collection de tailles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response getTailles() {
        JsonObject json = Json.createObjectBuilder()
                .add("type", "collection")
                .add("tailles", getTaillesList())
                .build();
        return Response.ok(json).build();
    }
    
    @GET
    @Path("{id}")
    @ApiOperation(value = "Récupère la taille", notes = "Renvoie le JSON associé à la taille")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")
        , 
        @ApiResponse(code = 404, message = "Not found")})
    public Response getTaille(@PathParam("id") long id, @Context UriInfo uriInfo) {
        return Optional.ofNullable(TailleManager.findById(id))
                .map(c -> Response.ok(buildJson(c)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("{id}/sandwichs")
    @ApiOperation(value = "Récupère tous les sandwichs associés à la taille", notes = "Renvoie le JSON associé aux sandwichs de la taille")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")
        , 
        @ApiResponse(code = 404, message = "Not found")})
    public Response getSandwichs(@PathParam("id") long id) {
        return Optional.ofNullable(this.TailleManager.findById(id))
                .map(c -> Response.ok(buildSandwichs(c)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    private JsonObject buildSandwichs(Taille t) {
        JsonArrayBuilder sandwichs = Json.createArrayBuilder();
        t.getSandwich().forEach((s) -> {
            sandwichs.add(buildJsonForSandwich(s));
        });
        return Json.createObjectBuilder()
                .add("sandwichs", sandwichs.build())
                .build();
    }
    
    private JsonObject buildJsonForSandwich(Sandwich s) {
        JsonObject details = Json.createObjectBuilder()
                .add("id", s.getId())
                .add("nom", s.getNom())
                .add("description", s.getDescription())
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

    @POST
    @ApiOperation(value = "Créé la taille", notes = "Renvoie le JSON associé à la taille")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response createTaille(@Valid Taille taille, @Context UriInfo uriInfo) {
        Taille newOne = this.TailleManager.save(taille);
        long id = newOne.getId();
        URI uri = uriInfo.getAbsolutePathBuilder().path("/" + id).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Path("{id}")
    @ApiOperation(value = "Supprime la taille", notes = "Renvoie le JSON associé à la taille")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")})
    public Response deleteTaille(@PathParam("id") long id) {
        this.TailleManager.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("{id}")
    @ApiOperation(value = "Modifie la taille ou ajoute si inexistante", notes = "Renvoie le JSON associé à la taille")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Internal server error")})
    public Taille updateTaille(@PathParam("id") long id, Taille taille) {
        taille.setId(id);
        return this.TailleManager.save(taille);
    }

    private JsonObject Taille2Json(Taille taille) {
        return Json.createObjectBuilder()
                .add("type", "resource")
                .add("tailles", buildJson(taille))
                .build();
    }

    private JsonArray getTaillesList() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        this.TailleManager.findAll().forEach((c) -> {
            jab.add(buildJson(c));
        });
        return jab.build();
    }

    private JsonObject buildJson(Taille taille) {
        return Json.createObjectBuilder()
                .add("id", taille.getId())
                .add("nom", taille.getNom())
                .add("prix", taille.getPrix())
                .build();
    }
}