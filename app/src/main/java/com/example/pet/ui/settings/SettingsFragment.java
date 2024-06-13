package com.example.pet.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.databinding.FragmentSettingsBinding;
import com.example.pet.ui.login.LoginActivity;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnModify = root.findViewById(R.id.settings_btnModify);
        btnModify.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.changeUserInfoFragment);
        });

        Button btnLogout = root.findViewById(R.id.settings_btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showBottomNavigationView();
    }
}