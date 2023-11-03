package com.example.hoard;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.AutoCompleteTextView;

/*
A fragment that provides the UI for creating a new tag or editing an existing one. It would include form fields for the tag name, color selection, etc.
This could be a dialog fragment or a full-screen fragment, depending on the design.
*/

public class TagEditFragment extends Fragment {
    private String TagColor ;
    private String TagName;
    // Declare a private listener variable to hold a reference to the callback method.
    private TagAddListener listener;

    // Define an interface that will act as a callback mechanism.
    // This interface is used to communicate between the fragment and the activity or another fragment.
    public interface TagAddListener {
        // This method will be called when a new Tag is created.
        void onTagAdded(Tag newTag);
    }

    // onAttach is a lifecycle method called when the fragment is attached to its host (usually an activity).
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Try to initialize the listener from the context (the hosting activity).
            listener = (TagAddListener) context;
        } catch (ClassCastException e) {
            // If the hosting activity hasn't implemented the TagAddListener interface, throw an exception.
            throw new ClassCastException(context.toString() + " must implement TagAddListener");
        }
    }

    // Override the onCreateView method which is called to have the fragment instantiate and intialize the fragment its user interface view.
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the fragment's layout (fragment_tag_edit) and assign it to the 'view' variable.
        View tagview = inflater.inflate(R.layout.fragment_tag_edit, container, false);

        // Instantiate the AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView = tagview.findViewById(R.id.editTagName);

        // Now you can work with autoCompleteTextView, for example:
        autoCompleteTextView.setHint("Enter tag name");

        // Find the GridLayout named 'tag_colorPicker' from the inflated view.
        GridLayout colorGrid = tagview.findViewById(R.id.tag_colorPicker);

        // Iterate through all children of the GridLayout.
        for(int i = 0; i < colorGrid.getChildCount(); i++) {
            // Get the child view at the current index.
            View child = colorGrid.getChildAt(i);

            // Check if the child view is an instance of a Button.
            if(child instanceof Button) {
                // Set an onClick listener to the button.
                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorStateList colorStateList = ((Button) v).getBackgroundTintList();

                        // If the tint list is not null, retrieve the default color
                        if (colorStateList != null) {
                            TagColor = "#" + Integer.toHexString(colorStateList.getDefaultColor()).substring(2).toUpperCase();
                        }

                        // TODO Optionally, you can update the UI to indicate which color was selected.
                    }
                });
            }
        }

        // Return the fully constructed view for this fragment.
        return tagview;
    }


    // This method is used to create a new Tag and send it to the listener.
    public void createAndSendTag(String tagName, String tagColor) {
        // Create a new Tag object using the provided tagName and tagColor.
        Tag newTag = new Tag(tagName, tagColor);  // Assuming Tag has a constructor like this.
        // Invoke the onTagAdded method of the listener, passing the newTag as an argument.
        listener.onTagAdded(newTag);
    }


}