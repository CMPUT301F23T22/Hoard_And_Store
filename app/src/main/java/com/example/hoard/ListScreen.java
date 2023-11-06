package com.example.hoard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ListScreen extends AppCompatActivity {

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
    private FilterCriteria filterCriteria;

    private final int sortingRequestCode = 1;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        filterCriteria = FilterCriteria.getInstance();

        itemDB = new ItemDB(new ItemDBConnector());

        addItemButton = findViewById(R.id.addItemButton);

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
        }, filterCriteria);

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(recyclerView);


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {

                } else if (id == R.id.nav_sort) {
//                    // Replace the fragment container with the SortFragment
//                    home.setEnabled(false);
//                    home.setChecked(false);
//                    sort.setEnabled(true);
//
//                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
//                    sortIntent.putExtra("filterCriteria", filterCriteria);
//                    filterActivityResultLauncher.launch(sortIntent);

                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    sortIntent.putExtra("filterCriteria", filterCriteria);
                    filterActivityResultLauncher.launch(sortIntent);



                }
                return true;
            }
        });



        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });
    }



    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_item, null);

        EditText briefDescription = view.findViewById(R.id.briefDescription);
        EditText make = view.findViewById(R.id.make);
        EditText model = view.findViewById(R.id.model);
        EditText serialNumber = view.findViewById(R.id.serialNumber);
        EditText estimatedValue = view.findViewById(R.id.estimatedValue);
        EditText comment = view.findViewById(R.id.comment);

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Item newItem = new Item(
                                new Date(),
                                briefDescription.getText().toString(),
                                make.getText().toString(),
                                model.getText().toString(),
                                serialNumber.getText().toString(),
                                Double.parseDouble(estimatedValue.getText().toString()),
                                comment.getText().toString()
                        );

                        dbController.addItem(newItem);
                        itemAdapter.addItem(newItem);
                        itemAdapter.notifyItemChanged(itemAdapter.getsize() - 1);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinate_layout), R.string.text_label, Snackbar.LENGTH_LONG)
                .setText("Deleting " + itemAdapter.getItem(viewHolder.getAdapterPosition()).getMake())
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //itemToDelete = itemAdapter.getItem(viewHolder.getAdapterPosition());
                        itemAdapter.notifyItemChanged(itemAdapter.getsize() - 1);
                    }
                });
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                        // Snackbar closed on its own
                        itemToDelete = itemAdapter.getItem(viewHolder.getAdapterPosition());
                        if (itemToDelete != null) {
                            itemAdapter.removeItem(viewHolder.getAdapterPosition());
                            itemAdapter.notifyItemChanged(itemAdapter.getsize() - 1);
                            dbController.deleteItem(itemToDelete);
                        } else {
                            // Handle the case where the position is out of bounds or the item is not found
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

    ActivityResultLauncher<Intent> filterActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            FilterCriteria updatedFilterCriteria = (FilterCriteria) data.getSerializableExtra("filterCriteria");
                            if (updatedFilterCriteria != null) {
                                filterCriteria.apply(updatedFilterCriteria);
                                dbController.loadItems(new DataLoadCallback() {
                                    @Override
                                    public void onDataLoaded(List<Item> items) {
                                        itemAdapter.setItems(items);
                                        recyclerView.scrollToPosition(0);
                                    }
                                }, filterCriteria);
                            }
                        }
                    }
                }
            });
}
