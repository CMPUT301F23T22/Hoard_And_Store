package com.example.hoard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class TestTag {
    private String tagName;
    private String tagColor;
    private String tagID;

    @Before
    public void setUp() {
        tagName = "test";
        tagColor = "test";
        tagID = UUID.randomUUID().toString();
    }
    @Test
    public void TestTag() {
        Tag tag = new Tag(tagName, tagColor);
        assertNotNull(tag);
    }

    @Test
    public void TestTag2() {
        Tag tag = new Tag(tagName, tagColor, tagID);
        assertNotNull(tag);
    }

    @Test
    public void TestgetTagID() {
        Tag tag = new Tag(tagName, tagColor, tagID);
        String tagID2 = tag.getTagID();
        assertEquals(tagID, tagID2);
    }

    @Test
    public void TestgetTagName() {
        Tag tag = new Tag(tagName, tagColor, tagID);
        String tagName2 = tag.getTagName();
        assertEquals(tagName, tagName2);
    }

    @Test
    public void TestsetTagName() {
        Tag tag = new Tag(tagName, tagColor, tagID);
        tag.setTagName("test2");
        String tagName2 = tag.getTagName();
        assertEquals("test2", tagName2);
    }

    @Test
    public void TestgetTagColor() {
        Tag tag = new Tag(tagName, tagColor, tagID);
        String tagColor2 = tag.getTagColor();
        assertEquals(tagColor, tagColor2);
    }

    @Test
    public void TestsetTagColor() {
        Tag tag = new Tag(tagName, tagColor, tagID);
        tag.setTagColor("test2");
        String tagColor2 = tag.getTagColor();
        assertEquals("test2", tagColor2);
    }
    @Test
    public void testEquals() {
        String tagName = "ExampleTag";
        String tagColor = "#FF0000";
        String tagID = "12345";

        Tag tag1 = new Tag(tagName, tagColor, tagID);
        Tag tag2 = new Tag(tagName, tagColor, tagID);

        boolean result = tag1.equals(tag2);

        Assert.assertTrue(result);
    }

    @Test
    public void testHashCode() {
        String tagName = "ExampleTag";
        String tagColor = "#FF0000";
        String tagID = "12345";

        Tag tag1 = new Tag(tagName, tagColor, tagID);
        Tag tag2 = new Tag(tagName, tagColor, tagID);

        // Check that the hash codes of tag1 and tag2 are equal
        int hashCode1 = tag1.hashCode();
        int hashCode2 = tag2.hashCode();

        Assert.assertEquals(hashCode1, hashCode2);
    }
}
