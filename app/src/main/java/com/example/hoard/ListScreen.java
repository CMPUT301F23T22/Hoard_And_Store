package com.example.hoard;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
/**
 * An activity class for displaying a list of items in the application.
 *
 */
public class ListScreen extends AppCompatActivity implements ItemAdapter.SelectionModeCallback, ItemAdapter.SumCallBack {

    private ItemDB itemDB;
    private Toolbar topBar;
    private FloatingActionButton addItemButton;
    private TextView tvTotalValue;
    private Fragment currentFragment;
    private MaterialButton switchView;
    FrameLayout listScreenFrame;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ItemDBController dbController;
    private MenuItem sort;
    private MenuItem home;
    private FirebaseAuth mAuth;
    private final ActivityResultLauncher<Intent> addTagResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddTagResult);

    private final Item itemToDelete = null;
    private Menu selectionModeMenu;
    private MenuItem bulkDelete;
    private MenuItem search;
    private MenuItem closeBulkDelete;
    private MenuItem bulkTag;
    private ItemAdapter.SelectionModeCallback selectionModeCallback;
    private TagDBController tagDBController;
    private final int sortingRequestCode = 1;
    private TextView totalValueTextView;
    private FilterCriteria filterCriteria;
    private MenuItem profile;
    private MenuItem profileMenuItem;
    private MenuItem chartMenuItem;


    private final ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddResult);
    ChipGroup chipGroupTags;
    FrameLayout tagSelectionLayout;

    /**
     * Creates the activity and sets up the UI.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_screen);
        totalValueTextView = findViewById(R.id.tvTotalValueAmount);
        tagSelectionLayout = findViewById(R.id.tagSelectionLayout);
        filterCriteria = FilterCriteria.getInstance();
        mAuth = FirebaseAuth.getInstance();

//        itemDB = new ItemDB(new ItemDBConnector());


        switchView = findViewById(R.id.switchView);
        addItemButton = findViewById(R.id.addItemButton);
        topBar = findViewById(R.id.topAppBar);
        Menu topBarMenu = topBar.getMenu();
        topBar.setTitle(mAuth.getCurrentUser().getDisplayName());
        topBar.setTitle(mAuth.getCurrentUser().getDisplayName());
        search = topBarMenu.findItem(R.id.search);
        bulkDelete = topBarMenu.findItem(R.id.bulk_delete);
        bulkTag = topBarMenu.findItem(R.id.bulk_tag);
        closeBulkDelete = topBarMenu.findItem(R.id.close_bulk_select);
        tvTotalValue = findViewById(R.id.tvTotalValueAmount);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        Menu bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        home = bottomMenu.findItem(R.id.nav_home);
        chartMenuItem = bottomMenu.findItem(R.id.nav_chart);
        profileMenuItem = bottomMenu.findItem(R.id.nav_profile);

        //we are in home screen
        home.setChecked(true);

        //to get the current users info

        chipGroupTags = findViewById(R.id.tagChipGroup);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbController = ItemDBController.getInstance();

        dbController.loadItems(new DataLoadCallbackItem() {
            /**
             * Loads the items from the database and updates the UI.
             * @param items
             */
            @Override
            public void onDataLoaded(List<Item> items) {

                itemAdapter = new ItemAdapter(items, recyclerView);
                itemAdapter.setSumCallback(ListScreen.this);
                recyclerView.setAdapter(itemAdapter);
                itemAdapter.setSelectionModeCallback(ListScreen.this);
                itemAdapter.setSum();

            }
        }, filterCriteria);


        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            /**
             * Handles the menu items in the top bar.
             * @param item
             * @return
             */
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.search) {
                    // Implement search functionality

                } else if (id == R.id.bulk_delete) {
                    if (itemAdapter.getItemsSelectedCount() == 0) {
                        Toast.makeText(ListScreen.this, "No items selected.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Implement bulk delete functionality
                        if (itemAdapter.getSelectionMode()) {
                            showConfirmDialog("Confirm Delete", "Are you sure you want delete the selected items?",
                                    new DialogInterfaceCallback() {
                                        @Override
                                        public void onPositiveButtonClick(DialogInterface dialog) {
                                            // Delete the selected items from Firestore and update UI
                                            handleBulkDelete();
                                        }

                                    });
                        }

                    }
                } else if (id == R.id.bulk_tag) {
                    if (itemAdapter.getItemsSelectedCount() == 0) {
                        Toast.makeText(ListScreen.this, "No items selected.", Toast.LENGTH_SHORT).show();
                    } else {

                        tagSelectionLayout = findViewById(R.id.tagSelectionLayout);
                        ChipGroup chipGroupTags = findViewById(R.id.tagChipGroup); // Assuming you have a ChipGroup with this ID
                        tagDBController = TagDBController.getInstance();

                        tagDBController.loadTags(new DataLoadCallBackTag() {
                            @Override
                            public void onDataLoaded(List<Tag> tags) {
                                // Clear the previous tags
                                chipGroupTags.removeAllViews();
                                // Iterate through the items, creating a chip for each one
                                for (Tag tag : tags) {
                                    Chip chip = new Chip(ListScreen.this);
                                    chip.setText(tag.getTagName());
                                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(tag.getTagColor())));
                                    chip.setCheckedIconVisible(true);
                                    chip.setCheckable(true);
                                    chip.setTag(tag);
                                    // Add the chip to the ChipGroup
                                    chipGroupTags.addView(chip);
                                }
                            }
                        });

                        // Make the tag selection layout visible
                        tagSelectionLayout.setVisibility(View.VISIBLE);

                        // Implement bulk tag functionality
                        Button applyTagsButton = findViewById(R.id.applyTagsButton);
                        Button addTagBtn = findViewById(R.id.AddTagButton);
                        addTagBtn.setOnClickListener(view -> {
                            Intent tagIntent = new Intent(ListScreen.this, TagAddEdit.class);
                            addTagResultLauncher.launch(tagIntent);

                        });
                        applyTagsButton.setOnClickListener(view -> {
                            if (itemAdapter.getSelectionMode()) {
                                showConfirmDialog("Confirm Tags", "Are you sure you want apply tags to selected items?",
                                        new DialogInterfaceCallback() {
                                            @Override
                                            public void onPositiveButtonClick(DialogInterface dialog) {
                                                handleBulkTag();
                                            }

                                        });
                            }
                        });
                    }
                } else if (id == R.id.close_bulk_select) {
                    closeBulkSelect();
                }

                return true;
            }
        });

        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSwitched = itemAdapter.toggleItemViewType();
                Resources res = getResources();
                switchView.setIcon(isSwitched ? ResourcesCompat.getDrawable(res, R.drawable.grid_view_24px,null) : ResourcesCompat.getDrawable(res, R.drawable.list_24px,null));
                recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(ListScreen.this) : new GridLayoutManager(ListScreen.this, 2));
                itemAdapter.notifyDataSetChanged();
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(recyclerView);


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            /**
             * Handles the menu items in the bottom bar.
             * @param item
             * @return
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
//                    Intent homeIntent = new Intent(getApplicationContext(), ListScreen.class);
//                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(homeIntent);
                } else if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    sortIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(sortIntent);
                    return true;
                } else if (id == R.id.nav_chart) {
                    Intent chartIntent = new Intent(getApplicationContext(), ItemBreakdown.class);
                    chartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(chartIntent);

                } else if (id == R.id.nav_profile){
                    Intent profileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(profileIntent);
            }

                return true;
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the add item button.
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListScreen.this, AddEditItem.class);
                addActivityResultLauncher.launch(intent);
            }
        });
    }



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        /**
         * Handles the swipe to delete functionality.
         * @param recyclerView
         * @param viewHolder
         * @param target
         * @return
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        /**
         * Handles the swipe to delete functionality.
         * @param viewHolder
         * @param direction
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Save the item before it's deleted
            final int deletedItemPosition = viewHolder.getAdapterPosition();
            final Item deletedItem = itemAdapter.getItem(deletedItemPosition);

            // Remove the item from the list
            itemAdapter.removeItem(deletedItemPosition);
            itemAdapter.notifyItemChanged(deletedItemPosition);

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinate_layout), R.string.text_label, Snackbar.LENGTH_LONG)
                    .setText("Deleting " + deletedItem.getMake())
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemAdapter.addItem(deletedItem);
                            itemAdapter.notifyItemInserted(deletedItemPosition);
                        }
                    });
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                        Item itemToDelete = deletedItem;
                        if (itemToDelete != null) {
                            dbController.deleteItem(itemToDelete, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateTotalValue();
                                        }
                                    });
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(findViewById(R.id.coordinate_layout), "Failed to delete item. Please try again.", Snackbar.LENGTH_LONG).show();
                                    itemAdapter.addItem(deletedItem);
                                    itemAdapter.notifyItemInserted(deletedItemPosition);
                                    updateTotalValue();
                                }
                            });
                        }
                    } else if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                        // TODO: Handle action
                    }

                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });

            snackbar.show();

        }
    };

    protected void onResume() {
        super.onResume();

        home.setChecked(true);

    }

    /**
     * When save instance state is called, the selection mode is saved.
     * @param outState
     */
    @Override
    public void onSavedInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        topBar.setTitle(mAuth.getCurrentUser().getDisplayName());
        outState.putBoolean("selectionMode", itemAdapter.getSelectionMode());
        ArrayList<String> itemIDs = new ArrayList<>();
        for (Item item : itemAdapter.getItemList()) {
            itemIDs.add(item.getItemID());
        }
        outState.putStringArrayList("itemIDs", itemIDs);
    }
    /**
     * When restore instance state is called, the selection mode is restored.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        home.setChecked(true);
        super.onRestoreInstanceState(savedInstanceState);
        boolean selectionMode = savedInstanceState.getBoolean("selectionMode");
        ArrayList<String> itemIDs = savedInstanceState.getStringArrayList("itemIDs");
        if (itemIDs != null) {
            dbController.loadItems(new DataLoadCallbackItem() {
                @Override
                public void onDataLoaded(List<Item> items) {
                    itemAdapter = new ItemAdapter(items, recyclerView);
                    recyclerView.setAdapter(itemAdapter);
                    itemAdapter.setSelectionMode(selectionMode);
                    itemAdapter.setSumCallback(ListScreen.this);
                    updateTotalValue();
                    for (String itemID : itemIDs) {
                        itemAdapter.selectItem(itemID);
                    }
                    itemAdapter.notifyDataSetChanged();

                }
            }, filterCriteria);
        }
    }

    /**
     * Updates the total value of the items.
     */
    private void updateTotalValue() {
        dbController.getTotalValue(new Consumer<Double>() {
            @Override
            public void accept(Double totalValue) {
                // Here, update your TextView with the total value
                TextView totalValueTextView = findViewById(R.id.tvTotalValueAmount); // Assuming you have a TextView with this ID
                totalValueTextView.setText(String.format(Locale.getDefault(), "%.2f", totalValue));
            }
        });
    }

    /**
     * Handles the add item result.
     * @param result
     */
    private void handleAddResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Item returnedItem = (Item) result.getData().getSerializableExtra("newItem");
            if (returnedItem != null) {
                itemAdapter.addItem(returnedItem);
                itemAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                dbController.loadItems(new DataLoadCallbackItem() {
                    @Override
                    public void onDataLoaded(List<Item> items) {
                        itemAdapter = new ItemAdapter(items, recyclerView);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.setSelectionModeCallback(ListScreen.this);
                        itemAdapter.setSumCallback(ListScreen.this);
                    }
                }, filterCriteria);
            }
        }
    }

    /**
     * Updates the UI when the selection mode is changed.
     *
     * @param selectionMode The new state of selection mode.
     */
    @Override
    public void onSelectionModeChanged(boolean selectionMode) {
        BottomAppBar bottomAppBar;
        if (selectionMode) {
            search.setEnabled(false);
            search.setVisible(false);

            bulkDelete.setEnabled(true);
            bulkDelete.setVisible(true);

            closeBulkDelete.setEnabled(true);
            closeBulkDelete.setVisible(true);

            bulkTag.setEnabled(true);
            bulkTag.setVisible(true);

            bottomAppBar = findViewById(R.id.bottomAppBar);
            bottomAppBar.setVisibility(View.GONE);

            addItemButton = findViewById(R.id.addItemButton);
            addItemButton.setVisibility(View.GONE);

            updateSelectionModeTitle();
        } else {
//            search.setEnabled(true);
//            search.setVisible(true);


            bulkDelete.setEnabled(false);
            bulkDelete.setVisible(false);

            tagSelectionLayout.setVisibility(View.GONE);

            closeBulkDelete.setEnabled(false);
            closeBulkDelete.setVisible(false);

            bulkTag.setEnabled(false);
            bulkTag.setVisible(false);

            bottomAppBar = findViewById(R.id.bottomAppBar);
            bottomAppBar.setVisibility(View.VISIBLE);

            addItemButton = findViewById(R.id.addItemButton);
            addItemButton.setVisibility(View.VISIBLE);

            topBar.setTitle(mAuth.getCurrentUser().getDisplayName());
        }
    }

    /**
     * Closes the bulk selection mode and resets the UI to its normal state.
     */
    public void closeBulkSelect() {
        if (itemAdapter != null) {
            itemAdapter.setSelectionMode(false);
            onSelectionModeChanged(false);
        }
    }

    /**
     * Updates the title of the selection mode to reflect the number of items selected.
     */
    public void updateSelectionModeTitle() {
        if (itemAdapter != null) {
            topBar.setTitle("Selected (" + itemAdapter.getItemsSelectedCount() + ")");
        }
    }

    /**
     * Updates the total value of the items selected.
     *
     * @param result The result of the operation.
     */
    private void handleAddTagResult(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Tag returnedTag = (Tag) result.getData().getSerializableExtra("newTag");
            if (returnedTag != null) {
                Chip chip = new Chip(ListScreen.this);
                chip.setText(returnedTag.getTagName());
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(returnedTag.getTagColor())));
                chip.setCheckedIconVisible(true);
                chip.setCheckable(true);
                chip.setTag(returnedTag);
                // make it so that new created tag is selected
                chip.setChecked(true);
                // Add the chip to the ChipGroup
                chipGroupTags.addView(chip);
            }

        }
    }


    /**
     * Updates the total value of the items selected.
     *
     * @param sum The new total value.
     */
    @Override
    public void onSumChanged(double sum) {
        // Update the total value TextView
        totalValueTextView.setText(String.format(Locale.getDefault(), "%.2f", sum));
    }

    private interface DialogInterfaceCallback {
        void onPositiveButtonClick(DialogInterface dialog);
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param title    The title of the dialog.
     * @param message  The message of the dialog.
     * @param callback The callback to be called when the user clicks the positive button.
     */
    private void showConfirmDialog(String title, String message, DialogInterfaceCallback callback) {
        new AlertDialog.Builder(ListScreen.this, R.style.PurpleAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (callback != null) {
                        callback.onPositiveButtonClick(dialog);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    /**
     * Handles the bulk delete functionality.
     * Calls the database controller to delete the items.
     */
    private void handleBulkDelete(){
        List<Item> selectedItems = itemAdapter.getSelectedItems();
        Task<Void> deleteTask = dbController.bulkDeleteItems(selectedItems);

        deleteTask.addOnSuccessListener(aVoid -> {
            // After successful deletion from Firestore, remove items from the adapter list.
            for (Item selectedItem : selectedItems) {
                int index = itemAdapter.getItemList().indexOf(selectedItem);
                if (index != -1) {
                    itemAdapter.removeItem(index);
                }
            }
            // Update the UI after items have been removed from the adapter.
            itemAdapter.notifyDataSetChanged();
            closeBulkSelect(); // Call this method to update UI and exit selection mode.
            Toast.makeText(ListScreen.this, "Items deleted.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Handle the failure scenario, perhaps by showing a message to the user.
            Toast.makeText(ListScreen.this, "Error while deleting items.", Toast.LENGTH_SHORT).show();
        });

    }
    /**
     * Handles the bulk tag functionality.
     * Calls the database controller to add the tags to the items.
     */
    private void handleBulkTag() {
        List<Tag> selectedTags = new ArrayList<>();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (chip.isChecked()) {
                Tag tag = (Tag) chip.getTag();
                selectedTags.add(tag);
            }
        }

        // Now you have the selected tags, iterate over selected items and update them
        List<Item> selectedItems = itemAdapter.getSelectedItems();
        for (Item selectedItem : selectedItems) {
            for (Tag selectedTag : selectedTags) {
                if (!selectedItem.getTags().contains(selectedTag))
                    selectedItem.addTag(selectedTag);
            }
            // Update each item in the database
            dbController.editItem(selectedItem.getItemID(), selectedItem, task -> {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    Log.e("BulkTag", "Failed to update item tags", e);
                    Toast.makeText(ListScreen.this, "Failed to update item tags" + (e != null ? ": " + e.getMessage() : ""), Toast.LENGTH_SHORT).show();
                }
            });
        }
        itemAdapter.notifyDataSetChanged();

        // Inform the user
        Toast.makeText(ListScreen.this, "Tags applied to selected items.", Toast.LENGTH_SHORT).show();

        // Update UI and hide the tag selection layout
        itemAdapter.notifyDataSetChanged();
        tagSelectionLayout.setVisibility(View.GONE);
        closeBulkSelect(); // Close the bulk selection mode
    }
}


