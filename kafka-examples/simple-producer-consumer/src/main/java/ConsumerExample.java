import com.sun.istack.internal.NotNull;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by mzagar on 25/04/17.
 */
public class ConsumerExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "TestConsumerGroup");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);

        consumer.subscribe(Collections.singleton("example-topic"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("Partitions REVOKED:");
                partitions.forEach(p -> System.out.println(String.format("  topic=%s, partitions=%s", p.topic(), p.partition())));
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("Partitions ASSIGNED:");
                partitions.forEach(p -> System.out.println(String.format("  topic=%s, partitions=%s", p.topic(), p.partition())));
            }
        });

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(10000);

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format("topic = %s, partition = %s, offset = %s, key = %s, value = %s",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value()));
                }

                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        System.err.println("Failed commit: " + offsets);
                    } else {
                        System.out.println("Commited offsets: " + offsets);
                    }
                });
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
