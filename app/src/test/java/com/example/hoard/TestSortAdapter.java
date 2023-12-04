package com.example.hoard;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestSortAdapter {

    @Test
    public void testSortAdapter() {
        SortAdapter sortAdapter = new SortAdapter();
        assert(sortAdapter != null);
    }

    @Test
    public void testgetItemCount() {
        SortAdapter sortAdapter = new SortAdapter();
        assert(sortAdapter.getItemCount() == 5);
    }
}