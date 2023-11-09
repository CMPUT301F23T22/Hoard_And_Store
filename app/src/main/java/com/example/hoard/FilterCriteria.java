package com.example.hoard;

import com.example.hoard.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterCriteria implements Serializable  {
    private List<String> makes;
    private List<String> descriptionKeyWords;
    private List<Tag> tags;
    private Date startDate;
    private Date endDate;

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

    public void clearStartDate() {
        startDate = null;
    }

    public void clearEndDate() {
        endDate = null;
    }

    public void clearMakes() {
        makes.clear();
    }

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

}


