package com.example.hoard;

import java.io.Serializable;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class FilterCriteria implements Serializable {
    public List<String> makes;
    public List<String> descriptionKeyWords;
    public List<Tag> tags;
    public Date startDate;
    public Date endDate;

    public FilterCriteria() {
    }

    public List<String> getMakes() {
        return makes;
    }

    public void setMakes(List<String> makes) {
        this.makes = makes;
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

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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
}
