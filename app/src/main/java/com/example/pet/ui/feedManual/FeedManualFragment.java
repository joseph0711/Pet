package com.example.pet.ui.feedManual;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.pet.MqttHandler;
import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.ui.feeding.FeedingFragment;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedManualFragment extends Fragment {
    ConnectionMysqlClass connectionMysqlClass;
    MqttHandler mqttHandler;
    Connection con;
    String str;
    private EditText editTextInputWeight;
    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "test01";

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_manual, container, false);

        // Make a connection to MySQL.
        connectionMysqlClass = new ConnectionMysqlClass();
        connectMysql();

        // Make a MQTT broker connection.
        mqttHandler = new MqttHandler();
        mqttHandler.connect(BROKER_URL, CLIENT_ID);

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
        int weight;
        weight = Integer.parseInt(editTextInputWeight.getText().toString());

        // Set date and time to currentDateString, currentDate and currentTime.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss");
        String currentDateString = dateFormat1.format(calendar.getTime());
        String currentDate = dateFormat2.format(calendar.getTime());
        String currentTime = dateFormat3.format(calendar.getTime());

        // Insert data and send to the database.
        String sql = "INSERT INTO feeding (Mode, Mount, ReservedDate, ReservedTime, Created) VALUES (?, ?, ?, ?, ?);";
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, "Manual");
                preparedStatement.setInt(2, weight);
                preparedStatement.setString(3, currentDate);
                preparedStatement.setString(4, currentTime);
                preparedStatement.setString(5, currentDateString);

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount > 0) {
                    requireActivity().runOnUiThread(() -> {
                        // Reserve successful
                        Toast.makeText(requireActivity(), "Reserved Successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Reserve failed
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Reserved Failed. Contact Developer!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Create JSON format message.
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("feedingweight", weight);

        // Publish the message to the topic "feeding_amount"
        publishMessage(jsonObject);

        // TODO: Fix the bug which caused the app to crash when the user clicked the Start button. (轉換到 FeedingFragment 造成的crush bug待處理
        // Might be due to the fragment transaction.
//        Fragment fragment = new FeedingFragment();
//        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    public void connectMysql() {
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                con = connectionMysqlClass.CONN();
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

    private void publishMessage(JSONObject message) {
        Toast.makeText(this.getContext(), "Publishing message: " + message, Toast.LENGTH_SHORT).show();
        mqttHandler.publish("pet/feed/weight", message);
    }
}