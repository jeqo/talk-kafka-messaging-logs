package io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka;

import io.github.jeqo.talk.kafka.streams_connect.domain.model.TweetRepository;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class KafkaTweetRepository implements TweetRepository {


    private static final String STORE_NAME = "tweets-store";

    private final KafkaStreams streams;

    public KafkaTweetRepository(KafkaStreams streams) {
        this.streams = streams;
    }

    @Override
    public List<String> getTweets(String filter) {
        List<String> tweetsResult = new ArrayList<>();
        final ReadOnlyKeyValueStore<Long, String> kvs = streams.store(
                STORE_NAME,
                QueryableStoreTypes.<Long, String>keyValueStore());
        kvs.all().forEachRemaining(kv -> tweetsResult.add(kv.value));
        return tweetsResult.stream()
                .filter(s -> s.toLowerCase().contains(filter.toLowerCase()))
                .collect(toList());
    }
}
