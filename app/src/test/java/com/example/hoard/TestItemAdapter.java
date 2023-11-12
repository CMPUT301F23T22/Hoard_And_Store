package com.example.hoard;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestItemAdapter {
    private List<Item> items;
    private ItemAdapter itemAdapter;

    @Before
    public void setUp() {
        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(items, null);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(0, itemAdapter.getItemCount());

    }

    @Test
    public void testGetItem() {
        assertEquals(null, itemAdapter.getItem(0));
    }

}
