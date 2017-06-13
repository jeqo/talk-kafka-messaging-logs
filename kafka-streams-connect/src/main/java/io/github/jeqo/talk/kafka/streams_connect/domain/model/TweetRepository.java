package io.github.jeqo.talk.kafka.streams_connect.domain.model;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface TweetRepository {

    List<String> getTweets(String filter);
}
