package org.lpro.boundary.commande;

import io.swagger.annotations.Api;
import org.lpro.entity.Sandwich;
import org.lpro.entity.CommandeItem;
import org.lpro.entity.Taille;
import org.lpro.boundary.sandwich.*;
import org.lpro.boundary.taille.*;
import org.lpro.boundary.commandeItem.*;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.lpro.entity.Commande;
import java.sql.Timestamp;
import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("commandes")
@Api(value = "COMMANDE")
public class CommandeResource {

    @Inject
    CommandeManager commandeManager;
    @Inject
    SandwichManager sandwichManager;
    @Inject
    TailleManager tailleManager;
    @Inject
    CommandeItemManager commandeItemManager;
    @Context
    UriInfo uriInfo;

    @GET
    @Path("/{commandeId}")
    public Response getOneCommande(@PathParam("commandeId") String commandeId,
                                   @DefaultValue("") @QueryParam("token") String tokenParam,
                                   @DefaultValue("") @HeaderParam("X-lbs-token") String tokenHeader) {
        // on cherche la commande
        Commande cmde = this.commandeManager.findById(commandeId);
        // la commande n'existe pas
        if(cmde == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // a-t-on un token ?
        if(tokenParam.isEmpty() && tokenHeader.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        // token présent, valide ?
        String token = (tokenParam.isEmpty()) ? tokenHeader : tokenParam;
        Boolean isTokenValide = cmde.getToken().equals(token);
        if (!isTokenValide) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            return Response.ok(buildCommandeObject(cmde)).build();
        }
    }

    @POST
    public Response addCommande(@Valid Commande commande,@Context UriInfo uriInfo) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            sdf.setTimeZone(TimeZone.getDefault());

            Date current = Date.from(LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

            Date commandeDate = sdf.parse(commande.getDateLivraison() + " " + commande.getHeureLivraison());

            Timestamp currentTimestamp = new Timestamp(current.getTime());
            Timestamp commandeDateTimestamp = new Timestamp(commandeDate.getTime());

            if (currentTimestamp.before(commandeDateTimestamp)){
                Commande newCommande = this.commandeManager.save(commande);
                URI uri = uriInfo.getAbsolutePathBuilder().path(newCommande.getId()).build();
                return Response.created(uri)
                        .entity(buildCommandeResponse(newCommande))
                        .build();
            }
        }catch(ParseException e){ }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/{commandeId}/add")
    public Response addSandwich(@PathParam("commandeId") String commandeId,
                                @DefaultValue("") @QueryParam("token") String tokenParam,
                                @DefaultValue("") @HeaderParam("X-lbs-token") String tokenHeader,
                                @Valid CommandeItem commandeItem) {

        Commande c = this.commandeManager.findById(commandeId);
        Set<CommandeItem> commande = c.getCommandeItem();

        boolean b = commande.stream().anyMatch((item) -> {
            if(item.getSandwich() == commandeItem.getSandwich() && item.getTaille() == commandeItem.getTaille()){
                commandeItem.setQuantity(item.getQuantity() + commandeItem.getQuantity());
                commandeItem.setId(item.getId());
                item.setQuantity(item.getQuantity() + commandeItem.getQuantity());
                return true ;
            } else {
                return false;
            }
        });
        if(!b){
            CommandeItem newItem = this.commandeItemManager.save(commandeItem);
            commande.add(newItem);
        }else{
            CommandeItem ci = this.commandeItemManager.update(commandeItem);
        }

        c.setCommandeItem(commande);
        Commande comm = this.commandeManager.update(c);

        URI uri = uriInfo.getAbsolutePathBuilder().path(comm.getId()).build();
        return Response.created(uri)
                .entity(buildCommandeObject(comm))
                .build();
    }

    private JsonObject buildCommandeObject(Commande c) {
        return Json.createObjectBuilder()
                .add("commande", buildJsonForCommande(c))
                .build();
    }

    private JsonObject buildCommandeResponse(Commande c){
        return Json.createObjectBuilder()
                .add("commande", buildJsonForResponse(c))
                .build();
    }

    private JsonObject buildJsonForResponse(Commande c){
        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("mail_client", c.getMail())
                .add("livraison", buildJsonForLivraison(c))
                .add("id", c.getId())
                .add("token", c.getToken())
                .build();
    }

    private JsonObject buildJsonForCommande(Commande c) {
        return Json.createObjectBuilder()
                .add("nom_client", c.getNom())
                .add("mail_client", c.getMail())
                .add("livraison", buildJsonForLivraison(c))
                .add("id", c.getId())
                .add("token", c.getToken())
                .add("sandwichs", buildArraySandwichs(c))
                .build();
    }

    private JsonObject buildJsonForLivraison(Commande c) {
        return Json.createObjectBuilder()
                .add("date", c.getDateLivraison())
                .add("heure", c.getHeureLivraison())
                .build();
    }

    private JsonArrayBuilder buildArraySandwichs(Commande c){
        JsonArrayBuilder sandwichs = Json.createArrayBuilder();
        c.getCommandeItem().forEach((s) -> {
            sandwichs.add(buildJsonForSandwich(s));
        });
        return sandwichs;
    }


    private JsonObject buildJsonForSandwich(CommandeItem s) {
        Sandwich sand = this.sandwichManager.findById(s.getSandwich());
        Taille t = this.tailleManager.findById(s.getTaille());
        JsonObject details = Json.createObjectBuilder()
                .add("nom", sand.getNom())
                .add("description", sand.getDescription())
                .add("pain", sand.getType_pain())
                .build();

        JsonObject href = Json.createObjectBuilder()
                .add("href", ((sand.getImg() == null) ? "" : sand.getImg()))
                .build();

        JsonObject self = Json.createObjectBuilder()
                .add("self", href)
                .build();

        return Json.createObjectBuilder()
                .add("sandwich", details)
                .add("links", self)
                .add("taille", t.getNom())
                .add("quantité", s.getQuantity())
                .build();
    }
}
