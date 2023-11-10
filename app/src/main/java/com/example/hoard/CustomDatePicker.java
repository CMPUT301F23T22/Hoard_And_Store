package com.example.hoard;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

public class CustomDatePicker {
    private final Context context;
    private final DatePickListener listener;

    public interface DatePickListener {
        void onDatePicked(int year, int month, int day);
    }

    public CustomDatePicker(Context context, DatePickListener listener) {
        this.context = context;
        this.listener = listener;
    }


    /**
     * shows the custom date picker
     */
    public void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;

            if (listener != null) {
                listener.onDatePicked(year, month, day);
            }
        });
    }
}
