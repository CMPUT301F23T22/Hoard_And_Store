package com.example.hoard;

import java.util.ArrayList;
import java.util.Date;

public class Item {
    private Date dateOfAcquisition;
    private String briefDescription;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private ArrayList<Tag> tags;
    private boolean isSelected;
    public Item(Date dateOfAcquisition, String briefDescription, String make, String model, String serialNumber, double estimatedValue, String comment) {
        this.dateOfAcquisition = dateOfAcquisition;
        this.briefDescription = briefDescription;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.isSelected = false;
        tags = new ArrayList<>();
    }

    // Getters and Setters for the Item's properties
    public Date getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    public void setDateOfAcquisition(Date dateOfAcquisition) {
        this.dateOfAcquisition = dateOfAcquisition;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public boolean isSelected() {return isSelected;}

    public void setSelected(boolean selected) {isSelected = selected;}
}
