package org.lpro.boundary.commande;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
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

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("commandes")
public class CommandeResource {

    @Inject
    CommandeManager commandeManager;
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
                        .entity(newCommande)
                        .build();
            }
        }catch(ParseException e){ }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private JsonObject buildCommandeObject(Commande c) {
        return Json.createObjectBuilder()
                .add("commande", buildJsonForCommande(c))
                .build();
    }

    private JsonObject buildJsonForCommande(Commande c) {
        return Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nom_client", c.getNom())
                .add("mail_client", c.getMail())
                .add("livraison", buildJsonForLivraison(c))
                .add("token", c.getToken())
                .build();
    }

    private JsonObject buildJsonForLivraison(Commande c) {
        return Json.createObjectBuilder()
                .add("date", c.getDateLivraison())
                .build();
    }
}
