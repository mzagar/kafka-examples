package com.github.mzagar.spring.kafka.test.example;

import org.springframework.stereotype.Component;

/**
 * Created by mzagar on 26/04/17.
 */
@Component
public class ZeroGuestsListener {
    public void onZeroGuests(Item item) {
        System.out.println(String.format("%s - No guests for: %s -- go here and signup: %s",
                Thread.currentThread().getName(),
                item.getEvent().getEvent_name(),
                item.getEvent().getEvent_url())
        );
    }
}
