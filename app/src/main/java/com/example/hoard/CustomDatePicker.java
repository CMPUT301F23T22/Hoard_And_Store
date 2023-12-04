package com.example.hoard;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;

/**
 * A custom date picker class for selecting dates within the application.
 * This class can be used to show either a single date picker or a date range picker based on the provided parameters.
 */
public class CustomDatePicker {
    private final Context context;
    private final DatePickListener listener;
    private final boolean isDateRangePicker;

    /**
     * Interface for the date picker listener.
     * Provides callbacks for single date or date range selection.
     */
    public interface DatePickListener {
        default void onSingleDatePicked(int year, int month, int day){

        }

        default void onDateRangePicked(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay){

        }

    }

    /**
     * Constructor for the custom date picker.
     *
     * @param context           The context of the activity where the date picker is used.
     * @param listener          The listener for date selection events.
     * @param isDateRangePicker True if the date picker is for selecting a date range, false for a single date.
     */
    public CustomDatePicker(Context context, DatePickListener listener, boolean isDateRangePicker) {
        this.context = context;
        this.listener = listener;
        this.isDateRangePicker = isDateRangePicker;
    }

    /**
     * Shows the custom date picker based on the type specified (single date or date range).
     */
    public void showDatePicker() {
        if (isDateRangePicker) {
            showDateRangePicker();
        } else {
            showSingleDatePicker();
        }
    }

    /**
     * Displays a single date picker using MaterialDatePicker.
     * On date selection, the listener's onSingleDatePicked method is called.
     */
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

    /**
     * Displays a date range picker using MaterialDatePicker.
     * On date range selection, the listener's onDateRangePicked method is called.
     */
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
