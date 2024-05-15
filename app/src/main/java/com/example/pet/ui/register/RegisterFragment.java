package com.example.pet.ui.register;

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
import com.example.pet.R;
import com.example.pet.ui.petInfo.PetInfoFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection con;
    String str;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button btnSubmit;
    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        nameEditText = view.findViewById(R.id.register_inputName);
        emailEditText = view.findViewById(R.id.register_inputEmail);
        passwordEditText = view.findViewById(R.id.register_inputPwd);
        btnSubmit = view.findViewById(R.id.register_btnContinue);

        connectionClass = new ConnectionClass();
        connect();

        // Call the sendDataToMySQL() method when the Submit button is clicked.
        btnSubmit.setOnClickListener(v -> register());
        return view;
    }

    private void register() {
        String name, email, password;
        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        String sql = "INSERT INTO user (Name, Email, Password) VALUES (?, ?, ?);";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);

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