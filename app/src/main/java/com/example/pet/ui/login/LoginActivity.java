package com.example.pet.ui.login;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pet.ConnectionMysqlClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.UserClass;
import com.example.pet.databinding.ActivityLoginBinding;
import com.example.pet.ui.register.RegisterFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private Button btnRegister, btnLogin;
    private EditText emailEditText, passwordEditText;
    private TextView loginTextView;
    private String email, password;
    private Dialog progressDialog;
    UserClass userClass = new UserClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnRegister = findViewById(R.id.register);
        btnLogin = findViewById(R.id.login);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginTextView = findViewById(R.id.login_textTitle);

        // Hide the Login page buttons and fields when the Register button is clicked.
        btnRegister.setOnClickListener(view -> {
            btnRegister.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            emailEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            loginTextView.setVisibility(View.GONE);
            Fragment fragment = new RegisterFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });

        connectionMysqlClass = new ConnectionMysqlClass();
        connectMySql();


        // Initialize the Progress Bar Dialog
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);

        // Call the loginCheck() method when the Login button is clicked.
        btnLogin.setOnClickListener(v -> {
            loginCheck();
        });
    }

    private void loginCheck() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

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

        // Show the progress bar and disable the login button
        progressDialog.show();
        btnLogin.setEnabled(false);

        String sql = "SELECT * FROM user WHERE Email = ? and Password = ?";
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) { // Login successful
                    userClass.setName(resultSet.getString("UserName"));
                    userClass.setId(resultSet.getInt("id"));
                    runOnUiThread(() -> {
                        // Store user's name and id in SharedPreferences
                        SharedPreferences userInfoSharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor userInfoEditor = userInfoSharedPreferences.edit();
                        userInfoEditor.putString("name", userClass.getName());
                        userInfoEditor.putInt("id", userClass.getId());
                        userInfoEditor.apply();

                        // Store user's login status in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        // Hide the progress bar when login is successful
                        progressDialog.dismiss();
                        btnLogin.setEnabled(true);
                    });
                } else {
                    // Login failed
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();

                        // Hide the progress bar when login is failed
                        progressDialog.dismiss();
                        btnLogin.setEnabled(true);
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void connectMySql() {
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                con = connectionMysqlClass.CONN();
                if (con == null) {
                    runOnUiThread(() -> {
                        str = "Error in connection with SQL server";
                        Log.e("ERROR", str);
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
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