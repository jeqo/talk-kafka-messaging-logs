package io.github.jeqo.talk.kafka.streams_connect;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.jeqo.talk.kafka.streams_connect.domain.model.TweetRepository;
import io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka.KafkaStreamsManager;
import io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka.KafkaTweetProcessorFactory;
import io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka.KafkaTweetRepository;
import io.github.jeqo.talk.kafka.streams_connect.infrastructure.persistence.kafka.KafkaTweetStoreFactory;
import io.github.jeqo.talk.kafka.streams_connect.interfaces.resources.TweetsResource;
import org.apache.kafka.streams.KafkaStreams;

/**
 *
 */
public class KafkaStreamsTwitterApp extends Application<KafkaStreamsTwitterConfig> {
    @Override
    public void run(KafkaStreamsTwitterConfig kafkaStreamsTwitterConfig, Environment environment) throws Exception {
        final KafkaStreams storeStreams = new KafkaTweetStoreFactory().build();
        environment.lifecycle().manage(new KafkaStreamsManager(storeStreams));
        final TweetRepository tweetRepository = new KafkaTweetRepository(storeStreams);

        final KafkaStreams processorStreams = new KafkaTweetProcessorFactory().build();
        environment.lifecycle().manage(new KafkaStreamsManager(processorStreams));

        final TweetsResource tweetsResource = new TweetsResource(tweetRepository);
        environment.jersey().register(tweetsResource);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<KafkaStreamsTwitterConfig> bootstrap) {
        // nothing to do yet
    }

    public static void main(String[] args) throws Exception {
        new KafkaStreamsTwitterApp().run(args);
    }
}
