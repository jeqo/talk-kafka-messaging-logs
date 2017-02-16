package io.github.jeqo.talk.kafka.producers;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * Created by jeqo on 16.02.17.
 */
public class RecordsProducer {

    public static void produce(String metricName, Producer producer, String topic) {
        Configuration config = ConfigurationProvider.getConfiguration();

        String pushGatewayServer = config.getOrDefault("PUSH_GATEWAY_SERVER", "localhost:9091");

        CollectorRegistry registry = new CollectorRegistry();
        final Histogram requestLatency = Histogram
                .build()
                .help("Latency in seconds")
                .name(metricName)
                .register(registry);

        IntStream.rangeClosed(1, 100).boxed()
                .map(number ->
                        new ProducerRecord<>(
                                topic,
                                number, //Key
                                String.format("record-%s", number))) //Value
                .forEach(record -> {
                    Histogram.Timer requestTimer = requestLatency.startTimer();
                    try {
                        producer.send(record);
                    } finally {
                        try {
                            requestTimer.observeDuration();
                            System.out.println("Sent");
                            Thread.sleep(1000);
                            PushGateway pg = new PushGateway(pushGatewayServer);
                            pg.pushAdd(registry, "kafka-producer-ack");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
