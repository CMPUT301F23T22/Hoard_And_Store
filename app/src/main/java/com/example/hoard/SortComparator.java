package com.example.hoard;


import android.media.Image;
import android.nfc.Tag;

import com.google.firebase.Timestamp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class SortComparator implements Comparator<Object> {
    /**
     * Compares 2 objects and their valies
     *
     * @param  value1 first object value to compare
     * @param  value2 second object to compare
     * @return int -1 if value1 is smaller, 1 if value1 is bigger and 0 if value1 and value2
     * are equal
     */
    @Override
    public int compare(Object value1, Object value2) {
        //compare strings
        if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).toLowerCase().compareTo(((String) value2).toLowerCase( ));
        //compare dobules
        } else if (value1 instanceof Double && value2 instanceof Double) {
            return -1 * (Double.compare((Double) value1, (Double) value2));
        // compare dates
        } else if (value1 instanceof Timestamp && value2 instanceof Timestamp) {
            return ((Timestamp) value1).compareTo((Timestamp) value2);
        // compare Tags
        } else if (value1 instanceof ArrayList && value2 instanceof ArrayList) {
            ArrayList<HashMap> list1 = (ArrayList<HashMap>) value1;
            ArrayList<HashMap> list2 = (ArrayList<HashMap>) value2;

            // Assuming the "tagName" key is present in the HashMap at index 0
            String tagName1 = (String) list1.get(0).get("tagName");
            String tagName2 = (String) list2.get(0).get("tagName");

            return tagName1.toLowerCase().compareTo(tagName2.toLowerCase());
        } else {
            // Handle other data types or return 0 if they are not directly comparable
            return 0;
        }
    }
}
