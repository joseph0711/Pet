package com.example.pet.ui.feedManual;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.pet.ConnectionClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.ui.feeding.FeedingFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedManualFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection con;
    String str;
    private EditText editTextInputAmount;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_manual, container, false);

        // Make a connection to MySQL.
        connectionClass = new ConnectionClass();
        connect();

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
}