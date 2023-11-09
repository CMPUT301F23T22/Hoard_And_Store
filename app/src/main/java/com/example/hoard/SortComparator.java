package com.example.hoard;


import com.google.firebase.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class SortComparator implements Comparator<Object> {
    @Override
    public int compare(Object value1, Object value2) {
        //compare strings
        if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).compareTo((String) value2);
        //compare dobules
        } else if (value1 instanceof Double && value2 instanceof Double) {
            return Double.compare((Double) value1, (Double) value2);
        // compare dates
        } else if (value1 instanceof Timestamp && value2 instanceof Timestamp) {
            return ((Timestamp) value1).compareTo((Timestamp) value2);
        // compare Tags
        } else if (value1 instanceof Tag && value2 instanceof Tag) {
            return ((Tag) value1).getTagName().compareTo(((Tag) value2).getTagName());
        } else {
            // Handle other data types or return 0 if they are not directly comparable
            return 0;
        }
    }
}
