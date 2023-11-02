package com.example.hoard;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
A fragment that provides the UI for creating a new tag or editing an existing one. It would include form fields for the tag name, color selection, etc.
This could be a dialog fragment or a full-screen fragment, depending on the design.
*/

public class TagEditFragment extends Fragment {

    private TagEditViewModel mViewModel;

    public static TagEditFragment newInstance() {
        return new TagEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TagEditViewModel.class);
        // TODO: Use the ViewModel
    }

}