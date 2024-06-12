package com.example.pet;

import android.util.Log;

import com.example.pet.ui.feeding.FeedingFragment;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttHandler {
    private MqttClient client;
    private FeedingFragment feedingFragment;
    private int failedMessageCount = 0;

    public void setFeedingFragment(FeedingFragment feedingFragment) {
        this.feedingFragment = feedingFragment;
    }

    public void connect(String brokerUrl, String clientId) {
        try {
            // Set up the persistence layer
            MemoryPersistence persistence = new MemoryPersistence();

            // Initialize the MQTT client
            client = new MqttClient(brokerUrl, clientId, persistence);

            // Set up the connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            // Connect to the broker
            client.connect(connectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, JSONObject message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.toString().getBytes());
            Log.i("INFO", message.toString());
            client.publish(topic, mqttMessage);
            Log.i("INFO", "msg send.");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("Error", "Connection lost.");
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws JSONException {
                    MessageArrivedListener listener = new MessageArrivedListener(feedingFragment);
                    JSONObject jsonObject = new JSONObject(message.toString());
                    String state = jsonObject.getString("state");
                    if ("Failed".equals(state)) {
                        failedMessageCount++;
                        if (failedMessageCount > 20) {
                            failedMessageCount = 0;
                            listener.result = state;
                            listener.messageArrived();
                        }
                    } else if ("Success".equals(state)) {
                        if (failedMessageCount > 0 && failedMessageCount < 20) {
                            failedMessageCount = 0;
                            listener.result = state;
                            listener.messageArrived();
                        } else if (failedMessageCount == 0) {
                            listener.result = state;
                            listener.messageArrived();
                        }
                    }
                    Log.i("INFO", "Message arrived: " + listener.result);
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i("INFO", "Delivery complete.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}