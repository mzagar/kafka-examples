package com.github.mzagar.spring.kafka.test.example;

/**
 * Created by mzagar on 26/04/17.
 */
class Item {
    private String visibility;
    private Event event;
    private double guests;

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public double getGuests() {
        return guests;
    }

    public void setGuests(double guests) {
        this.guests = guests;
    }
}
