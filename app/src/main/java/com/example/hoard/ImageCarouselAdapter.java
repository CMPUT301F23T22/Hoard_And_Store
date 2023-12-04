package com.example.hoard;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Adapter for a RecyclerView that displays a carousel of images.
 */
public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {

    private List<Uri> imageUris;
    private Context context;

    private final OnImageDeleteListener deleteListener;

    /**
     * Constructor for ImageCarouselAdapter.
     *
     * @param imageUris List of Uri objects representing the images.
     * @param context  Context of the application.
     */
    public ImageCarouselAdapter(List<Uri> imageUris, Context context, OnImageDeleteListener deleteListener) {
        this.imageUris = imageUris;
        this.context = context;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = imageUris.get(position);
        Glide.with(context)
                .load(uri)
                .into(holder.imageView);

        holder.deleteButton.setOnClickListener(view -> {
            deleteListener.onImageDelete(uri);
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    /**
     * ViewHolder class for the RecyclerView items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnImageDeleteListener {
        void onImageDelete(Uri uri);
    }
}