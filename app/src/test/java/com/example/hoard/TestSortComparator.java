package com.example.hoard;

import org.junit.Test;

public class TestSortComparator {
    @Test
    public void testcompare() {
        SortComparator sortComparator = new SortComparator();
        assert(sortComparator.compare("a", "b") == -1);
        assert(sortComparator.compare("b", "a") == 1);
        assert(sortComparator.compare("a", "a") == 0);
        assert(sortComparator.compare(1.0, 2.0) == -1);
        assert(sortComparator.compare(2.0, 1.0) == 1);
        assert(sortComparator.compare(1.0, 1.0) == 0);
        assert(sortComparator.compare("a", 1.0) == 0);
        assert(sortComparator.compare(1.0, "a") == 0);
    }
}