package com.example.hoard;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ListScreen extends AppCompatActivity {

    private ItemDB itemDB;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton addItemButton;
    private Sort sortFragment = new Sort();
    private TextView tvTotalValue;
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

    private ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddResult);


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        filterCriteria = FilterCriteria.getInstance();

        itemDB = new ItemDB(new ItemDBConnector());

        addItemButton = findViewById(R.id.addItemButton);
        tvTotalValue = findViewById(R.id.tvTotalValueAmount);

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
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    startActivity(sortIntent);
                }
                return true;
            }
        });
        updateTotalValue();

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

    protected void onResume() {
        super.onResume();
        dbController.loadItems(new DataLoadCallbackItem() {
            @Override
            public void onDataLoaded(List<Item> items) {
                itemAdapter = new ItemAdapter(items);
                recyclerView.setAdapter(itemAdapter);
                updateTotalValue();
            }
        });
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
                    itemAdapter.notifyItemChanged(itemAdapter.getsize() - 1);
                }
            updateTotalValue();
            }
        }
}
