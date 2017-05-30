package io.github.jeqo.talk.kafka.consumers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jeqo on 13.02.17.
 * https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/
 */
public class KafkaConsumerGroup {
    public static void main(String[] args) {
        int numConsumers = 5;
        String groupId = "consumer-group";
        List<String> topics = Arrays.asList("parallel-consumers");
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<ConsumerLoop> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics);
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ConsumerLoop consumer : consumers) {
                consumer.shutdown();
            }
            executor.shutdown();
            try {
                executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }
}
