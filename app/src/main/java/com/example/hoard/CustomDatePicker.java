package com.example.hoard;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

/**
 * A custom date picker class for selecting dates within the application.
 *
 */
public class CustomDatePicker {
    private final Context context;
    private final DatePickListener listener;

    /**
     * interface for the date picker
     */
    public interface DatePickListener {
        void onDatePicked(int year, int month, int day);
    }

    /**
     * constructor for the custom date picker
     * @param context the context of the activity
     * @param listener the listener for the date picker
     */
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
