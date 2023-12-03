package com.example.hoard;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for a RecyclerView that displays images in a carousel.
 * This adapter manages a list of Bitmap objects, each representing an image to be displayed.
 */
public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {
    private List<Bitmap> images; // List of images to be displayed

    /**
     * Constructor for the ImageCarouselAdapter.
     *
     * @param images A list of Bitmap objects representing the images to display.
     */
    public ImageCarouselAdapter(List<Bitmap> images) {
        this.images = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        // Setting LayoutParams to ensure ImageView takes up entire space
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap image = images.get(position);
        // Setting the Bitmap as the content of the ImageView
        holder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        // Returns the total number of items in the list
        return images.size();
    }

    /**
     * ViewHolder class for the adapter. This class holds references to the views for each data item.
     * In this case, it's just an ImageView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Constructor for the ViewHolder.
         *
         * @param view The ImageView that will display an image.
         */
        public ViewHolder(ImageView view) {
            super(view);
            imageView = view;
        }
    }
}
