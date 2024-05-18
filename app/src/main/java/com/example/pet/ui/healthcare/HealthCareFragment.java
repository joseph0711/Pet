package com.example.pet.ui.healthcare;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pet.R;
import com.example.pet.databinding.FragmentHealthCareBinding;
import com.example.pet.databinding.FragmentHomeBinding;
import com.example.pet.ui.home.HomeViewModel;

public class HealthCareFragment extends Fragment {
    private @NonNull FragmentHealthCareBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HealthCareViewModel healthCareViewModel =
                new ViewModelProvider(this).get(HealthCareViewModel.class);

        binding = FragmentHealthCareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}