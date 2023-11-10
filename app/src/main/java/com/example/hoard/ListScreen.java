package com.example.hoard;


import com.google.android.gms.tasks.Task;


import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ListScreen extends AppCompatActivity implements ItemAdapter.SelectionModeCallback, ItemAdapter.SumCallBack {

    private ItemDB itemDB;
    private Toolbar topBar;
    private Menu topBarMenu;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton addItemButton;
    private TextView tvTotalValue;
    private Fragment currentFragment;
    FrameLayout listScreenFrame;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ItemDBController dbController;
    private Menu bottomMenu;
    private MenuItem sort;
    private MenuItem home;
    private ActivityResultLauncher<Intent> addTagResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddTagResult);

    private Item itemToDelete = null;
    private Menu selectionModeMenu;
    private MenuItem bulkDelete;
    private MenuItem search;
    private MenuItem closeBulkDelete;
    private MenuItem bulkTag;
    private ItemAdapter.SelectionModeCallback selectionModeCallback;
    private FilterCriteria filterCriteria;
    private TagDBController tagDBController;
    private final int sortingRequestCode = 1;
    private TextView totalValueTextView;


    private ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddResult);
    ChipGroup chipGroupTags;
    FrameLayout tagSelectionLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_screen);
        totalValueTextView = findViewById(R.id.tvTotalValueAmount);
        tagSelectionLayout = findViewById(R.id.tagSelectionLayout);
        filterCriteria = FilterCriteria.getInstance();

//        itemDB = new ItemDB(new ItemDBConnector());

        addItemButton = findViewById(R.id.addItemButton);
        topBar = findViewById(R.id.topAppBar);
        topBarMenu = topBar.getMenu();

        search = topBarMenu.findItem(R.id.search);
        bulkDelete = topBarMenu.findItem(R.id.bulk_delete);
        bulkTag = topBarMenu.findItem(R.id.bulk_tag);
        closeBulkDelete = topBarMenu.findItem(R.id.close_bulk_select);
        tvTotalValue = findViewById(R.id.tvTotalValueAmount);

        bottomNav = findViewById(R.id.bottomNavigationView);

        bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        home = bottomMenu.findItem(R.id.nav_home);

        home.setChecked(true);


        chipGroupTags = findViewById(R.id.tagChipGroup);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbController = ItemDBController.getInstance();
        dbController.loadItems(new DataLoadCallbackItem() {
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
                            new AlertDialog.Builder(ListScreen.this, R.style.PurpleAlertDialog)
                                    .setTitle("Confirm Delete")
                                    .setMessage("Are you sure you want to delete these items?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        // Delete the selected items from Firestore
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
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
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
                                new AlertDialog.Builder(ListScreen.this, R.style.PurpleAlertDialog)
                                        .setTitle("Confirm Tags")
                                        .setMessage("Are you sure you want to add the selected tags to these items?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            // Get selected tags
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
                                        }).setNegativeButton("No", (dialog, which) -> {
                                            dialog.dismiss();
                                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
                            }
                        });
                    }
                } else if (id == R.id.close_bulk_select) {
                    closeBulkSelect();
                }

                return true;
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(recyclerView);


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {

                } else if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    startActivity(sortIntent);
                    return true;
                }

                return true;
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListScreen.this, AddEditItem.class);
                addActivityResultLauncher.launch(intent);
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

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

//    protected void onResume() {
//        super.onResume();
//
//        dbController.loadItems(new DataLoadCallbackItem() {
//            @Override
//            public void onDataLoaded(List<Item> items) {
////                itemAdapter = new ItemAdapter(items, recyclerView);
////                recyclerView.setAdapter(itemAdapter);
//                itemAdapter.setSelectionModeCallback(ListScreen.this);
//                itemAdapter.setSumCallback(ListScreen.this);
//            }
//        }, FilterCriteria.getInstance());
//
//    }
    @Override
    public void onSavedInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("selectionMode", itemAdapter.getSelectionMode());
        ArrayList<String> itemIDs = new ArrayList<>();
        for (Item item : itemAdapter.getItemList()) {
            itemIDs.add(item.getItemID());
        }
        outState.putStringArrayList("itemIDs", itemIDs);
    }

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
            }, FilterCriteria.getInstance());
        }
    }

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

    private void handleAddResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Item returnedItem = (Item) result.getData().getSerializableExtra("newItem");
            if (returnedItem != null) {
                itemAdapter.addItem(returnedItem);
                itemAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                updateTotalValue();
            }
        }
    }

    @Override
    public void onSelectionModeChanged(boolean selectionMode) {
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

            topBar.setTitle("Items");
        }
    }

    public void closeBulkSelect() {
        if (itemAdapter != null) {
            itemAdapter.setSelectionMode(false);
            onSelectionModeChanged(false);
        }
    }

    public void updateSelectionModeTitle() {
        if (itemAdapter != null) {
            topBar.setTitle("Selected (" + itemAdapter.getItemsSelectedCount() + ")");
        }
    }

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


    @Override
    public void onSumChanged(double sum) {
        // Update the total value TextView
        totalValueTextView.setText(String.format(Locale.getDefault(), "%.2f", sum));

    }
}