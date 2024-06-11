package com.example.pet.ui.feedAutomatic;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.pet.FeedOperationsClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.ui.feed.FeedFragment;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class FeedAutomaticFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    public static FeedAutomaticFragment newInstance() {
        return new FeedAutomaticFragment();
    }
    private TextView textViewDate, textViewTime;
    private Button btnSubmit;
    private EditText editTextAmount;
    int day, month, year;
    String reservedTime, reservedDate;
    private FeedOperationsClass feedOperationsClass;
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "test01";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_automatic, container, false);
        ((MainActivity) requireActivity()).hideBottomNavigationView();

        // Handle the back button event
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Use NavController to navigate back to FeedFragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_feed);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Get the view model.
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

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
        feedOperationsClass = new FeedOperationsClass();
        feedOperationsClass.connectMysql();

        // Make a MQTT broker connection.
        feedOperationsClass.connectBroker(BROKER_URL, CLIENT_ID);

        // Make a reservation for automatic feeding mode.
        btnSubmit = view.findViewById(R.id.feedAuto_btnSubmit);
        editTextAmount = view.findViewById(R.id.feedAuto_inputAmount);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    autoFeed();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FeedFragment.progressDialog.dismiss();
    }

    private void autoFeed() throws JSONException {
        int weight = Integer.parseInt(editTextAmount.getText().toString());
        int id = Objects.requireNonNull(sharedViewModel.getUserClass().getValue()).id;
        feedOperationsClass.feed(id,"Auto", weight, reservedDate, reservedTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomNavigationView();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).showBottomNavigationView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireActivity()).showBottomNavigationView();
    }
}
