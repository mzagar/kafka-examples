import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by mzagar on 25/04/17.
 */
public class ProducerExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        KafkaProducer<String, String> producer = createProducer();

        for (int i = 0; i < 100_000_000; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    "spring-topic",
                    String.valueOf(i),
                    UUID.randomUUID() + "-" + String.valueOf(i)
            );

            producer.send(record, callback());
        }

        producer.close();
    }

    private static KafkaProducer<String, String> createProducer() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");

        producerProps.put(ProducerConfig.RETRIES_CONFIG, "100");
        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "951448");
        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 500);
//        producerProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        producerProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "524288000");
        producerProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "100");

        return new KafkaProducer<>(producerProps);
    }

    private static Callback callback() {
        return (metadata, exception) -> {
            if (exception != null) {
                System.err.println("failed: " + exception.getMessage());
            } else {
                System.out.println(String.format("sent to: part=%s, offset=%s", metadata.partition(), metadata.offset()));
            }
        };
    }
}
