package com.example.pet.ui.changeUserInfo;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.ui.login.LoginActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeUserInfoFragment extends Fragment {
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private EditText petNameEditText, weightEditText;
    private RadioButton radioGenderButton;
    private Button btnConfirm, btnDatePicker;
    private TextView textDatePicked, textAge;
    int day, month, year;
    String userInputDate;
    Period period;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_user_info, container, false);

        // Get the value from the radio button in radio group.
        RadioGroup radioGenderGroup = view.findViewById(R.id.changeUserInfo_radioGenderGroup);
        radioGenderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.changeUserInfo_radioBtnMale) {
                radioGenderButton = view.findViewById(R.id.changeUserInfo_radioBtnMale);
            } else {
                radioGenderButton = view.findViewById(R.id.changeUserInfo_radioBtnFemale);
            }
        });

        // Set the date picker for the Birth Date.
        btnDatePicker = view.findViewById(R.id.changeUserInfo_btnDatePicker);
        textDatePicked = view.findViewById(R.id.changeUserInfo_textDatePicked);
        textAge = view.findViewById(R.id.changeUserInfo_textAge);
        btnDatePicker.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (datePicker, year, month, dayOfMonth) -> {
                month += 1;
                userInputDate = year + "-" + month + "-" + dayOfMonth;
                textDatePicked.setText("Date: " + userInputDate);

                // Calculate the pet's age.
                LocalDate selectedDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    selectedDate = LocalDate.of(year, month, dayOfMonth);
                }
                LocalDate currentDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentDate = LocalDate.now();
                }
                period = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    period = Period.between(selectedDate, currentDate);
                }

                // Display the pet's age.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    textAge.setText("Pet's age: " + period.getYears());
                }
            }, year, month, day);
            datePickerDialog.show();
        });

        petNameEditText = view.findViewById(R.id.changeUserInfo_inputName);
        weightEditText = view.findViewById(R.id.changeUserInfo_inputWeight);

        // Call the petInfoRegister() method when the Confirm button is clicked.
        btnConfirm = view.findViewById(R.id.changeUserInfo_btnConfirm);
        btnConfirm.setOnClickListener( view1 -> petInfoUpdate());
        return view;
    }

    private void petInfoUpdate() {
        String petName, birthDate, gender;
        int age, weight;
        petName = petNameEditText.getText().toString();
        birthDate = userInputDate;
        weight = Integer.parseInt(weightEditText.getText().toString());
        gender = radioGenderButton.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age = period.getYears();
        } else {
            age = 0;
        }

        String sql = "INSERT INTO pet (Name, Weight, Age, BirthDate, Gender) VALUES (?, ?, ?, ?, ?);";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, petName);
                preparedStatement.setInt(2, weight);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, birthDate);
                preparedStatement.setString(5, gender);

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount > 0) {
                    requireActivity().runOnUiThread(() -> {
                        // Registration successful
                        Toast.makeText(requireActivity(), "Pet Registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                    });
                } else {
                    // Registration failed
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Pet Registration failed. Contact Developer!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void connect() {
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
}