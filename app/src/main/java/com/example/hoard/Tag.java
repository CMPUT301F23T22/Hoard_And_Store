package com.example.hoard;

import java.io.Serializable;
import java.util.UUID;

public class Tag implements Serializable {
    private String tagName;
    private String tagColor;

    private String tagID;

    public Tag(String tagName, String tagColor) {
        this.tagName = tagName;
        this.tagColor = tagColor;
        this.tagID = UUID.randomUUID().toString();
    }

    public Tag(String tagName, String tagColor, String tagID) {
        this.tagName = tagName;
        this.tagColor = tagColor;
        this.tagID = tagID;
    }

    public String getTagID() {
        return tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }
}