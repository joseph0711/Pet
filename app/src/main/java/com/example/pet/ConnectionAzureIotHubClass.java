package com.example.pet;

import android.util.Log;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageSentCallback;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

public class ConnectionAzureIotHubClass {
    private final String connString = "HostName=pet.azure-devices.net;DeviceId=esp32;SharedAccessKey=Pux1gnjg2jJZaSj0GCVme/TBKKsmam14BAIoTFKr2sc=";
    private DeviceClient client;
    private Twin twin;
    IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    public void ConnectToAzureIoTHub()
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

    public void sendMsgToArduino(int kilogram)
    {
        String msgStr = "{\"kilogram\":" + kilogram + "}";
        try
        {
            Message msg = new Message(msgStr);
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
