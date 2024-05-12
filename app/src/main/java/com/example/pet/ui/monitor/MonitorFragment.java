package com.example.pet.ui.monitor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet.R;
import com.example.pet.databinding.FragmentMonitorBinding;
import com.example.pet.ui.feedManual.FeedManualFragment;
import com.example.pet.ui.monitorLive.MonitorLiveFragment;

public class MonitorFragment extends Fragment {

    private Button btnStart;
    private FragmentMonitorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MonitorViewModel monitorViewModel =
                new ViewModelProvider(this).get(MonitorViewModel.class);
        binding = FragmentMonitorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnStart = root.findViewById(R.id.monitor_btnStart);
        btnStart.setOnClickListener(view -> {
            Fragment fragment = new MonitorLiveFragment();
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