package com.example.pet.ui.feeding;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pet.MainActivity;
import com.example.pet.MqttHandler;
import com.example.pet.R;

import java.util.Objects;

public class FeedingFragment extends Fragment {
    MqttHandler mqttHandler;
    public TextView textViewTitle;
    private Handler mainHandler;
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "test01";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeding, container, false);
        ((MainActivity) requireActivity()).hideBottomNavigationView();

        textViewTitle = view.findViewById(R.id.feeding_textTitle);
        Button doneButton = view.findViewById(R.id.feeding_btnDone);
        doneButton.setVisibility(View.GONE);

        // Check if the arguments are not null before retrieving the message
        if (getArguments() != null) {
            String message = getArguments().getString("message");
            textViewTitle.setText(message);
            doneButton.setVisibility(View.VISIBLE);
        }

        // Make a MQTT broker connection.
        mqttHandler = new MqttHandler();
        mqttHandler.connect(BROKER_URL, CLIENT_ID);

        // Subscribe to the topic "pet/feed/state"
        subscribeToTopic();

        mainHandler = new Handler(Looper.getMainLooper());
        mqttHandler.setFeedingFragment(this);

        textViewTitle.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.toString().equals("Feeding...")) {
                    doneButton.setVisibility(View.GONE);
                } else {
                    doneButton.setVisibility(View.VISIBLE);
                }
            }
        });

        doneButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_feed);
        });

        // Handle the back button event
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Show an alert dialog
                showAlertDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void subscribeToTopic() {
        mqttHandler.subscribe("pet/feed/state");
    }

    @SuppressLint("SetTextI18n")
    public void updateTextView(String result) {
        if (mainHandler != null) {
            mainHandler.post(() -> {
                if (textViewTitle != null) {
                    if (Objects.equals(result, "Success")) {
                        textViewTitle.setText("Feeding Successfully!");
                    } else {
                        textViewTitle.setText("Feeding Failed.");
                    }
                }
            });
        }
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit the feeding process?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Navigate back to FeedFragment
                    Button doneButton = requireView().findViewById(R.id.feeding_btnDone);
                    if (doneButton != null) {
                        NavController navController = Navigation.findNavController(doneButton);
                        navController.navigate(R.id.navigation_feed);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
