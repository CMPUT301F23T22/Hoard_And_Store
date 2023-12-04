package com.example.hoard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestFilterCriteria {
    private List<String> makes;
    private List<String> descriptionKeyWords;
    private List<Tag> tags;
    private Date startDate;
    private Date endDate;
    private FilterCriteria filterCriteria;
    private Date Startdate;

    @Before
    public void setUp() {
        filterCriteria = FilterCriteria.getInstance();
    }

    @Test
    public void testGetMakes() {
        assertNull(filterCriteria.getMakes());
        makes = new ArrayList<>();
        filterCriteria.setMakes(makes);
        assertEquals(makes, filterCriteria.getMakes());
    }

    @Test
    public void testSetMakes() {
        List<String> makes = new ArrayList<>(); // Initialize with an empty list
        filterCriteria.setMakes(makes);
        assertNotNull(filterCriteria.getMakes());
        assertTrue(filterCriteria.getMakes().isEmpty());
    }

    @Test
    public void testGetDescriptionKeyWords() {
        assertNull(filterCriteria.getDescriptionKeyWords());
        descriptionKeyWords = new ArrayList<>();
        filterCriteria.setDescriptionKeyWords(descriptionKeyWords);
        assertEquals(descriptionKeyWords, filterCriteria.getDescriptionKeyWords());

    }

    @Test
    public void testSetDescriptionKeyWords() {
        filterCriteria.setDescriptionKeyWords(null);
        assertNull(filterCriteria.getDescriptionKeyWords());
        descriptionKeyWords = new ArrayList<>();
        filterCriteria.setDescriptionKeyWords(descriptionKeyWords);
        assertEquals(descriptionKeyWords, filterCriteria.getDescriptionKeyWords());
    }

    @Test
    public void testGetTags() {
        assertNull(filterCriteria.getTags());
        tags = new ArrayList<>();
        filterCriteria.setTags(tags);
        assertEquals(tags, filterCriteria.getTags());
    }

    @Test
    public void testSetTags() {
        List<Tag> tags = new ArrayList<>(); // Initialize with an empty list
        filterCriteria.setTags(tags);
        assertNotNull(filterCriteria.getTags());
        assertTrue(filterCriteria.getTags().isEmpty());
    }


    @Test
    public void testGetStartDate() {
        assertNull(filterCriteria.getStartDate());
        Startdate = new Date();
        filterCriteria.setStartDate(Startdate);
        assertEquals(Startdate, filterCriteria.getStartDate());
    }

    @Test
    public void testSetStartDate() {
        Startdate = new Date();
        filterCriteria.setStartDate(Startdate);
        assertEquals(Startdate, filterCriteria.getStartDate());

    }

    @Test
    public void testGetEndDate() {
        assertNull(filterCriteria.getEndDate());
        endDate = new Date();
        filterCriteria.setEndDate(endDate);
        assertEquals(endDate, filterCriteria.getEndDate());
    }

    @Test
    public void testSetEndDate() {
        endDate = new Date();
        filterCriteria.setEndDate(endDate);
        assertEquals(endDate, filterCriteria.getEndDate());
    }

    @Test
    public void testClearStartDate() {
        Startdate = new Date();
        filterCriteria.setStartDate(Startdate);
        assertEquals(Startdate, filterCriteria.getStartDate());
        filterCriteria.clearStartDate();
        assertNull(filterCriteria.getStartDate());
    }

    @Test
    public void testClearEndDate() {
        endDate = new Date();
        filterCriteria.setEndDate(endDate);
        assertEquals(endDate, filterCriteria.getEndDate());
        filterCriteria.clearEndDate();
        assertNull(filterCriteria.getEndDate());
    }

    @Test
    public void testClearMakes() {
        filterCriteria.setMakes(new ArrayList<>());
        assertNotNull(filterCriteria.getMakes());
        filterCriteria.clearMakes();
        assertTrue(filterCriteria.getMakes().isEmpty());
    }

    @Test
    public void apply() {
        FilterCriteria updatedCriteria = FilterCriteria.getInstance();
        updatedCriteria.setDescriptionKeyWords(null);
        updatedCriteria.setStartDate(null);
        updatedCriteria.setEndDate(null);
        filterCriteria.apply(updatedCriteria);
        assertNull(filterCriteria.getDescriptionKeyWords());
        assertNull(filterCriteria.getStartDate());
        assertNull(filterCriteria.getEndDate());
    }

}