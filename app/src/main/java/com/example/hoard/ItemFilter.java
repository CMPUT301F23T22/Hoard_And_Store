package com.example.hoard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemFilter {
    private Date startDate;
    private Date endDate;
    private List<String> make;
    private List<String> desciptionKeyWords;
    private List<Tag> tags;
    public ItemFilter() {
        //We automatically assume the range in infinite set these to large and early date values
        startDate = new Date();
        endDate = new Date();
    }

    public List<Item> filter(List<Item> items){
        if(startDate != null | endDate != null) {
            filterDateRange(items);
        }
        return items;
    }

    private void filterDateRange(List<Item> items) {
        List<Item> filteredItems = new ArrayList<Item>();
        for (Item item: items){
            if(item.getDateOfAcquisition().compareTo(startDate) < 0 && item.getDateOfAcquisition().compareTo(startDate) > 0){
                filteredItems.add(item);
            }
        }
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

    public List<String> getMake() {
        return make;
    }

    public void setMake(List<String> make) {
        this.make = make;
    }

    public List<String> getDesciptionKeyWords() {
        return desciptionKeyWords;
    }

    public void setDesciptionKeyWords(List<String> desciptionKeyWords) {
        this.desciptionKeyWords = desciptionKeyWords;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}

