package com.example.hoard;


import com.google.android.gms.tasks.Task;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class ListScreen extends AppCompatActivity{

    private ItemDB itemDB;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton addItemButton;
    private Sort sortFragment = new Sort();
    private Fragment currentFragment;
    FrameLayout listScreenFrame;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ItemDBController dbController;
    private Menu bottomMenu;
    private MenuItem sort;
    private MenuItem home;
    private Item itemToDelete = null;


    private void enterSelectionMode() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);

        // Hide BottomAppBar and FloatingActionButton
        bottomAppBar.setVisibility(View.GONE);
        addItemButton.setVisibility(View.GONE);

        // Show "Delete All" button
        Button deleteAllButton = findViewById(R.id.deleteAllButton);
        deleteAllButton.setVisibility(View.VISIBLE);

        // Change title
        toolbar.setTitle("Select items to delete");

        itemAdapter.setSelectionMode(true);


        // Replace the toolbar's menu with the selection mode menu
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_selection_mode);

        // Handle clicks on the "x" icon in the toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_close_selection) {
                exitSelectionMode();
                itemAdapter.clearSelections();
                return true;
            }
            return false;
        });
        // Set an OnClickListener for the "Delete All" button
        deleteAllButton.setOnClickListener(v -> {
            if (itemAdapter.getSelectedItemCount() == 0) {
                Toast.makeText(ListScreen.this, "No items selected.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Create an AlertDialog Builder
                new AlertDialog.Builder(ListScreen.this, R.style.PurpleAlertDialog)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete all selected items?") // Set the dialog message
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Positive button clicked, proceed with the deletion
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
                                itemAdapter.clearSelections();
                                exitSelectionMode();
                                Toast.makeText(ListScreen.this, "Items deleted successfully.", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(ListScreen.this, "Error while deleting items.", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Negative button clicked, do nothing
                            dialog.dismiss();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert) // Set an icon for the dialog
                        .show();
            }
        });
    }
    private void exitSelectionMode() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        // Revert back to the normal state
        toolbar.setTitle("Items");
        toolbar.setNavigationIcon(null); // Or set it to your default icon

        // Set the default click listener for the navigation icon
        toolbar.setNavigationOnClickListener(null);

        // Revert back to the normal menu
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.top_bar_menu);
        // Show BottomAppBar and FloatingActionButton
        bottomAppBar.setVisibility(View.VISIBLE);
        addItemButton.setVisibility(View.VISIBLE);

        // Hide the "Delete All" button
        Button deleteAllButton = findViewById(R.id.deleteAllButton);
        deleteAllButton.setVisibility(View.GONE);
        itemAdapter.setSelectionMode(false);
    }

    private ActivityResultLauncher<Intent> addEditActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddEditResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        addItemButton = findViewById(R.id.addItemButton);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        bottomNav = findViewById(R.id.bottomNavigationView);

        bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        home = bottomMenu.findItem(R.id.nav_home);
        home.setChecked(true);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbController = ItemDBController.getInstance();
        dbController.loadItems(new DataLoadCallback() {
            @Override
            public void onDataLoaded(List<Item> items) {
                itemAdapter = new ItemAdapter(items);
                recyclerView.setAdapter(itemAdapter);
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
                } else if (id == R.id.nav_delete) {
                    enterSelectionMode();
                    return false;
                }

                return true;
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListScreen.this, AddEditItem.class);
                addEditActivityResultLauncher.launch(intent);
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
                        // Snackbar closed on its own
                        itemToDelete = deletedItem;
                        if (itemToDelete != null) {
                            dbController.deleteItem(itemToDelete);
                        }
                    }

                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });

            snackbar.show();

        }
    };


    private void handleAddEditResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Item returnedItem = (Item) result.getData().getSerializableExtra("itemData");
            boolean wasUpdated = result.getData().getBooleanExtra("wasEdited", false);
            if (returnedItem != null) {
                if (wasUpdated) {
                    dbController.editItem(returnedItem);
                } else {
                    dbController.addItem(returnedItem);
                    itemAdapter.addItem(returnedItem);
                    itemAdapter.notifyItemChanged(itemAdapter.getsize() - 1);
                }
            }
        }
    }
}
