package com.example.pet.ui.login;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pet.ConnectionClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.databinding.ActivityLoginBinding;
import com.example.pet.ui.register.RegisterFragment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    ConnectionClass connectionClass;
    Connection con;
    String str;
    private Button btnRegister, btnLogin;
    private EditText emailEditText, passwordEditText;
    private TextView loginTextView;
    private String email, password;

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

        connectionClass = new ConnectionClass();
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
                if (resultSet.next()) {
                    // Login successful
                    runOnUiThread(() -> {
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
                con = connectionClass.CONN();
                if (con == null) {
                    runOnUiThread(() -> {
                        str = "Error in connection with SQL server";
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}