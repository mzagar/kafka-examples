package com.github.mzagar.spring.kafka.test.example;

import com.jayway.awaitility.Duration;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by mzagar on 26/04/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaProducerConsumerTest.Config.class)
public class KafkaProducerConsumerTest {
    static final String topic = MeetupItemConsumer.TOPIC;

    @ClassRule
    public static KafkaEmbedded kafkaEmbedded = new KafkaEmbedded(1, false, topic);

    @Autowired
    ZeroGuestsListener zeroGuestsListener;

    @Autowired
    MeetupItemConsumer consumer;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(5000);

        kafkaTemplate.send(topic, nonZeroGuestsJsonMessage());
        kafkaTemplate.send(topic, zeroGuestsJsonMessage());

        await().atMost(Duration.FIVE_SECONDS).until(() -> consumer.getRecordsProcessed() == 2);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(zeroGuestsListener).onZeroGuests(captor.capture());
        verifyNoMoreInteractions(zeroGuestsListener);
        assertThat(captor.getValue().getGuests()).isZero();
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public ZeroGuestsListener zeroGuestsListener() {
            return mock(ZeroGuestsListener.class);
        }

        @Bean
        public KafkaTemplate<String, String> kafkaTemplate() {
            Map<String, Object> config = KafkaTestUtils.producerProps(kafkaEmbedded);
            ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(config);
            return new KafkaTemplate<>(producerFactory);
        }

        @Bean
        public ConsumerFactory consumerFactory() {
            Map<String, Object> config = KafkaTestUtils.consumerProps(
                    "test-group",
                    "true",
                    kafkaEmbedded
            );
            return new DefaultKafkaConsumerFactory(config);
        }
    }

    private String zeroGuestsJsonMessage() {
        return "{\"venue\":{\"venue_name\":\"Pivotal Labs Toronto\",\"lon\":-79.375641,\"lat\":43.649971,\"venue_id\":20351012},\"visibility\":\"public\",\"response\":\"yes\",\"guests\":0,\"member\":{\"member_id\":226447814,\"photo\":\"https:\\/\\/secure.meetupstatic.com\\/photos\\/member\\/a\\/e\\/c\\/thumb_266402796.jpeg\",\"member_name\":\"Nikhil V.\"},\"rsvp_id\":1665365939,\"mtime\":1493231047996,\"event\":{\"event_name\":\"Machine Learning Exposed\",\"event_id\":\"238944252\",\"time\":1493330400000,\"event_url\":\"https:\\/\\/www.meetup.com\\/Toronto-Pivotal-User-Group\\/events\\/238944252\\/\"},\"group\":{\"group_topics\":[{\"urlkey\":\"spring-framework\",\"topic_name\":\"Spring Framework\"},{\"urlkey\":\"opensource\",\"topic_name\":\"Open Source\"},{\"urlkey\":\"newtech\",\"topic_name\":\"New Technology\"},{\"urlkey\":\"web\",\"topic_name\":\"Web Technology\"},{\"urlkey\":\"java\",\"topic_name\":\"Java\"},{\"urlkey\":\"grails\",\"topic_name\":\"Grails\"},{\"urlkey\":\"tc-server\",\"topic_name\":\"tc Server\"},{\"urlkey\":\"gemfire\",\"topic_name\":\"GemFire\"},{\"urlkey\":\"sqlfire\",\"topic_name\":\"SQLFire\"},{\"urlkey\":\"rabbit-mq\",\"topic_name\":\"Rabbit MQ\"}],\"group_city\":\"Toronto\",\"group_country\":\"ca\",\"group_id\":6015342,\"group_name\":\"Pivotal Toronto User Group\",\"group_lon\":-79.41,\"group_urlname\":\"Toronto-Pivotal-User-Group\",\"group_state\":\"ON\",\"group_lat\":43.66}}";
    }

    private String nonZeroGuestsJsonMessage() {
        return "{\"venue\":{\"venue_name\":\"Pivotal Labs Toronto\",\"lon\":-79.375641,\"lat\":43.649971,\"venue_id\":20351012},\"visibility\":\"public\",\"response\":\"yes\",\"guests\":3,\"member\":{\"member_id\":226447814,\"photo\":\"https:\\/\\/secure.meetupstatic.com\\/photos\\/member\\/a\\/e\\/c\\/thumb_266402796.jpeg\",\"member_name\":\"Nikhil V.\"},\"rsvp_id\":1665365939,\"mtime\":1493231047996,\"event\":{\"event_name\":\"Machine Learning Exposed\",\"event_id\":\"238944252\",\"time\":1493330400000,\"event_url\":\"https:\\/\\/www.meetup.com\\/Toronto-Pivotal-User-Group\\/events\\/238944252\\/\"},\"group\":{\"group_topics\":[{\"urlkey\":\"spring-framework\",\"topic_name\":\"Spring Framework\"},{\"urlkey\":\"opensource\",\"topic_name\":\"Open Source\"},{\"urlkey\":\"newtech\",\"topic_name\":\"New Technology\"},{\"urlkey\":\"web\",\"topic_name\":\"Web Technology\"},{\"urlkey\":\"java\",\"topic_name\":\"Java\"},{\"urlkey\":\"grails\",\"topic_name\":\"Grails\"},{\"urlkey\":\"tc-server\",\"topic_name\":\"tc Server\"},{\"urlkey\":\"gemfire\",\"topic_name\":\"GemFire\"},{\"urlkey\":\"sqlfire\",\"topic_name\":\"SQLFire\"},{\"urlkey\":\"rabbit-mq\",\"topic_name\":\"Rabbit MQ\"}],\"group_city\":\"Toronto\",\"group_country\":\"ca\",\"group_id\":6015342,\"group_name\":\"Pivotal Toronto User Group\",\"group_lon\":-79.41,\"group_urlname\":\"Toronto-Pivotal-User-Group\",\"group_state\":\"ON\",\"group_lat\":43.66}}";
    }
}
