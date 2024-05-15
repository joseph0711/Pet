package com.example.pet.ui.feedAutomatic;

import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.pet.R;
import com.example.pet.ui.feeding.FeedingFragment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class FeedAutomaticFragment extends Fragment {
    public static FeedAutomaticFragment newInstance() {
        return new FeedAutomaticFragment();
    }
    private EditText editTextFeedingTimes;
    private TextView textViewDate, textViewTime;
    private Button btnDateDialog, btnTimeDialog, btnSubmit;
    int day, month, year;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_automatic, container, false);

        // Set the date picker for the Date.
        btnDateDialog = view.findViewById(R.id.feedAuto_btnDate);
        textViewDate = view.findViewById(R.id.feedAuto_textDatePicked);
        btnDateDialog.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (datePicker, year, month, dayOfMonth) -> {
                month += 1;
                String userInputDate = year + "-" + month + "-" + dayOfMonth;
                textViewDate.setText("Date: " + userInputDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Set the time picker for the Time.
        btnTimeDialog = view.findViewById(R.id.feedAuto_btnTime);
        textViewTime = view.findViewById(R.id.feedAuto_textTimePicked);
        btnTimeDialog.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("SimpleDateFormat")
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    textViewTime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
                }
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        Button btnAppointment = view.findViewById(R.id.feedAuto_btnSubmit);
        btnAppointment.setOnClickListener(view1 -> {
            autoFeed();


            Fragment fragment = new FeedingFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });
        return view;
    }

    private void autoFeed() {

    }
}