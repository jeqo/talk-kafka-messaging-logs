package io.github.jeqo.talk.kafka.streams_connect.domain.model;

import java.util.List;
import java.util.Set;

/**
 * Created by jeqo on 14.02.17.
 */
public interface TweetRepository {

    List<String> getTweets(String filter);
}
