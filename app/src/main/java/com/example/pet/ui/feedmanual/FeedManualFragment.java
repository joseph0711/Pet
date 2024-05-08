package com.example.pet.ui.feedmanual;

import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pet.R;
import com.example.pet.databinding.ActivityMainBinding;
import com.example.pet.databinding.FragmentFeedBinding;
import com.example.pet.databinding.FragmentFeedManualBinding;
import com.example.pet.ui.feed.FeedFragment;
import com.example.pet.ui.feed.FeedViewModel;

public class FeedManualFragment extends AppCompatActivity {

    private FeedManualViewModel feedManualViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_manual, container, false);
    }


    /*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        feedManualViewModel = new ViewModelProvider(this).get(FeedManualViewModel.class);
        // TODO: Use the ViewModel
    }*/

}