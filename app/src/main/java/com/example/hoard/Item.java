package com.example.hoard;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item implements Serializable {
    private Date dateOfAcquisition;
    private String briefDescription, make, model, serialNumber, comment;
    private double estimatedValue;
    private final String itemID;
    private ArrayList<Tag> tags;
    @PropertyName("briefDescriptionList")
    private final List<String> briefDescriptionList;

    private boolean isSelected;

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
        this.briefDescriptionList = splitBriefDescription(this.briefDescription);
        this.itemID = UUID.randomUUID().toString();
        this.tags = tagsList;
    }

    public Item(Date dateOfAcquisition, String briefDescription, String make, String model, String serialNumber, double estimatedValue, String comment, String itemID, ArrayList<Tag> tagsList) {
        this.dateOfAcquisition = dateOfAcquisition;
        this.briefDescription = briefDescription;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.itemID = itemID;
        this.tags = tagsList;
        this.briefDescriptionList = splitBriefDescription(this.briefDescription);

        this.isSelected = false;
        this.tags = tags;

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

    /**
     * Checks if the provided description is valid (not empty).
     *
     * @param description The description to be validated.
     * @return True if the description is valid, false otherwise.
     */
    public static boolean isValidDescription(String description) {
        return !description.isEmpty();
    }

    /**
     * Checks if the provided make is valid (not empty).
     *
     * @param make The make to be validated.
     * @return True if the make is valid, false otherwise.
     */
    public static boolean isValidMake(String make) {
        return !make.isEmpty();
    }

    /**
     * Checks if the provided model is valid (not empty).
     *
     * @param model The model to be validated.
     * @return True if the model is valid, false otherwise.
     */
    public static boolean isValidModel(String model) {
        return !model.isEmpty();
    }

    /**
     * Checks if the provided serial number is valid (not empty).
     *
     * @param serialNumber The serial number to be validated.
     * @return True if the serial number is valid, false otherwise.
     */
    public static boolean isValidSerialNumber(String serialNumber) {
        return !serialNumber.isEmpty();
    }

    /**
     * Checks if the provided value is valid (non-negative).
     *
     * @param value The value to be validated.
     * @return True if the value is valid, false otherwise.
     */
    public static boolean isValidValue(double value) {
        return value >= 0;
    }

    /**
     * Checks if the provided comment is valid (not empty).
     *
     * @param comment The comment to be validated.
     * @return True if the comment is valid, false otherwise.
     */
    public static boolean isValidComment(String comment) {
        return !comment.isEmpty();
    }

    // Getters and Setters for the Item's properties

    /**
     * Gets the date of acquisition for the item.
     *
     * @return The date of acquisition.
     */
    public Date getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    /**
     * Sets the date of acquisition for the item.
     *
     * @param dateOfAcquisition The date of acquisition to be set.
     */
    public void setDateOfAcquisition(Date dateOfAcquisition) {
        this.dateOfAcquisition = dateOfAcquisition;
    }

    /**
     * Gets the brief description of the item.
     *
     * @return The brief description.
     */
    public String getBriefDescription() {
        return briefDescription;
    }

    /**
     * Sets the brief description of the item.
     *
     * @param briefDescription The brief description to be set.
     */
    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    /**
     * Gets the unique ID of the item.
     *
     * @return The item's ID.
     */
    public String getItemID() {
        return itemID;
    }

    /**
     * Gets the make of the item.
     *
     * @return The make of the item.
     */
    public String getMake() {
        return make;
    }

    /**
     * Sets the make of the item.
     *
     * @param make The make to be set.
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Gets the model of the item.
     *
     * @return The model of the item.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the item.
     *
     * @param model The model to be set.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the serial number of the item.
     *
     * @return The serial number of the item.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the item.
     *
     * @param serialNumber The serial number to be set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the estimated value of the item.
     *
     * @return The estimated value of the item.
     */
    public double getEstimatedValue() {
        return estimatedValue;
    }

    /**
     * Sets the estimated value of the item.
     *
     * @param estimatedValue The estimated value to be set.
     */
    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    /**
     * Gets the comment associated with the item.
     *
     * @return The comment associated with the item.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment associated with the item.
     *
     * @param comment The comment to be set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Adds a tag to the item.
     *
     * @param tag The tag to be added.
     */
    public void addTag(Tag tag) {
        if (tags != null) {
            tags.add(tag);
        } else {
            // TODO: Handle the case where tags list is null (e.g., log an error message).
        }
    }

    /**
     * set the items tag
     *
     * @param tags ArrayList<Tag>
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public void removeTag(Tag tag) {
        if (tags != null) {
            tags.remove(tag);
        }
    }

    public List<String> getBriefDescriptionList() {
        return briefDescriptionList;
    }

    public ArrayList<Tag> getTags() {
        return this.tags;
    }
}
