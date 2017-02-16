package io.github.jeqo.talk.kafka.streams_connect.interfaces.resources;

import io.github.jeqo.talk.kafka.streams_connect.domain.model.TweetRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by jeqo on 14.02.17.
 */
@Path("tweets")
public class TweetsResource {

    @Inject
    TweetRepository tweetRepository;

    @GET
    @Path("{filter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTweets(@PathParam("filter") String filter) {
        return Response
                .ok()
                .entity(new TweetsRepresentation(tweetRepository.getTweets(filter)))
                .build();
    }
}
