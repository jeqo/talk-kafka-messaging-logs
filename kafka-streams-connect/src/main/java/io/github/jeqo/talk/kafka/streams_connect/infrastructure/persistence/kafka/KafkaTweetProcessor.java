package io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka;

import com.eneco.trading.kafka.connect.twitter.Tweet;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka.util.SpecificAvroSerde;
import org.apache.avro.specific.SpecificData;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Properties;

/**
 * Created by jeqo on 14.02.17.
 */
@Startup
@Singleton
public class KafkaTweetProcessor {

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "zookeeper:2181");
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://schema-registry:8081");

        KStreamBuilder builder = new KStreamBuilder();

        builder.stream("tweets")
                .map((k, v) -> {
                    Tweet tweet = (Tweet) SpecificData.get().deepCopy(Tweet.getClassSchema(), v);
                    return new KeyValue<>(tweet.getId(), tweet.getText().toString());
                })
                .to(Serdes.Long(), Serdes.String(), "processed-tweets");

        KafkaStreams streams = new KafkaStreams(builder, props);
        streams.start();
    }
}
