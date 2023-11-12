package com.example.hoard;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class TestSortAdapter {
    private ArrayList<String> data;

    @Before
    public void setUp() {
        data = new ArrayList<>();
        data.add("test");
    }

    @Test
    public void testGetItemCount() {
        SortAdapter sortAdapter = new SortAdapter(data);
        assertEquals(1, sortAdapter.getItemCount());
        data.add("test2");
        data.add("test3");
        assertEquals(3, sortAdapter.getItemCount());
    }
}