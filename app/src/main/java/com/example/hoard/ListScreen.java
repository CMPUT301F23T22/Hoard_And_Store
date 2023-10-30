package com.example.hoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Date;
import java.util.List;

public class ListScreen extends AppCompatActivity {

    private ItemDB itemDB;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton addItemButton;
    private Sort sortFragment = new Sort();
    private Fragment currentFragment;

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ItemDBController dbController;



    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        itemDB = new ItemDB(new ItemDBConnector());

        addItemButton = findViewById(R.id.addItemButton);

        bottomNav = findViewById(R.id.bottomNavigationView);

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
                    currentFragment = sortFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, sortFragment).commit();
                    return true;
                }
                return false;
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
}
