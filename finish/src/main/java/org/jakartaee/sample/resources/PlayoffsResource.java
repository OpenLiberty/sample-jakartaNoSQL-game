package org.jakartaee.sample.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jakartaee.sample.model.Playoffs;
import org.jakartaee.sample.model.Ranking;

@Path("/playoffs")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PlayoffsResource {

    @Inject
    Playoffs playoffs;

    @GET
    @Path("/ranking")
    public Ranking getRanking(){
        return playoffs.winnerRanking();
    }
}
