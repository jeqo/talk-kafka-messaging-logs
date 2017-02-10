package io.github.jeqo.talk.kafka.record.avro;

import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by jeqo on 10.02.17.
 */
public class UserAvroSerdes {

    public static byte[] serialize(User user) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<User> writer = new SpecificDatumWriter<>(User.getClassSchema());
            writer.write(user, encoder);
            encoder.flush();

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error", e);
        }
    }

    public static User deserialize(byte[] record) {
        try {
            DatumReader<User> reader = new SpecificDatumReader<>(User.getClassSchema());
            Decoder decoder = DecoderFactory.get().binaryDecoder(record, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new RuntimeException("Error", e);
        }
    }
}
