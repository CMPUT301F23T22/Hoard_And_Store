package com.example.hoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Item implements Serializable {
    private Date dateOfAcquisition;
    private String briefDescription ,make, model, serialNumber , comment;
    private double estimatedValue;
    private String itemID;
    private ArrayList<Tag> tags;

    public Item(Date dateOfAcquisition, String briefDescription, String make, String model, String serialNumber, double estimatedValue, String comment, ArrayList<Tag> tagsList) {

        if (!isValidDate(dateOfAcquisition)) {
            throw new IllegalArgumentException("Invalid date format.");
        }
        if (!isValidDescription(briefDescription)) {
            throw new IllegalArgumentException("Invalid description.");
        }
        if (!isValidMake(make)) {
            throw new IllegalArgumentException("Invalid make.");
        }
        if (!isValidModel(model)) {
            throw new IllegalArgumentException("Invalid model.");
        }
        if (!isValidSerialNumber(serialNumber)) {
            throw new IllegalArgumentException("Invalid serial number.");
        }
        if (!isValidValue(estimatedValue)) {
            throw new IllegalArgumentException("Invalid estimated value.");
        }
        if (!isValidComment(comment)) {
            throw new IllegalArgumentException("Invalid comment length.");
        }

        this.dateOfAcquisition = dateOfAcquisition;
        this.briefDescription = briefDescription;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.itemID = UUID.randomUUID().toString();
        this.tags = tagsList;
    }

    public Item(Date dateOfAcquisition, String briefDescription, String make, String model, String serialNumber, double estimatedValue, String comment, String itemID,ArrayList<Tag> tagsList) {
        this.dateOfAcquisition = dateOfAcquisition;
        this.briefDescription = briefDescription;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.itemID = itemID;
        this.tags = tagsList;
    }

    public static boolean isValidDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = sdf.format(date);
        String regex = "^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formattedDate);
        return matcher.matches();
    }

    public static boolean isValidDescription(String description) {
        return !description.isEmpty();
    }

    public static boolean isValidMake(String make) {
        return !make.isEmpty();
    }

    public static boolean isValidModel(String model) {
        return !model.isEmpty();
    }

    public static boolean isValidSerialNumber(String serialNumber) {
        return !serialNumber.isEmpty();
    }

    public static boolean isValidValue(double value) {
        return value >= 0;
    }

    public static boolean isValidComment(String comment) {
        return !comment.isEmpty();
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

    public String getItemID() {
        return itemID;
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
        if (tags != null) {
            tags.add(tag);
        }else{
            //#TODO error message
        }
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }
    public void removeTag(Tag tag) {
        if (tags != null) {
            tags.remove(tag);
        }
    }

    public ArrayList<Tag> getTags() {
        return this.tags;
    }
}
