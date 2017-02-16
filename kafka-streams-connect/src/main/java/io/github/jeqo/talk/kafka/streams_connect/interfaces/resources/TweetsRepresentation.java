package io.github.jeqo.talk.kafka.streams_connect.interfaces.resources;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by jeqo on 16.02.17.
 */
@XmlRootElement
public class TweetsRepresentation {
    private List<String> tweets;

    public TweetsRepresentation() {
    }

    public TweetsRepresentation(List<String> tweets) {
        this.tweets = tweets;
    }

    public List<String> getTweets() {
        return tweets;
    }

    public void setTweets(List<String> tweets) {
        this.tweets = tweets;
    }
}
