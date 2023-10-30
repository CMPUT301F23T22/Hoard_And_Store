package com.example.hoard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Date;
import java.util.List;

public class ListScreen extends AppCompatActivity {

    private ItemDB itemDB;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ItemDBController dbController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemDB = new ItemDB(new ItemDBConnector());
        dbController = ItemDBController.getInstance();
        dbController.loadItems(new DataLoadCallback() {
            @Override
            public void onDataLoaded(List<Item> items) {
                itemAdapter = new ItemAdapter(items);
                recyclerView.setAdapter(itemAdapter);
            }
        });

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

        EditText dateOfAcquisition = view.findViewById(R.id.dateOfAcquisition);
        EditText briefDescription = view.findViewById(R.id.briefDescription);
        EditText make = view.findViewById(R.id.make);
        EditText model = view.findViewById(R.id.model);
        EditText serialNumber = view.findViewById(R.id.serialNumber);
        EditText estimatedValue = view.findViewById(R.id.estimatedValue);
        EditText comment = view.findViewById(R.id.comment);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((EditText) view.findFocus()).setBackground(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        dateOfAcquisition.addTextChangedListener(textWatcher);
        briefDescription.addTextChangedListener(textWatcher);
        make.addTextChangedListener(textWatcher);
        model.addTextChangedListener(textWatcher);
        serialNumber.addTextChangedListener(textWatcher);
        estimatedValue.addTextChangedListener(textWatcher);
        comment.addTextChangedListener(textWatcher);

        AlertDialog dialog = builder.setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button positiveButton = ((AlertDialog) d).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        Date parsedDate = null;
                        try {
                            parsedDate = sdf.parse(dateOfAcquisition.getText().toString());
                        } catch (ParseException e) {
                            dateOfAcquisition.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        String description = briefDescription.getText().toString();
                        String makeStr = make.getText().toString();
                        String modelStr = model.getText().toString();
                        String serial = serialNumber.getText().toString();
                        String valueStr = estimatedValue.getText().toString();
                        String commentStr = comment.getText().toString();

                        if (!Item.isValidDate(parsedDate)) {
                            dateOfAcquisition.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        if (!Item.isValidDescription(description)) {
                            briefDescription.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        if (!Item.isValidMake(makeStr)) {
                            make.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        if (!Item.isValidModel(modelStr)) {
                            model.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        if (!Item.isValidSerialNumber(serial)) {
                            serialNumber.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        if (valueStr.isEmpty() || !Item.isValidValue(Double.parseDouble(valueStr))) {
                            estimatedValue.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        if (!Item.isValidComment(commentStr)) {
                            comment.setBackground(getResources().getDrawable(R.drawable.invalid_input));
                            return;
                        }

                        double value = Double.parseDouble(valueStr);

                        Item newItem = new Item(
                                parsedDate,
                                description,
                                makeStr,
                                modelStr,
                                serial,
                                value,
                                commentStr
                        );

                        dbController.addItem(newItem);
                        itemAdapter.addItem(newItem);
                        itemAdapter.notifyItemChanged(itemAdapter.getsize() - 1);
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

}
