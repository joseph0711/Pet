package com.example.pet.ui.petInfo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.example.pet.UserClass;
import com.example.pet.ui.login.LoginActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetInfoFragment extends Fragment {
    ConnectionMysqlClass connectionMysqlClass;
    UserClass userClass;
    Connection con;
    String str;
    private EditText petNameEditText, weightEditText;
    private RadioButton radioGenderButton;
    private Button btnConfirm, btnDatePicker;
    private TextView textDatePicked, textAge;
    int day, month, year;
    String userInputDate;
    Period period;
    public static PetInfoFragment newInstance() {
        return new PetInfoFragment();
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_info, container, false);

        // Get the value from the radio button in radio group.
        RadioGroup radioGenderGroup = view.findViewById(R.id.petInfo_radioGenderGroup);
        radioGenderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.petInfo_radioBtnMale) {
                radioGenderButton = view.findViewById(R.id.petInfo_radioBtnMale);
            } else {
                radioGenderButton = view.findViewById(R.id.petInfo_radioBtnFemale);
            }
        });

        // Set the date picker for the Birth Date.
        btnDatePicker = view.findViewById(R.id.petInfo_btnDatePicker);
        textDatePicked = view.findViewById(R.id.petInfo_textDatePicked);
        textAge = view.findViewById(R.id.petInfo_textAge);
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

        petNameEditText = view.findViewById(R.id.petInfo_inputName);
        weightEditText = view.findViewById(R.id.petInfo_inputWeight);

        // Make a connection to MySQL.
        connectionMysqlClass = new ConnectionMysqlClass();
        connect();

        // Call the petInfoRegister() method when the Confirm button is clicked.
        btnConfirm = view.findViewById(R.id.petInfo_btnConfirm);
        btnConfirm.setOnClickListener( view2 -> petInfoRegister());
        return view;
    }

    private void petInfoRegister() {
        findUserById(); // Find the user id by the created date and time.
        Log.i("info", "Executed findUserById() method." + "\n id: " + userClass.id);

        String petName, birthDate, gender;
        int age, weight, id;
        petName = petNameEditText.getText().toString();
        birthDate = userInputDate;
        weight = Integer.parseInt(weightEditText.getText().toString());
        gender = radioGenderButton.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age = period.getYears();
        } else {
            age = 0;
        }

        String sql = "INSERT INTO pet (id, Name, Weight, Age, BirthDate, Gender) VALUES (?, ?, ?, ?, ?, ?);";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, userClass.id);
                preparedStatement.setString(2, petName);
                preparedStatement.setInt(3, weight);
                preparedStatement.setInt(4, age);
                preparedStatement.setString(5, birthDate);
                preparedStatement.setString(6, gender);

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

    //TODO: Fix the bug which causes the user id to be 0.（findUserByID bug待處理）
    private void findUserById() {
        userClass = new UserClass();
        String sql = "SELECT id FROM user WHERE (Created) = (?);";
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, userClass.createdDateTime);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userClass.id = resultSet.getInt("id");
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