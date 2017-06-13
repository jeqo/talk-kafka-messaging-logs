package io.github.jeqo.talk.kafka.streams_connect.interfaces.resources;

import java.util.List;

/**
 *
 */
public class TweetsRepresentation {
    private List<String> tweets;

    public TweetsRepresentation() {
    }

    TweetsRepresentation(List<String> tweets) {
        this.tweets = tweets;
    }

    public List<String> getTweets() {
        return tweets;
    }

    public void setTweets(List<String> tweets) {
        this.tweets = tweets;
    }
}
