package io.github.jeqo.poc.akka.streams.kafka.java;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

/**
 * Created by jeqo on 24.01.17.
 */
public class KafkaProducer {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("KafkaProducerSystem");

        final Materializer materializer = ActorMaterializer.create(system);

        final ProducerSettings<byte[], String> producerSettings = ProducerSettings
                .create(system, new ByteArraySerializer(), new StringSerializer())
                //.withProperty("zookeeper", "localhost:2181")
                .withBootstrapServers("localhost:9092");

        CompletionStage<Done> done =
                Source.range(1, 100)
                        .map(n -> n.toString())
                        .map(elem ->
                                new ProducerRecord<byte[], String>(
                                        "topic1-ts",
                                        0,
                                        Instant.now().getEpochSecond(),
                                        null,
                                        elem
                                ))
                        .runWith(Producer.plainSink(producerSettings), materializer);

        done.whenComplete((d, ex) -> System.out.println("sent"));
    }
}