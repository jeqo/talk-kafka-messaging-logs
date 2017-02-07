package io.github.jeqo.talk.kafka.record;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.IntStream;

/**
 * Created by jeqo on 06.02.17.
 */
public class ProduceConsumeIntegerStringRecord {

    private static final String TOPIC = "string-kv";

    public static void main(String[] args) {
        Configuration config = ConfigurationProvider.getConfiguration();

        String bootstrapServers = config.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        produceRecords(bootstrapServers);

        consumeRecords(bootstrapServers);
    }

    private static void produceRecords(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<Integer, String> producer = new KafkaProducer<>(properties);

        IntStream.rangeClosed(1, 100).boxed()
                .map(number ->
                        new ProducerRecord<>(
                                TOPIC,
                                number,
                                String.format("record-%s", number)))
                .forEach(record -> producer.send(record));
        producer.close();
    }

    private static void consumeRecords(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "string-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<Integer, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList(TOPIC));

        ConsumerRecords<Integer, String> records = consumer.poll(10000);

        for (ConsumerRecord<Integer, String> record : records)
            System.out.printf("key = %s value = %s%n", record.key(), record.value());

        consumer.close();
    }
}
