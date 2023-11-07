package com.example.hoard;

import com.example.hoard.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FilterCriteria implements Serializable  {
    private List<String> makes;
    private List<String> descriptionKeyWords;
    private List<Tag> tags;
    private Date startDate;
    private Date endDate;
    private Map<String, String> sortOptions;


    private static FilterCriteria instance;

    private FilterCriteria() {
        // Initialize the fields as needed
    }

    public static FilterCriteria getInstance() {
        if (instance == null) {
            instance = new FilterCriteria();
        }
        return instance;
    }

    public List<String> getMakes() {
        return makes;
    }

    public void setMakes(List<String> newMakes) {
        if (makes == null) {
            makes = new ArrayList<>();
        }
        makes.addAll(newMakes);
    }

    public List<String> getDescriptionKeyWords() {
        return descriptionKeyWords;
    }

    public void setDescriptionKeyWords(List<String> descriptionKeyWords) {
        this.descriptionKeyWords = descriptionKeyWords;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> newTags) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.addAll(newTags);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void clearMakes() {
        makes.clear();
    }

    public Map<String, String> getSortOptions (){
        return sortOptions;
    }

    public void setSortOptions(Map<String, String> sortOptions) {
        this.sortOptions = sortOptions;
    }

    public void apply(FilterCriteria updatedCriteria) {
        if (updatedCriteria.getDescriptionKeyWords() != null) {
            this.setDescriptionKeyWords(updatedCriteria.getDescriptionKeyWords()); 
        }
        if (updatedCriteria.getStartDate() != null) {
            this.setStartDate(updatedCriteria.getStartDate());
        }
        if (updatedCriteria.getEndDate() != null) {
            this.setEndDate(updatedCriteria.getEndDate());
        }
    }

    // our database stores the fields in camelCase however we display them to end user differently
    public static String toCamelCase(String input, String delimiter) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split(delimiter);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                result.append(word.toLowerCase()); // Convert the first word to lowercase
            } else {
                result.append(word.substring(0, 1).toUpperCase()); // Capitalize the first letter
                result.append(word.substring(1).toLowerCase()); // Convert the rest to lowercase
            }
        }

        return result.toString();
    }

}


