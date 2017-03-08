package io.github.jeqo.talk.kafka.producers;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.LongStream;

import static java.lang.System.out;

/**
 *
 */
public class MainProducer {
    private final static Configuration configuration = ConfigurationProvider.getConfiguration();

    public static void main(String[] args) {
        try {
            final String producerPropertiesPath = configuration
                    .getOrDefault("PRODUCER_PROPS_PATH", "/config/producer.properties");

            Properties properties = new Properties();
            properties.load(
                    new FileInputStream(
                            new File(producerPropertiesPath)));

            Producer<String, byte[]> producer = new KafkaProducer<>(properties);

            final String pushGatewayServer = configuration.getOrDefault("PUSH_GATEWAY_SERVER", "pushgateway:9091");
            final String metricName = configuration.getOrDefault("METRIC_NAME", "kafka_producer");

            final CollectorRegistry registry = new CollectorRegistry();
            final Histogram requestLatency = Histogram
                    .build()
                    .help("Latency in seconds")
                    .name(metricName)
                    .register(registry);

            final String topic = configuration.getOrDefault("TOPIC", "topic1");
            final Long wait = configuration.getOrDefault("WAIT_SEG", Long.class, 1L);
            final Long maxMessages = configuration.getOrDefault("MAX_MESSAGES", Long.class, 100L);

            LongStream
                    .rangeClosed(1, maxMessages)
                    .boxed()
                    .map(number ->
                            new ProducerRecord<>(
                                    topic,
                                    number.toString(), //Key
                                    generateMessage(number))) //Value
                    .forEach(record -> {
                        Histogram.Timer requestTimer = requestLatency.startTimer();
                        try {
                            RecordMetadata metadata = producer.send(record).get();
                            out.printf(
                                    "Record saved: %s-%s offset=%s, timestamp=%s",
                                    metadata.topic(),
                                    metadata.partition(),
                                    metadata.offset(),
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(metadata.timestamp()),
                                            ZoneId.systemDefault()));
                            out.println();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                requestTimer.observeDuration();
                                PushGateway pg = new PushGateway(pushGatewayServer);
                                pg.pushAdd(registry, "kafka-producers");
                                Thread.sleep(wait * 1000);
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            producer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static byte[] generateMessage(Long number) {
        final Integer messageSize = configuration.getOrDefault("MESSAGE_SIZE", Integer.class, -1);

        if (messageSize < 0) {
            return String.format("record-%s", number).getBytes();
        } else {
            return new byte[messageSize];
        }
    }
}
