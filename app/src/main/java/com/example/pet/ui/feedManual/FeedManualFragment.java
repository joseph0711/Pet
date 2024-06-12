package com.example.pet.ui.feedManual;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pet.FeedOperationsClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.ui.feed.FeedFragment;
import com.example.pet.ui.feeding.FeedingFragment;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FeedFragment.progressDialog.dismiss();
    }

    private void manualFeed() throws MqttException, JSONException {
        // Validate the weight input is not empty.
        String weightString = editTextInputWeight.getText().toString();
        if (weightString.isEmpty()) {
            editTextInputWeight.setError("Weight is required");
            editTextInputWeight.requestFocus();
            return;
        }
        // Validate the weight format.
        if (!isValidWeight(weightString)) {
            editTextInputWeight.setError("Invalid weight format. Weight should be an integer between 10 and 300");
            editTextInputWeight.requestFocus();
            return;
        }
        int weight = Integer.parseInt(weightString);
        int id = Objects.requireNonNull(sharedViewModel.getUserClass().getValue()).id;

        // Set date and time to currentDateString, currentDate and currentTime.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateNow = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeNow = new SimpleDateFormat("HH:mm:ss");
        String currentDate = dateNow.format(calendar.getTime());
        String currentTime = timeNow.format(calendar.getTime());

        feedOperationsClass.feed(id,"Manual", weight, currentDate, currentTime);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.feedingFragment);
    }

    private boolean isValidWeight(String weight) {
        String integerRegex = "\\b([1-9][0-9]|1[0-9]{2}|2[0-9]{2}|300)\\b";
        Pattern integerPattern = Pattern.compile(integerRegex);
        Matcher integerMatcher = integerPattern.matcher(weight);

        if (integerMatcher.matches()) {
            // If the weight is an integer between 10 and 300, return true.
            return true;
        } else {
            // If the weight doesn't match the regex, return false.
            return false;
        }
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
