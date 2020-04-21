package com.hungdt.waterplan.model;

import java.io.Serializable;

public class Event implements Serializable {
    String eventId;
    String eventName;
    String eventDate;
    String eventPosition;

    public Event(String eventId, String eventName, String eventDate, String eventPosition) {

        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventPosition = eventPosition;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventPosition() {
        return eventPosition;
    }

    public void setEventPosition(String eventPosition) {
        this.eventPosition = eventPosition;
    }
}
