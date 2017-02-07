package io.github.jeqo.talk.kafka.record;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Created by jeqo on 06.02.17.
 */
public class ProduceConsumeLongByteArrayRecord {

    private static final String TOPIC = "bytes-kv";

    public static void main(String[] args) {
        Configuration config = ConfigurationProvider.getConfiguration();

        String bootstrapServers = config.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        produceRecords(bootstrapServers);

        consumeRecords(bootstrapServers);
    }

    private static void produceRecords(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        Producer<Long, byte[]> producer = new KafkaProducer<>(properties);

        LongStream.rangeClosed(1, 100).boxed()
                .map(number ->
                        new ProducerRecord<>(
                                TOPIC,
                                number,
                                String.format("record-%s", number.toString()).getBytes()))
                .forEach(record -> producer.send(record));
        producer.close();
    }

    private static void consumeRecords(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "byte-array-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());

        Consumer<Long, byte[]> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList(TOPIC));

        ConsumerRecords<Long, byte[]> records = consumer.poll(10000);

        for (ConsumerRecord<Long, byte[]> record : records)
            System.out.printf("key = %s value = %s%n", record.key(), new String(record.value()));

        consumer.close();
    }
}
