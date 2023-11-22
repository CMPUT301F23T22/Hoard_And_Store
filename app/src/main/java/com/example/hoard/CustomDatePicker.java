package com.example.hoard;

import android.content.Context;
import android.util.Pair;

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
    private final boolean isDateRangePicker;

    /**
     * Interface for the date picker
     */
    public interface DatePickListener {
        default void onSingleDatePicked(int year, int month, int day){

        }

        default void onDateRangePicked(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay){

        }

    }

    /**
     * Constructor for the custom date picker
     *
     * @param context           The context of the activity
     * @param listener          The listener for the date picker
     * @param isDateRangePicker True if a date range picker should be shown, false for a normal date picker
     */
    public CustomDatePicker(Context context, DatePickListener listener, boolean isDateRangePicker) {
        this.context = context;
        this.listener = listener;
        this.isDateRangePicker = isDateRangePicker;
    }

    /**
     * Shows the custom date picker
     */
    public void showDatePicker() {
        if (isDateRangePicker) {
            showDateRangePicker();
        } else {
            showSingleDatePicker();
        }
    }

    private void showSingleDatePicker() {
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
                listener.onSingleDatePicked(year, month, day);
            }
        });
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> materialDateRangePicker = builder.build();
        materialDateRangePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_RANGE_PICKER");

        materialDateRangePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(selection.first);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(selection.second);

            if (listener != null) {
                listener.onDateRangePicked(
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)
                );
            }
        });
    }
}
