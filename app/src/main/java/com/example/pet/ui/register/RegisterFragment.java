package com.example.pet.ui.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.app.Activity;
import android.Manifest;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.UserClass;
import com.example.pet.ui.login.LoginActivity;
import com.example.pet.ui.petInfo.PetInfoFragment;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str; // put into local variable.
    private EditText nameEditText, emailEditText, passwordEditText;
    private ImageView registerAvatar;
    private SharedViewModel sharedViewModel;
    ActivityResultLauncher<Intent> resultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Get the view model.
        sharedViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.NewInstanceFactory()).get(SharedViewModel.class);

        // Call the photo picker when user clicks on the avatar.
        registerAvatar = view.findViewById(R.id.register_imgAvatar);
        registerAvatar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Pick Image", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        nameEditText = view.findViewById(R.id.register_inputName);
        emailEditText = view.findViewById(R.id.register_inputEmail);
        passwordEditText = view.findViewById(R.id.register_inputPwd);
        Button btnSubmit = view.findViewById(R.id.register_btnContinue);

        connectionMysqlClass = new ConnectionMysqlClass();
        connect();

        // Call the sendDataToMySQL() method when the Submit button is clicked.
        btnSubmit.setOnClickListener(v -> register());
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
                registerAvatar.setImageURI(imageUri);
            }
        });
    }

    // Pick image from gallery.
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    // Register the user.
    private void register() {
        String name, email, password, createdDateTime;
        byte[] imageBytes;

        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Get the current date and time and store it in the createdDateTime variable.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createdDateTime = dateFormat.format(calendar.getTime());

        // Convert the image to a byte array.
        Bitmap bitmap = ((BitmapDrawable) registerAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
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
                if (rowCount > 0) {
                    // User Registration Successful
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireActivity(), "User Register Successfully", Toast.LENGTH_SHORT).show();
                        Fragment fragment = new PetInfoFragment();
                        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container, fragment).commit();
                    });
                } else {
                    // User Registration failed
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "User Register Failed. Contact Developer!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception ex) {
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
    private void connect() {
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