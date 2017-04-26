package com.github.mzagar.kafka.spring.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mzagar on 26/04/17.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0; i < 1_000_000; i++) {
            kafkaTemplate.send("spring-topic", "key" + i, "value " + i);
        }
    }

    private AtomicInteger sum = new AtomicInteger(0);

    @KafkaListener(topics = "spring-topic")
    public void listen(List<ConsumerRecord> cr) throws Exception {
        System.out.println("listen --> size=" + sum.addAndGet(cr.size()));
    }

    @Bean
    public ProducerFactory<?,?> kafkaProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> producerConfig = kafkaProperties.buildProducerProperties();
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, "100");
        return new DefaultKafkaProducerFactory<>(producerConfig);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory(ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        return factory;
    }

}

