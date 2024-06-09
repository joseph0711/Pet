package com.example.pet.ui.feeding;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pet.MainActivity;
import com.example.pet.MqttHandler;
import com.example.pet.R;

import java.util.Objects;

public class FeedingFragment extends Fragment {
    MqttHandler mqttHandler;
    public TextView textViewTitle;
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "test01";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeding, container, false);
        ((MainActivity) requireActivity()).hideBottomNavigationView();

        textViewTitle = view.findViewById(R.id.feeding_textTitle);

        // Make a MQTT broker connection.
        mqttHandler = new MqttHandler();
        mqttHandler.connect(BROKER_URL, CLIENT_ID);

        // Subscribe to the topic "pet/feed/state"
        subscribeToTopic();

        mqttHandler.setFeedingFragment(this);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void subscribeToTopic() {
        mqttHandler.subscribe("pet/feed/state");
    }

    @SuppressLint("SetTextI18n")
    public void updateTextView(String result) {
        //TODO: fix the bug which caused textview can't be modified by code.
        Log.i("INFO", "Executing updateTextView()");
        Log.i("INFO", "state: " + result);

        if (Objects.equals(result, "Success")) {
            textViewTitle.setText("Feeding Complete.");
        } else {
            textViewTitle.setText("Feeding Failed.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireActivity()).showBottomNavigationView();
    }
}