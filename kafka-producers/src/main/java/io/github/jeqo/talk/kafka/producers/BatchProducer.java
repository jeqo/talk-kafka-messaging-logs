package io.github.jeqo.talk.kafka.producers;

import org.apache.kafka.clients.producer.*;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.LongStream;

import static java.lang.System.out;

/**
 *
 */
public class BatchProducer {
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

            final String topic = configuration.getOrDefault("TOPIC", "topic1");
            final Long wait = configuration.getOrDefault("WAIT_SEG", Long.class, 1L);
            final Long maxMessages = configuration.getOrDefault("MAX_MESSAGES", Long.class, 100L);
            final Integer messageSize = configuration.getOrDefault("MESSAGE_SIZE", Integer.class, -1);

            out.printf(
                    "Producer info: topic: %s max_messages: %s wait_by_record: %s message_size: %s",
                    topic,
                    maxMessages,
                    wait,
                    messageSize);
            out.println();

            LongStream
                    .rangeClosed(1, maxMessages)
                    .boxed()
                    .map(number ->
                            new ProducerRecord<>(
                                    topic,
                                    generateKey(number), //Key
                                    generateValue(number))) //Value
                    .forEach(record -> producer.send(record, (recordMetadata, e) -> {

                    }));

            producer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateKey(Long number) {
        final Long keyMax = configuration.getOrDefault("KEY_MAX", Long.class, -1L);
        final Long maxMessages = configuration.getOrDefault("MAX_MESSAGES", Long.class, 100L);
        if (keyMax > 1) {
            final Long rebased = ((maxMessages - 1) * (number - 1) / (keyMax - 1)) + 1;
            return rebased.toString();
        } else if (keyMax < 0) {
            return number.toString();
        } else {
            return "KEY";
        }
    }

    private static byte[] generateValue(Long number) {
        final Integer messageSize = configuration.getOrDefault("MESSAGE_SIZE", Integer.class, -1);

        if (messageSize < 0) {
            return String.format("record-%s", number).getBytes();
        } else {
            byte[] bytes = new byte[messageSize];
            Arrays.fill(bytes, (byte) 1);
            return bytes;
        }
    }
}
