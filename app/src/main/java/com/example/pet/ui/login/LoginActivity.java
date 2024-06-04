package com.example.pet.ui.login;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private Button btnRegister, btnLogin;
    private EditText emailEditText, passwordEditText;
    private TextView loginTextView;
    private String email, password;
    UserClass userClass = new UserClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        connect();

        // Call the loginCheck() method when the Login button is clicked.
        btnLogin.setOnClickListener(v -> loginCheck());
    }

    private void loginCheck() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
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
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", userClass.getName());
                        editor.putInt("id", userClass.getId());
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    });
                } else {
                    // Login failed
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show());
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
}