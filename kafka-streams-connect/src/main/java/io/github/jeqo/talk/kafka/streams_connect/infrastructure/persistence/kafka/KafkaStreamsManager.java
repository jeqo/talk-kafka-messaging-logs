package io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka;

import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.streams.KafkaStreams;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class KafkaStreamsManager implements Managed {

    private final static Logger LOGGER = Logger.getLogger(KafkaStreamsManager.class.getName());

    private final KafkaStreams kafkaStreams;

    public KafkaStreamsManager(KafkaStreams kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }

    @Override
    public void start() {
        kafkaStreams.start();
        LOGGER.log(Level.WARNING, "Kafka Streams State: " + kafkaStreams.state());
    }

    @Override
    public void stop() {
        kafkaStreams.close();
    }
}
