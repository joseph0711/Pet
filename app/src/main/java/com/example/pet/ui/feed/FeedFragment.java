package com.example.pet.ui.feed;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.pet.R;
import com.example.pet.databinding.FragmentFeedBinding;
import com.example.pet.ui.feedmanual.FeedManualFragment;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private Button button;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FeedViewModel feedViewModel =
                new ViewModelProvider(this).get(FeedViewModel.class);
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            button = binding.getRoot().requireViewById(R.id.feed_btnManual);
        }
        button.setOnClickListener(view -> {
            Intent intent = new Intent();
            startActivity(new Intent(requireActivity(), FeedManualFragment.class));
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}