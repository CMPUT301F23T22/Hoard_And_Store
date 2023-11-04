package com.example.hoard;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.Date;

public class CustomDatePicker {
    private Context context;
    private DatePickListener listener;

    public interface DatePickListener {
        void onDatePicked(Date date);
    }

    public CustomDatePicker(Context context, DatePickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            Date selectedDate = calendar.getTime();
            if (listener != null) {
                listener.onDatePicked(selectedDate);
            }
        });
    }
}
