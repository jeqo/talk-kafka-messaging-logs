package io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.github.jeqo.talk.kafka.streams_connect.domain.model.TweetRepository;
import io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka.util.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.stream.Collectors.toList;

/**
 * Created by jeqo on 14.02.17.
 */
@Startup
@Singleton
public class KafkaTweetRepository implements TweetRepository {


    public static final String STORE_NAME = "tweets-store";

    KafkaStreams streams;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-repo");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "zookeeper:2181");
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://schema-registry:8081");

        KStreamBuilder builder = new KStreamBuilder();
        builder.table(Serdes.Long(), Serdes.String(), "processed-tweets", STORE_NAME);

        streams = new KafkaStreams(builder, props);

        streams.start();
    }

    @Override
    public List<String> getTweets(String filter) {
        List<String> tweetsResult = new ArrayList<>();
        final ReadOnlyKeyValueStore<Long, String> kvs = streams.store(
                STORE_NAME,
                QueryableStoreTypes.<Long, String>keyValueStore());
        kvs.all().forEachRemaining(kv -> tweetsResult.add(kv.value));
        return tweetsResult
                .stream()
                .filter(s -> s.toLowerCase().contains(filter.toLowerCase()))
                .collect(toList());
    }
}
