package com.example.pet.ui.changePetInfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.ui.login.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangePetInfoFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private EditText petNameEditText, weightEditText;
    private RadioButton radioGenderButton;
    private Button btnConfirm, btnDatePicker;
    private TextView textDatePicked, textAge;
    private ImageView petInfoAvatar;
    int day, month, year;
    String userInputDate;
    Period period;
    ActivityResultLauncher<Intent> resultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_pet_info, container, false);

        // Get the view model.
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Call the photo picker when user clicks on the avatar.
        petInfoAvatar = view.findViewById(R.id.changePetInfo_imgAvatar);
        petInfoAvatar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Pick Image", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        // Get the value from the radio button in radio group.
        RadioGroup radioGenderGroup = view.findViewById(R.id.changePetInfo_radioGenderGroup);
        radioGenderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.changePetInfo_radioBtnMale) {
                radioGenderButton = view.findViewById(R.id.changePetInfo_radioBtnMale);
            } else {
                radioGenderButton = view.findViewById(R.id.changePetInfo_radioBtnFemale);
            }
        });

        // Set the date picker for the Birth Date.
        btnDatePicker = view.findViewById(R.id.changePetInfo_btnDatePicker);
        textDatePicked = view.findViewById(R.id.changePetInfo_textDatePicked);
        textAge = view.findViewById(R.id.changePetInfo_textAge);
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

        petNameEditText = view.findViewById(R.id.changePetInfo_inputName);
        weightEditText = view.findViewById(R.id.changePetInfo_inputWeight);

        // Make a connection to MySQL.
        connectionMysqlClass = new ConnectionMysqlClass();
        connectMysql();

        // Call the petInfoUpdate() method when the Confirm button is clicked.
        btnConfirm = view.findViewById(R.id.changePetInfo_btnConfirm);
        btnConfirm.setOnClickListener( view1 -> petInfoUpdate());
        return view;
    }

    // Request permission to pick image from gallery.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for permission to read external storage.
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                pickImage();
            } else {
                Toast.makeText(requireContext(), "Permission required to pick image", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the ActivityResultLauncher instance for handling the result of an activity that is started for a result.
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Uri imageUri = result.getData().getData();
                petInfoAvatar.setImageURI(imageUri);
            }
        });
    }

    // Pick image from gallery.
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    private void petInfoUpdate() {
        int id = Objects.requireNonNull(sharedViewModel.getUserClass().getValue()).id;
        String petName, birthDate, gender;
        byte[] imageBytes;
        int age;
        float weight;
        petName = petNameEditText.getText().toString();
        birthDate = userInputDate;
        weight = Float.parseFloat(weightEditText.getText().toString());
        gender = radioGenderButton.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age = period.getYears();
        } else {
            age = 0;
        }

        // Convert the image to a byte array.
        Bitmap bitmap = ((BitmapDrawable) petInfoAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        imageBytes = outputStream.toByteArray();

        String sql = "UPDATE pet SET PetName = ?, Weight = ?, Age = ?, BirthDate = ?, Gender = ?, PetImage = ? WHERE id = ?;";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, petName);
                preparedStatement.setFloat(2, weight);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, birthDate);
                preparedStatement.setString(5, gender);
                preparedStatement.setBytes(6, imageBytes);
                preparedStatement.setInt(7, id);

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
}