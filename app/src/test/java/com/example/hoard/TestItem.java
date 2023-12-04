package com.example.hoard;

import static com.google.firebase.firestore.util.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestItem {

    private Date date;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private String itemID;
    private ArrayList<Tag> tags;
    private List<String> imageUrls;

    @Test
    public void testItem() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(date, item.getDateOfAcquisition());
        assertEquals(description, item.getBriefDescription());
        assertEquals(make, item.getMake());
        assertEquals(model, item.getModel());
        assertEquals(serialNumber, item.getSerialNumber());
        assertEquals(estimatedValue, item.getEstimatedValue(), 0.0);
        assertEquals(comment, item.getComment());
        assertEquals(tags, item.getTags());
    }

    @Test
    public void testisValidDate() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidDate(date));
    }

    @Test
    public void testisValidDescription() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidDescription(description));
    }

    @Test
    public void testisValidMake() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidMake(make));
    }

    @Test
    public void testisValidModel() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidModel(model));
    }

    @Test
    public void testisValidSerialNumber() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidSerialNumber(serialNumber));
    }

    @Test
    public void testisValidValue() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidValue(estimatedValue));
    }

    @Test
    public void testisValidComment() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(true, item.isValidComment(comment));
    }

    @Test
    public void testgetDateOfAcquisition() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        Date date2 = item.getDateOfAcquisition();
        assertEquals(date, date2);
    }

    @Test
    public void testsetDateOfAcquisition() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setDateOfAcquisition(date);
        assertEquals(date, item.getDateOfAcquisition());
    }

    @Test
    public void testgetBriefDescription() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        String description2 = item.getBriefDescription();
        assertEquals(description, description2);
    }

    @Test
    public void testsetBriefDescription() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setBriefDescription(description);
        assertEquals(description, item.getBriefDescription());
    }

    @Test
    public void testgetItemID() {
        // Setup test data
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        // Create an Item object
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);

        // Get itemID from the Item object
        String itemID = item.getItemID();

        // Assert that itemID is a valid UUID
        try {
            UUID uuid = UUID.fromString(itemID);
            assertTrue("Item ID is a valid UUID", true);
        } catch (IllegalArgumentException exception) {
            fail("Item ID is not a valid UUID");
        }
    }

    @Test
    public void testgetMake() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        String make2 = item.getMake();
        assertEquals(make, make2);
    }

    @Test
    public void testsetMake() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setMake(make);
        assertEquals(make, item.getMake());
    }

    @Test
    public void testgetModel() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        String model2 = item.getModel();
        assertEquals(model, model2);
    }

    @Test
    public void testsetModel() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setModel(model);
        assertEquals(model, item.getModel());
    }

    @Test
    public void testgetSerialNumber() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        String serialNumber2 = item.getSerialNumber();
        assertEquals(serialNumber, serialNumber2);
    }

    @Test
    public void testsetSerialNumber() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setSerialNumber(serialNumber);
        assertEquals(serialNumber, item.getSerialNumber());
    }

    @Test
    public void testgetEstimatedValue() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        double estimatedValue2 = item.getEstimatedValue();
        assertEquals(estimatedValue, estimatedValue2, 0.0);
    }

    @Test
    public void testsetEstimatedValue() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setEstimatedValue(estimatedValue);
        assertEquals(estimatedValue, item.getEstimatedValue(), 0.0);
    }

    @Test
    public void testgetComment() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        String comment2 = item.getComment();
        assertEquals(comment, comment2);
    }

    @Test
    public void testsetComment() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setComment(comment);
        assertEquals(comment, item.getComment());
    }

    @Test
    public void addTag() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        tags.add(new Tag("Test Tag", "Test Color"));
        item.addTag(new Tag("Test Tag", "Test Color"));
        assertEquals(tags, item.getTags());
    }

    @Test
    public void testsetTags() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setTags(tags);
        assertEquals(tags, item.getTags());
    }

    @Test
    public void testremoveTag() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.removeTag(new Tag("Test Tag", "Test Color"));
        assertEquals(tags, item.getTags());
    }
    @Test
    public void testsetImageUrls() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        item.setImageUrls(imageUrls);
        assertEquals(imageUrls, item.getImageUrls());
    }

    @Test
    public void testgetImageUrls() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);

        assertEquals(imageUrls, item.getImageUrls());
    }

    @Test
    public void testgetTags() {
        date = new Date();
        description = "This is a test description";
        make = "Test Make";
        model = "Test Model";
        serialNumber = "Test Serial Number";
        estimatedValue = 100.00;
        comment = "This is a test comment";
        itemID = "Test Item ID";
        tags = new ArrayList<>();
        imageUrls = new ArrayList<>();
        Item item = new Item(date, description, make, model, serialNumber, estimatedValue, comment, tags, imageUrls);
        assertEquals(tags, item.getTags());
    }

}