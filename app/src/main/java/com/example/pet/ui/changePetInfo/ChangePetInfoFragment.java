package com.example.pet.ui.changePetInfo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.SharedViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePetInfoFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    public static Dialog progressDialog;
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
    private boolean isImageSelected = false;
    private RadioGroup radioGenderGroup;
    private View view;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_pet_info, container, false);
        ((MainActivity) requireActivity()).hideBottomNavigationView();

        // Initialize the Progress Bar Dialog
        progressDialog = new Dialog(requireActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);

        // Handle the back button event
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Discard change pet info?")
                        .setMessage("Are you sure you want to discard change pet info?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                            navController.navigate(R.id.navigation_settings);
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        // Get the view model.
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            petInfoAvatar.setImageBitmap(bitmap);
                            isImageSelected = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Call the photo picker when user clicks on the avatar.
        petInfoAvatar = view.findViewById(R.id.changePetInfo_imgAvatar);
        petInfoAvatar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Pick Image", Toast.LENGTH_SHORT).show();
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Get the value from the radio button in radio group.
        radioGenderGroup = view.findViewById(R.id.changePetInfo_radioGenderGroup);
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
            // Set the maximum date to the current date.
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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

    private void petInfoUpdate() {
        int id = Objects.requireNonNull(sharedViewModel.getUserClass().getValue()).id;
        String petName, birthDate;
        byte[] imageBytes;
        int age;

        petName = petNameEditText.getText().toString();
        birthDate = userInputDate;

        // Validate the pet name input is not empty.
        if (petName.isEmpty()) {
            petNameEditText.setError("Pet name is required");
            petNameEditText.requestFocus();
            return;
        }

        // Validate the weight input is not empty.
        String weightString = weightEditText.getText().toString();
        if (weightString.isEmpty()) {
            weightEditText.setError("Weight is required");
            weightEditText.requestFocus();
            return;
        }
        // Validate the weight format.
        if (!isValidWeight(weightString)) {
            weightEditText.setError("Invalid weight format. Only one decimal place is allowed");
            weightEditText.requestFocus();
            return;
        }
        float weight = Float.parseFloat(weightString);

        // Validate the gender radio input is not empty.
        int selectedId = radioGenderGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(requireContext(), "Gender is required", Toast.LENGTH_SHORT).show();
            return;
        }
        radioGenderButton = view.findViewById(selectedId);
        String gender = radioGenderButton.getText().toString();

        // Validate the birth date input is not empty.
        if (textDatePicked.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Birth Date is required", Toast.LENGTH_SHORT).show();
            btnDatePicker.requestFocus();
            return;
        }

        // Validate the image is selected.
        if (!isImageSelected) {
            Toast.makeText(requireContext(), "Image is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age = period.getYears();
        } else {
            age = 0;
        }

        // Show the progress dialog
        progressDialog.show();

        // Convert the image to a byte array.
        Bitmap bitmap = ((BitmapDrawable) petInfoAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
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
                requireActivity().runOnUiThread(() -> {
                    // Dismiss the progress dialog
                    progressDialog.dismiss();

                    if (rowCount > 0) {
                        // update successful
                        Toast.makeText(requireActivity(), "Pet Info Updated!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        // update failed
                        Toast.makeText(requireActivity(), "Pet Info Update Failed. Contact Developer!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                // Dismiss the progress dialog in case of an exception
                requireActivity().runOnUiThread(() -> progressDialog.dismiss());
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

    private boolean isValidWeight(String weight) {
        String integerRegex = "^[1-9][0-9]?$|^100$";
        String floatRegex = "^(100\\.0|[1-9][0-9]?\\.\\d|0\\.\\d)$";
        Pattern integerPattern = Pattern.compile(integerRegex);
        Pattern floatPattern = Pattern.compile(floatRegex);
        Matcher integerMatcher = integerPattern.matcher(weight);
        Matcher floatMatcher = floatPattern.matcher(weight);

        if (integerMatcher.matches()) {
            // If the weight is an integer, format it to have one decimal place.
            weightEditText.setText(weight + ".0");
            return true;
        } else if (floatMatcher.matches()) {
            // If the weight is a float with one decimal place, return true.
            return true;
        } else {
            // If the weight doesn't match either regex, return false.
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