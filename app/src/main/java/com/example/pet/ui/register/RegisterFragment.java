package com.example.pet.ui.register;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.UserClass;
import com.example.pet.ui.petInfo.PetInfoFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button btnSubmit;
    private RegisterViewModel registerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Get the view model.
        registerViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.NewInstanceFactory()).get(RegisterViewModel.class);

        nameEditText = view.findViewById(R.id.register_inputName);
        emailEditText = view.findViewById(R.id.register_inputEmail);
        passwordEditText = view.findViewById(R.id.register_inputPwd);
        btnSubmit = view.findViewById(R.id.register_btnContinue);

        connectionMysqlClass = new ConnectionMysqlClass();
        connect();

        // Call the sendDataToMySQL() method when the Submit button is clicked.
        btnSubmit.setOnClickListener(v -> register());
        return view;
    }

    private void register() {
        String name, email, password, createdDateTime;

        // Get the current date and time and store it in the createdDateTime variable.
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createdDateTime = dateFormat.format(calendar.getTime());

        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        String sql = "INSERT INTO user (Name, Email, Password, Created) VALUES (?, ?, ?, ?);";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, createdDateTime);

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
        registerViewModel.setUserClass(userClass);
    }

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