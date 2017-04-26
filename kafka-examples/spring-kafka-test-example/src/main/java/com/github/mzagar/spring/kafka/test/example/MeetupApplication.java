package com.github.mzagar.spring.kafka.test.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * https://www.meetup.com/meetup_api/docs/stream/2/rsvps/
 *
 * Created by mzagar on 26/04/17.
 */
@SpringBootApplication
public class MeetupApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeetupApplication.class, args);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory(ConsumerFactory consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<?, ?> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(6);
        return factory;
    }
}

