package com.example.pet.ui.feedManual;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pet.FeedOperationsClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.ui.feeding.FeedingFragment;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class FeedManualFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private FeedOperationsClass feedOperationsClass;
    private EditText editTextInputWeight;
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "test01";
    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_manual, container, false);
        ((MainActivity) requireActivity()).hideBottomNavigationView();

        // Create a new FeedOperationsClass object.
        feedOperationsClass = new FeedOperationsClass();

        // Get the view model.
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Make a connection to MySQL.
        feedOperationsClass.connectMysql();

        // Make a MQTT broker connection.
        feedOperationsClass.connectBroker(BROKER_URL, CLIENT_ID);

        // When user clicks the Start button.
        // Call manualFeed()
        editTextInputWeight = view.findViewById(R.id.feedManual_inputWeight);
        Button btnStart = view.findViewById(R.id.feedManual_btnStart);
        btnStart.setOnClickListener(view1 -> {
            try {
                manualFeed();
            } catch (MqttException | JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return view;
    }

    private void manualFeed() throws MqttException, JSONException {
        int weight = Integer.parseInt(editTextInputWeight.getText().toString());
        int id = Objects.requireNonNull(sharedViewModel.getUserClass().getValue()).id;

        // Set date and time to currentDateString, currentDate and currentTime.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateNow = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeNow = new SimpleDateFormat("HH:mm:ss");
        String currentDate = dateNow.format(calendar.getTime());
        String currentTime = timeNow.format(calendar.getTime());

        feedOperationsClass.feed(id,"Manual", weight, currentDate, currentTime);

       Fragment fragment = new FeedingFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireActivity()).showBottomNavigationView();
    }
}