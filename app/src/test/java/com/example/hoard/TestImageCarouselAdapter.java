package com.example.hoard;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestImageCarouselAdapter {

    @Test
    public void testgetItemCount() {
        List<Bitmap> emptyImageList = new ArrayList<>();

        ImageCarouselAdapter imageCarouselAdapter = new ImageCarouselAdapter(emptyImageList);
        assertEquals(0, imageCarouselAdapter.getItemCount());
    }
}
