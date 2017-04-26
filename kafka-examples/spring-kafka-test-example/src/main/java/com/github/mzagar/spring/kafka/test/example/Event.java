package com.github.mzagar.spring.kafka.test.example;

/**
 * Created by mzagar on 26/04/17.
 */
class Event {
    private String event_name;
    private String event_url;

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_url() {
        return event_url;
    }

    public void setEvent_url(String event_url) {
        this.event_url = event_url;
    }
}
