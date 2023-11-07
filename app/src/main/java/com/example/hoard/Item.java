package com.example.hoard;

import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Item implements Serializable {
    private Date dateOfAcquisition;
    private String briefDescription;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private ArrayList<Tag> tags;
    private List<String> briefDescriptionList;

    public Item(Date dateOfAcquisition, String briefDescription, String make, String model, String serialNumber, double estimatedValue, String comment) {

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
        this.briefDescriptionList = splitBriefDescription(this.briefDescription);

        Log.d("BriefDescriptionArray", briefDescriptionList.toString());
    }

    private List<String> splitBriefDescription(String briefDescription) {
        String[] briefDescriptionArray = briefDescription.split(" ");
        return Arrays.asList(briefDescriptionArray);
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
        this.briefDescriptionList = splitBriefDescription(this.briefDescription);
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

    public List<String> getBriefDescriptionList() {
        return briefDescriptionList;
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
}
