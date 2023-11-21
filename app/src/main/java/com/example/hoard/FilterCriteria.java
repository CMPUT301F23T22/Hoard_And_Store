package com.example.hoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents the criteria used for filtering items, implementing Serializable for easy data passing.
 *
 */
public class FilterCriteria implements Serializable {
    private List<String> makes;
    private List<String> descriptionKeyWords;
    private List<Map<String, Object>> tags;
    private Date startDate;
    private Date endDate;
    private Map<String, String> sortOptions;
    private String sortOption;
    private String sortBy;
    private List<Map<String, Object>> tagMaps;


    private static FilterCriteria instance;

    private FilterCriteria() {
        // Initialize the fields as needed
    }

    /**
     * Returns a singleton object that is shared.
     *
     * @return instance of FilterCriteria
     */
    public static FilterCriteria getInstance() {
        if (instance == null) {
            instance = new FilterCriteria();
        }
        return instance;
    }

    /**
     * Returns the makes added to filterCriteria
     *
     * @return list of strings
     */
    public List<String> getMakes() {
        return makes;
    }

    /**
     * Sets the makes to FilterCriteria for filtering.
     *
     * @param newMakes The list of makes to be set.
     */
    public void setMakes(List<String> newMakes) {
        if (makes == null) {
            makes = new ArrayList<>();
        }
        makes.addAll(newMakes);
    }

    /**
     * Returns the description keywords added to FilterCriteria.
     *
     * @return List of strings representing description keywords.
     */
    public List<String> getDescriptionKeyWords() {
        return descriptionKeyWords;
    }

    /**
     * Sets the desciption key words to FilterCriteria for filtering.
     *
     * @param descriptionKeyWords The list of description key words to be set.
     */
    public void setDescriptionKeyWords(List<String> descriptionKeyWords) {
        this.descriptionKeyWords = descriptionKeyWords;
    }

    /**
     * Returns the tags added to FilterCriteria.
     *
     * @return List of tags representing makes.
     */
    public List<Map<String, Object>> getTags() {
        return tags;
    }

    /**
     * Sets the tags to FilterCriteria for filtering.
     *
     * @param newTags The list of tags to be set.
     */
    public void setTags(List<Tag> newTags) {
        // Clear the existing list
        tags = new ArrayList<>();

        // Convert list of Tag objects to list of maps
        for (Tag tag : newTags) {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("tagColor", tag.getTagColor());
            tagMap.put("tagID", tag.getTagID());
            tagMap.put("tagName", tag.getTagName());
            // Add other properties as needed
            tags.add(tagMap);
        }
    }
    /**
     * Gets the start date for the filter criteria.
     *
     * @return The start date of the filter criteria.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date for the filter criteria.
     *
     * @param startDate The start date to be set for the filter criteria.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date for the filter criteria.
     *
     * @return The end date of the filter criteria.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date for the filter criteria.
     *
     * @param endDate The end date to be set for the filter criteria.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Clears the start date in the filter criteria.
     */
    public void clearStartDate() {
        startDate = null;
    }

    /**
     * Clears the end date in the filter criteria.
     */
    public void clearEndDate() {
        endDate = null;
    }

    /**
     * Clears the list of makes in the filter criteria.
     */
    public void clearMakes() {
        makes.clear();
    }
    public void clearDescriptionKeyWords(){
        descriptionKeyWords = null;
    }

    /**
     * Gets the sort options in the filter criteria.
     *
     * @return A map of sort options.
     */
    public Map<String, String> getSortOptions() {
        Map<String, String> sortOptions = new HashMap<>();
        if(sortBy != null & sortOption != null){
            sortOptions.put(sortBy, sortOption);
            return sortOptions;
        }

        return null;
    }


    /**
     * Sets the sort options in the filter criteria.
     *
     * @param sortOptions A map containing sort options.
     */
    public void setSortOptions(Map<String, String> sortOptions) {
        this.sortOptions = sortOptions;
    }

    /**
     * Applies the updated filter criteria to the current instance.
     *
     * @param updatedCriteria The updated filter criteria to apply.
     */
    public void apply(FilterCriteria updatedCriteria) {
        if (updatedCriteria.getDescriptionKeyWords() != null) {
            this.setDescriptionKeyWords(updatedCriteria.getDescriptionKeyWords());
        }
        if (updatedCriteria.getStartDate() != null) {
            this.clearStartDate();
            this.setStartDate(updatedCriteria.getStartDate());
        }
        if (updatedCriteria.getEndDate() != null) {
            this.clearEndDate();
            this.setEndDate(updatedCriteria.getEndDate());
        }
    }

    /**
     * Converts a string to camel case with a specified delimiter.
     *
     * @param input     The input string to be converted.
     * @param delimiter The delimiter used to split the input string.
     * @return The converted camel case string.
     */
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

    public void removeAllOptions(){
        makes = null;
        descriptionKeyWords = null;
        tags = null;
        startDate = null;
        endDate = null;
        sortOptions = null;
        sortOption = null;
        sortBy = null;
    }

    public void setSortBy(String sortBy){
        this.sortBy = sortBy;
    }

    public void setSortOption(String sortOption){
        this.sortOption = sortOption;

    }

    public String getSortBy(){
        return sortBy;
    }

    public String getSortOption(){
       return sortOption;
    }

}


