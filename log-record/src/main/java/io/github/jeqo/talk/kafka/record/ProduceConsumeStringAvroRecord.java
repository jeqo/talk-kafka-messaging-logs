package io.github.jeqo.talk.kafka.record;

import io.github.jeqo.talk.kafka.record.avro.User;
import io.github.jeqo.talk.kafka.record.avro.UserAvroSerdes;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.IntStream;

import static java.lang.System.out;

/**
 * Created by jeqo on 06.02.17.
 */
public class ProduceConsumeStringAvroRecord {

    private static final String TOPIC = "avro-kv";

    public static void main(String[] args) {
        Configuration config = ConfigurationProvider.getConfiguration();

        String bootstrapServers = config.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");

        produceRecords(bootstrapServers);

        consumeRecords(bootstrapServers);
    }

    private static void produceRecords(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        Producer<String, byte[]> producer = new KafkaProducer<>(properties);

        IntStream.rangeClosed(1, 100).boxed()
                .map(number -> new ProducerRecord<>(
                        TOPIC, //topic
                        number.toString(), //key
                        UserAvroSerdes.serialize(new User(String.format("user-%s", number.toString()))))) //value
                .forEach(record -> producer.send(record));
        producer.close();
    }

    private static void consumeRecords(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());

        Consumer<String, byte[]> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList(TOPIC));

        ConsumerRecords<String, byte[]> records = consumer.poll(10000);

        for (ConsumerRecord<String, byte[]> record : records)
            out.printf(
                    "key = %s value = %s%n",
                    record.key(),
                    UserAvroSerdes.deserialize(record.value()).getName().toString());

        consumer.close();
    }
}
