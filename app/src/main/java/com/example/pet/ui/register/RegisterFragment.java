package com.example.pet.ui.register;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pet.R;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Button btnSubmit;
    private String name, email, password;
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel
    }

}