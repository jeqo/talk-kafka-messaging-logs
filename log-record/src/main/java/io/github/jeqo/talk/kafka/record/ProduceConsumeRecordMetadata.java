package io.github.jeqo.talk.kafka.record;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Created by jeqo on 06.02.17.
 */
public class ProduceConsumeRecordMetadata {
    private static final String TOPIC = "record-metadata";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault());

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
                .forEach(record -> {
                    Future<RecordMetadata> result = producer.send(record);
                    try {
                        RecordMetadata metadata = result.get();
                        System.out.printf("ProducerRecord: topic=>%s partition=>%s offset=>%s timestamp=>%s checksum=>%s",
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset(),
                                FORMATTER.format(Instant.ofEpochMilli(metadata.timestamp())),
                                metadata.checksum());
                        System.out.println();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
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

        for (ConsumerRecord<Integer, String> record : records) {
            System.out.printf("key = %s value = %s\t", record.key(), record.value());
            System.out.printf("ProducerRecord: topic=>%s partition=>%s offset=>%s timestamp=>%s checksum=>%s",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    FORMATTER.format(Instant.ofEpochMilli(record.timestamp())),
                    record.checksum());
            System.out.println();
        }
        consumer.close();
    }
}
