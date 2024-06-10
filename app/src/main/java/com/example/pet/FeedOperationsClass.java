package com.example.pet;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedOperationsClass {
    private final ConnectionMysqlClass connectionMysqlClass;
    private final MqttHandler mqttHandler;
    private Connection con;
    private boolean isDatabaseConnected = false;

    public FeedOperationsClass() {
        connectionMysqlClass = new ConnectionMysqlClass();
        mqttHandler = new MqttHandler();
    }

    public void connectMysql() {
        if (!isDatabaseConnected) {
            ExecutorService executionService = Executors.newSingleThreadExecutor();
            executionService.execute(() -> {
                try {
                    con = connectionMysqlClass.CONN();
                    if (con == null) {
                       Log.e("Debug msg", "Error in connection with SQL server");
                    } else {
                        isDatabaseConnected = true;
                    }
                } catch (Exception ex) {
                    isDatabaseConnected = false; // Maintain invariant
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public void connectBroker(String brokerUrl, String clientId) {
        mqttHandler.connect(brokerUrl, clientId);
    }

    public void feed(int id, String mode, float weight, String reservedDate, String reservedTime) throws JSONException {
        // Method invariant checks
        if (id <= 0 || weight <= 0 || reservedDate == null || reservedTime == null || mode == null) {
            throw new IllegalArgumentException("Invalid input parameters for feed method.");
        }
        if (!mode.equals("Auto") && !mode.equals("Manual")) {
            throw new IllegalArgumentException("Mode must be 'Auto' or 'Manual'.");
        }

        String sql = "INSERT INTO feeding (id, Mode, FeedingWeight, ReservedDate, ReservedTime, SetUpTime) VALUES (?, ?, ?, ?, ?, ?);";

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateString = dateFormat.format(calendar.getTime());

        // Insert the data into the database
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, mode);
                preparedStatement.setFloat(3, weight);
                preparedStatement.setString(4, reservedDate);
                preparedStatement.setString(5, reservedTime);
                preparedStatement.setString(6, currentDateString);

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount <= 0) {
                    throw new RuntimeException("Reserved Failed. Contact Developer!");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Remove the seconds from the reserved time
        String reservedTimeWithoutSeconds = reservedTime.substring(0, 5).replace(":", "");
        int reservedTimeAsInteger = Integer.parseInt(reservedTimeWithoutSeconds);

        // Publish the data to the MQTT broker
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("weight", weight);
        jsonObject.put("time", reservedTimeAsInteger);
        mqttHandler.publish("pet/feed/weight", jsonObject);

    }
}
