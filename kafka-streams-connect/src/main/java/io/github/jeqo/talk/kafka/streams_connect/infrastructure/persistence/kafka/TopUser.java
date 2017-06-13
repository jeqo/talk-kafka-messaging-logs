package io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka;

import java.util.List;

/**
 *
 */
public class TopUser {
    private String username;
    private List<String> tweets;

    public TopUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getTweets() {
        return tweets;
    }

    public void setTweets(List<String> tweets) {
        this.tweets = tweets;
    }
}
