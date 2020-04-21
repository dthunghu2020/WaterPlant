package com.hungdt.waterplan.model;

import java.io.Serializable;
import java.util.List;

public class Plant implements Serializable {
    private String plantID;
    private String plantImage;
    private String plantName;
    private String plantNote;
    private List<Remind> reminds;
    private List<Event> events;
    private boolean isTicked = false;

    public Plant(String plantID, String plantImage, String plantName, String plantNote, List<Remind> reminds,List<Event> events) {
        this.plantID = plantID;
        this.plantImage = plantImage;
        this.plantName = plantName;
        this.plantNote = plantNote;
        this.reminds = reminds;
        this.events = events;
    }

    public String getPlantID() {
        return plantID;
    }

    public void setPlantID(String plantID) {
        this.plantID = plantID;
    }

    public String getPlantImage() {
        return plantImage;
    }

    public void setPlantImage(String plantImage) {
        this.plantImage = plantImage;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantNote() {
        return plantNote;
    }

    public void setPlantNote(String plantNote) {
        this.plantNote = plantNote;
    }

    public List<Remind> getReminds() {
        return reminds;
    }

    public void setReminds(List<Remind> reminds) {
        this.reminds = reminds;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public boolean isTicked() {
        return isTicked;
    }

    public void setTicked(boolean ticked) {
        isTicked = ticked;
    }
}
