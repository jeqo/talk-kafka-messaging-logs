package io.github.jeqo.talk.kafka.producers;

import java.util.Arrays;

/**
 *
 */
public class KafkaProducerUtil {
    public static byte[] createMessage(Integer messageSize) {
        byte[] bytes = new byte[messageSize];
        Arrays.fill(bytes, (byte) 1);
        return bytes;
    }
}
