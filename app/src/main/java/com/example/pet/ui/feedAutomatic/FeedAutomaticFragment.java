package com.example.pet.ui.feedAutomatic;

import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.pet.ConnectionClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.ui.feed.FeedFragment;
import com.example.pet.ui.feedManual.FeedManualFragment;
import com.example.pet.ui.feeding.FeedingFragment;
import com.example.pet.ui.home.HomeFragment;
import com.example.pet.ui.login.LoginActivity;
import com.example.pet.ui.register.RegisterFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedAutomaticFragment extends Fragment {
    public static FeedAutomaticFragment newInstance() {
        return new FeedAutomaticFragment();
    }
    ConnectionClass connectionClass;
    Connection con;
    String str;
    private TextView textViewDate, textViewTime;
    private Button btnSubmit;
    private EditText editTextAmount;
    int day, month, year, mount;
    String reservedTime, reservedDate;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_automatic, container, false);

        // Set the date picker for the Date.
        Button btnDateDialog = view.findViewById(R.id.feedAuto_btnDate);
        textViewDate = view.findViewById(R.id.feedAuto_textDatePicked);
        btnDateDialog.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (datePicker, year, month, dayOfMonth) -> {
                month += 1;
                reservedDate = year + "-" + month + "-" + dayOfMonth;
                textViewDate.setText("Date: " + reservedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        // Set the time picker for the Time.
        Button btnTimeDialog = view.findViewById(R.id.feedAuto_btnTime);
        textViewTime = view.findViewById(R.id.feedAuto_textTimePicked);
        btnTimeDialog.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("SimpleDateFormat")
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    reservedTime = new SimpleDateFormat("HH:mm").format(calendar.getTime());
                    textViewTime.setText(reservedTime);
                }
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Make a connection to MySQL.
        connectionClass = new ConnectionClass();
        connect();

        // Make a reservation for automatic feeding mode.
        btnSubmit = view.findViewById(R.id.feedAuto_btnSubmit);
        editTextAmount = view.findViewById(R.id.feedAuto_inputAmount);
        btnSubmit.setOnClickListener(view1 -> {
            autoFeed();
            Fragment fragment = new FeedingFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });
        return view;
    }

    private void autoFeed() {
        mount = Integer.parseInt(editTextAmount.getText().toString());
        String sql = "INSERT INTO feeding (Mode, Mount, ReservedDate, ReservedTime, Created) VALUES (?, ?, ?, ?, ?);";

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateString = dateFormat.format(calendar.getTime());

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, "Auto");
                preparedStatement.setInt(2, mount);
                preparedStatement.setString(3, reservedDate);
                preparedStatement.setString(4, reservedTime);
                preparedStatement.setString(5, currentDateString);

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount > 0) {
                    requireActivity().runOnUiThread(() -> {
                        // Registration successful
                        Toast.makeText(requireActivity(), "Reserved Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                    });
                } else {
                    // Registration failed
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Reserved Failed. Contact Developer!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void connect() {
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                con = connectionClass.CONN();
                if (con == null) {
                    requireActivity().runOnUiThread(() -> {
                        str = "Error in connection with SQL server";
                        Toast.makeText(requireActivity(), str, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}