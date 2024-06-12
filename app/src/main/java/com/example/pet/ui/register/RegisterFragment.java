package com.example.pet.ui.register;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.UserClass;
import com.example.pet.ui.login.LoginActivity;
import com.example.pet.ui.petInfo.PetInfoFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str; // put into local variable.
    private EditText nameEditText, emailEditText, passwordEditText;
    private ImageView registerAvatar;
    private SharedViewModel sharedViewModel;
    ActivityResultLauncher<Intent> resultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    public static Dialog progressDialog;
    private boolean isImageSelected = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Handle the back button event
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Discard registration?")
                        .setMessage("Are you sure you want to discard your registration?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Use NavController to navigate back to FeedFragment
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        // Get the view model.
        sharedViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.NewInstanceFactory()).get(SharedViewModel.class);

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            registerAvatar.setImageBitmap(bitmap);
                            isImageSelected = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Call the photo picker when user clicks on the avatar.
        registerAvatar = view.findViewById(R.id.register_imgAvatar);
        registerAvatar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Pick Image", Toast.LENGTH_SHORT).show();
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        nameEditText = view.findViewById(R.id.register_inputName);
        emailEditText = view.findViewById(R.id.register_inputEmail);
        passwordEditText = view.findViewById(R.id.register_inputPwd);
        Button btnSubmit = view.findViewById(R.id.register_btnContinue);

        connectionMysqlClass = new ConnectionMysqlClass();
        connectMySql();

        // Initialize the Progress Bar Dialog
        progressDialog = new Dialog(requireActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);

        // Call the sendDataToMySQL() method when the Submit button is clicked.
        btnSubmit.setOnClickListener(v -> register());
        return view;
    }

    // Register the user.
    private void register() {
        String name, email, password, createdDateTime;
        byte[] imageBytes;

        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        // Validate the name input is not empty.
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        // Validate the email input is not empty.
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        // Validate the email format.
        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid email format");
            emailEditText.requestFocus();
            return;
        }

        // Validate the password input is not empty.
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Validate the image is selected.
        if (!isImageSelected) {
            Toast.makeText(requireContext(), "Image is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the progress dialog
        progressDialog.show();

        // Get the current date and time and store it in the createdDateTime variable.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createdDateTime = dateFormat.format(calendar.getTime());

        // Convert the image to a byte array.
        Bitmap bitmap = ((BitmapDrawable) registerAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        imageBytes = outputStream.toByteArray();

        String sql = "INSERT INTO user (UserName, Email, Password, AccountCreatedTime, UserImage) VALUES (?, ?, ?, ?, ?);";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, createdDateTime);
                preparedStatement.setBytes(5, imageBytes);

                int rowCount = preparedStatement.executeUpdate();
                requireActivity().runOnUiThread(() -> {
                    // Dismiss the progress dialog
                    progressDialog.dismiss();

                    if (rowCount > 0) {
                        // User Registration Successful
                        Toast.makeText(requireActivity(), "User Register Successfully", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new PetInfoFragment();
                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment).commit();
                    } else {
                        // User Registration failed
                        Toast.makeText(requireActivity(), "User Register Failed. Contact Developer!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                // Dismiss the progress dialog in case of an exception
                requireActivity().runOnUiThread(() -> progressDialog.dismiss());
                throw new RuntimeException(ex);
            }
        });

        // Set the created date and time in the UserClass object.
        UserClass userClass = new UserClass();
        userClass.setCreatedDateTime(createdDateTime);

        // Then, Set the UserClass object from UserClass in the ViewModel.
        sharedViewModel.setUserClass(userClass);
    }

    // Connect to the MySQL database.
    private void connectMySql() {
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}