package com.example.hoard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestItemFilter {
    private Date startDate;
    private Date endDate;
    private List<String> make;
    private List<String> descriptionKeyWords;
    private List<Tag> tags;

    @Test
    public void testfilterDateRange() {
        List<Item> items = new ArrayList<>();
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.filter(items);
    }

    @Test
    public void testgetStartDate() {
        ItemFilter itemFilter = new ItemFilter();
        assertNotNull(itemFilter.getStartDate());
    }

    @Test
    public void testsetStartDate() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.setStartDate(null);
        assertEquals(null, itemFilter.getStartDate());
    }

    @Test
    public void testgetEndDate() {
        ItemFilter itemFilter = new ItemFilter();
        assertNotNull(itemFilter.getEndDate());
    }

    @Test
    public void testsetEndDate() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.setEndDate(null);
        assertEquals(null, itemFilter.getEndDate());
    }


    @Test
    public void testgetMake() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.getMake();
        assertEquals(null, itemFilter.getMake());
    }

    @Test
    public void testsetMake() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.setMake(null);
        assertEquals(null, itemFilter.getMake());
    }

    @Test
    public void testgetDesciptionKeyWords() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.getDesciptionKeyWords();
        assertEquals(null, itemFilter.getDesciptionKeyWords());
    }

    @Test
    public void testdescriptionKeyWords() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.descriptionKeyWords(null);
        assertEquals(null, itemFilter.getDesciptionKeyWords());
    }

    @Test
    public void testgetTags() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.getTags();
        assertEquals(null, itemFilter.getTags());
    }

    @Test
    public void testsetTags() {
        ItemFilter itemFilter = new ItemFilter();
        itemFilter.setTags(null);
        assertEquals(null, itemFilter.getTags());
    }
}
