package io.github.jeqo.talk.kafka.consumers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Properties;
import java.util.stream.IntStream;

/**
 *
 */
public class KafkaSlowProducer {
    private static final String TOPIC = "parallel-consumers";

    public static void main(String[] args) {
        Configuration config = ConfigurationProvider.getConfiguration();

        String bootstrapServers = config.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        produceRecords(bootstrapServers);
    }

    private static void produceRecords(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<Integer, String> producer = new KafkaProducer<>(properties);

        IntStream.rangeClosed(1, 100).boxed()
                .map(number ->
                        new ProducerRecord<>(
                                TOPIC,
                                number, //Key
                                String.format("record-%s", number))) //Value
                .forEach(record -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    producer.send(record);
                });
        producer.close();
    }
}
