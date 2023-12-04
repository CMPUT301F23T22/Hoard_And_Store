package com.example.hoard;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;
import android.net.Uri;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestImageCarouselAdapter {
    private List<Uri> imageUrls = new ArrayList<>();
    @Test
    public void testgetItemCount() {
        List<Bitmap> bitmaps = new ArrayList<>();
        ImageCarouselAdapter imageCarouselAdapter = new ImageCarouselAdapter(imageUrls, null);
        assert(imageCarouselAdapter.getItemCount() == 0);
    }
}