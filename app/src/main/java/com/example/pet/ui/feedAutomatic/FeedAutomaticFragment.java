package com.example.pet.ui.feedAutomatic;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pet.R;
import com.example.pet.ui.feeding.FeedingFragment;

public class FeedAutomaticFragment extends Fragment {

    private FeedAutomaticViewModel mViewModel;

    public static FeedAutomaticFragment newInstance() {
        return new FeedAutomaticFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_automatic, container, false);
        Button btnAppoitment = view.findViewById(R.id.feedAuto_btnAppointment);
        btnAppoitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FeedingFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment).commit();
            }
        });
        return view;
    }

    /*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedAutomaticViewModel.class);
        // TODO: Use the ViewModel
    }*/
}