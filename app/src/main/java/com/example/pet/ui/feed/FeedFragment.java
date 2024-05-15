package com.example.pet.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.pet.R;
import com.example.pet.databinding.FragmentFeedBinding;
import com.example.pet.ui.feedAutomatic.FeedAutomaticFragment;
import com.example.pet.ui.feedManual.FeedManualFragment;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button btnManual = root.findViewById(R.id.feed_btnManual);
        Button btnAutomatic = root.findViewById(R.id.feed_btnAutomatic);

        btnManual.setOnClickListener(view -> {
            Fragment fragment = new FeedManualFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });
        btnAutomatic.setOnClickListener(view -> {
            Fragment fragment = new FeedAutomaticFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}