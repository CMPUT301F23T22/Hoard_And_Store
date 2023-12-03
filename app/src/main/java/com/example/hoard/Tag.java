package com.example.hoard;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a tag that can be associated with an item. Tags have a name, color, and a unique identifier.
 */
public class Tag implements Serializable {
    private String tagName;
    private String tagColor;

    private final String tagID;

    /**
     * Constructor for creating a new Tag with a generated unique identifier.
     *
     * @param tagName  The name of the tag.
     * @param tagColor The color of the tag, expressed as a hex string.
     */
    public Tag(String tagName, String tagColor) {
        this.tagName = tagName;
        this.tagColor = tagColor;
        this.tagID = UUID.randomUUID().toString();
    }

    /**
     * Constructor for creating a new Tag with a specified unique identifier.
     *
     * @param tagName  The name of the tag.
     * @param tagColor The color of the tag, expressed as a hex string.
     * @param tagID    The unique identifier of the tag.
     */
    public Tag(String tagName, String tagColor, String tagID) {
        this.tagName = tagName;
        this.tagColor = tagColor;
        this.tagID = tagID;
    }

    /**
     * Gets the unique identifier of the tag.
     *
     * @return The unique identifier of the tag.
     */
    public String getTagID() {
        return tagID;
    }

    /**
     * Gets the name of the tag.
     *
     * @return The name of the tag.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Sets the name of the tag.
     *
     * @param tagName The name of the tag.
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Gets the color of the tag, expressed as a hex string.
     *
     * @return The color of the tag, expressed as a hex string.
     */
    public String getTagColor() {
        return tagColor;
    }

    /**
     * Sets the color of the tag, expressed as a hex string.
     *
     * @param tagColor The color of the tag, expressed as a hex string.
     */
    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    /**
     * Checks if two tags are equal. Two tags are equal if they have the same unique identifier.
     *
     * @param o The object to compare to.
     * @return True if the tags are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        return tagID.equals(tag.tagID) &&
                tagName.equals(tag.tagName) &&
                tagColor.equals(tag.tagColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagID, tagName, tagColor);
    }
}