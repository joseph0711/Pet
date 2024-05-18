package com.example.pet.ui.feedManual;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.twin.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pet.ConnectionClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.ui.feeding.FeedingFragment;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import com.microsoft.azure.sdk.iot.device.twin.DesiredPropertiesCallback;
import com.microsoft.azure.sdk.iot.device.twin.MethodCallback;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedManualFragment extends Fragment {
    private final String connString = "HostName=pet.azure-devices.net;DeviceId=esp32;SharedAccessKey=Pux1gnjg2jJZaSj0GCVme/TBKKsmam14BAIoTFKr2sc=";
    private DeviceClient client;
    private Twin twin;
    ConnectionClass connectionClass;
    Connection con;
    String str;
    private EditText editTextInputAmount;
    IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_manual, container, false);

        // Make a connection to MySQL.
        connectionClass = new ConnectionClass();
        connect();


        try
        {
            ConnectToAzureIoTHub();
        }
        catch (Exception e2)
        {
            System.out.println("Exception while opening IoTHub connection");
            e2.printStackTrace();
        }

        // When user clicks the Start button.
        // Call manualFeed()
        editTextInputAmount = view.findViewById(R.id.feedManual_inputAmount);
        Button btnStart = view.findViewById(R.id.feedManual_btnStart);
        btnStart.setOnClickListener(view1 -> manualFeed());
        return view;
    }

    private void manualFeed() {
        int mount;
        mount = Integer.parseInt(editTextInputAmount.getText().toString());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss");
        String currentDateString = dateFormat1.format(calendar.getTime());
        String currentDate = dateFormat2.format(calendar.getTime());
        String currentTime = dateFormat3.format(calendar.getTime());

        String sql = "INSERT INTO feeding (Mode, Mount, ReservedDate, ReservedTime, Created) VALUES (?, ?, ?, ?, ?);";
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, "Manual");
                preparedStatement.setInt(2, mount);
                preparedStatement.setString(3, currentDate);
                preparedStatement.setString(4, currentTime);
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

        // Add: Call Arduino to do feeding function...
        btnSendOnClick();

        // When send the signal to Arduino successfully, go to FeedingFragment.
        Fragment fragment = new FeedingFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();


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

    private void ConnectToAzureIoTHub()
    {
        client = new DeviceClient(connString, protocol);

        try
        {
            client.open(true);

            if (protocol == IotHubClientProtocol.MQTT)
            {
                MessageCallbackMqtt callback = new MessageCallbackMqtt();
                Counter counter = new Counter(0);
                client.setMessageCallback(callback, counter);
            }
            else
            {
                MessageCallback callback = new MessageCallback();
                Counter counter = new Counter(0);
                client.setMessageCallback(callback, counter);
            }

            /*
            client.subscribeToMethods(this, null);
            client.subscribeToDesiredProperties((DesiredPropertiesCallback) this, null);
            twin = client.getTwin();*/
        }
        catch (Exception e2)
        {
            Log.e("debug msg", "Exception while opening IoTHub connection: " + e2.getMessage());
            client.close();
            Log.e("debug msg", "Shutting down...");
        }
    }

    public void btnSendOnClick()
    {
        double temperature = 20.0 + Math.random() * 10;
        double humidity = 30.0 + Math.random() * 20;

        String msgStr = "{\"temperature\":" + temperature + ",\"humidity\":" + humidity + "}";
        try
        {
            Message msg = new Message(msgStr);
            msg.setProperty("temperatureAlert", temperature > 28 ? "true" : "false");
            msg.setMessageId(java.util.UUID.randomUUID().toString());
            Log.i("debug info",msgStr);
            client.sendEventAsync(msg, new MessageSentCallbackImpl(), null);
        }
        catch (Exception e)
        {
           Log.e("debug msg", "Exception while sending event: " + e.getMessage());
        }
    }

    static class MessageCallbackMqtt implements com.microsoft.azure.sdk.iot.device.MessageCallback
    {
        public IotHubMessageResult onCloudToDeviceMessageReceived(Message msg, Object context)
        {
            Counter counter = (Counter) context;
            Log.i("debug info",
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            counter.increment();

            return IotHubMessageResult.COMPLETE;
        }
    }

    static class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback
    {
        public IotHubMessageResult onCloudToDeviceMessageReceived(Message msg, Object context)
        {
            Counter counter = (Counter) context;
            Log.i("debug info",
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            counter.increment();

            return IotHubMessageResult.COMPLETE;
        }
    }

    static class MessageSentCallbackImpl implements MessageSentCallback
    {
        @Override
        public void onMessageSent(Message message, IotHubClientException e, Object o)
        {
            if (e == null)
            {
                Log.i("debug info","IoT Hub responded to message " + message.getMessageId() + " with status OK");
            }
            else
            {
                Log.i("debug info","IoT Hub responded to message " + message.getMessageId() + " with status " + e.getStatusCode().name());
            }
        }
    }

    /**
     * Used as a counter in the message callback.
     */
    static class Counter
    {
        int num;

        Counter(int num)
        {
            this.num = num;
        }

        int get()
        {
            return this.num;
        }

        void increment()
        {
            this.num++;
        }

        @Override
        public String toString()
        {
            return Integer.toString(this.num);
        }
    }
}