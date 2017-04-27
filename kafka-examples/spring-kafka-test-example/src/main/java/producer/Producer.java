package producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by mzagar on 26/04/17.
 */
public class Producer {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        KafkaTemplate<String, String> kafkaTemplate = createKafkaTemplate();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://stream.meetup.com/2/rsvps").openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    System.out.println("Sending: " + inputLine);
                    ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send("meetup", inputLine);
                    System.out.println(result.get().getRecordMetadata().toString());
                } catch (Exception e) {
                    System.err.println("error sending: " + e.getMessage());
                    Thread.sleep(1000);
                }
            }
        }
    }

    private static KafkaTemplate<String, String> createKafkaTemplate() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);

        return new KafkaTemplate<>(producerFactory);
    }
}
