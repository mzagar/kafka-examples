package com.github.mzagar.spring.kafka.test.example;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mzagar on 26/04/17.
 */
@Component
@KafkaListener(topics = MeetupItemConsumer.TOPIC)
public class MeetupItemConsumer {
    public static final String TOPIC = "meetup";
    @Autowired
    private Gson gson;

    private final ZeroGuestsListener zeroGuestsListener;
    private final AtomicLong recordsProcessed = new AtomicLong(0);

    @Autowired
    public MeetupItemConsumer(ZeroGuestsListener zeroGuestsListener) {
        this.zeroGuestsListener = zeroGuestsListener;
    }

    @KafkaHandler
    public void process(@Payload String record) {
        Item item = gson.fromJson(record, Item.class);
        if (item.getGuests() == 0) {
            zeroGuestsListener.onZeroGuests(item);
        }
        recordsProcessed.incrementAndGet();
    }

    public long getRecordsProcessed() {
        return recordsProcessed.get();
    }
}
