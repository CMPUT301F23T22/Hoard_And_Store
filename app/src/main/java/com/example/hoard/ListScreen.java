package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.result.ActivityResult;
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

import java.util.Date;
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

    private ActivityResultLauncher<Intent> addEditActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleAddEditResult);


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

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
        });

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(recyclerView);


        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    if (currentFragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                        currentFragment = null;
                    }
                    return true;
                } else if (id == R.id.nav_sort) {
                    // Replace the fragment container with the SortFragment
                    home.setEnabled(false);
                    home.setChecked(false);
                    sort.setEnabled(true);
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    startActivity(sortIntent);


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
