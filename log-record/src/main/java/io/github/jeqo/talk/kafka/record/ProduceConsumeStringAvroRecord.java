package io.github.jeqo.talk.kafka.record;

import io.github.jeqo.talk.kafka.record.avro.User;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                .map(number -> {
                            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
                                DatumWriter<User> writer = new SpecificDatumWriter<>(User.getClassSchema());
                                User user = new User(String.format("user-%s", number.toString()));
                                writer.write(user, encoder);
                                encoder.flush();
                                return new ProducerRecord<>(
                                        TOPIC,
                                        number.toString(),
                                        out.toByteArray());
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                ).forEach(record -> producer.send(record));
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

        for (ConsumerRecord<String, byte[]> record : records) {
            try {
                DatumReader<User> reader = new SpecificDatumReader<>(User.getClassSchema());
                Decoder decoder = DecoderFactory.get().binaryDecoder(record.value(), null);
                User user = reader.read(null, decoder);
                out.printf("key = %s value = %s%n", record.key(), user.getName().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        consumer.close();
    }
}
