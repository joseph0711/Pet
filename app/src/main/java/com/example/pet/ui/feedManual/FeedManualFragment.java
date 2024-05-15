package com.example.pet.ui.feedManual;

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

import com.example.pet.R;
import com.example.pet.ui.feeding.FeedingFragment;
public class FeedManualFragment extends Fragment {
    private EditText editTextInputAmount;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_manual, container, false);

        // When user clicks the Start button.
        // Call manualFeed()
        editTextInputAmount = view.findViewById(R.id.feedManual_inputAmount);
        Button btnStart = view.findViewById(R.id.feedManual_btnStart);
        btnStart.setOnClickListener(view1 -> manualFeed());
        return view;
    }

    private void manualFeed() {
        int mount;
        mount = Integer.parseInt(editTextInputAmount.getText().toString());

        // Add: Call Arduino to do feeding function...

        // When send the signal to Arduino successfully, go to FeedingFragment.
        Fragment fragment = new FeedingFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();


    }
}