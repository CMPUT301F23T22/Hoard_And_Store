package com.example.hoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

public class ListScreen extends AppCompatActivity {

    private ItemDB itemDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        itemDB = new ItemDB(new ItemDBConnector());

        FloatingActionButton addItemButton = findViewById(R.id.addItemButton);
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

                        itemDB.addItem(newItem);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
