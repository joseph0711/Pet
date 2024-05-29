package com.example.pet;

import android.util.Log;

import com.example.pet.ui.feeding.FeedingFragment;

public class MessageArrivedListener {
    public String result;
    public void messageArrived() {
        FeedingFragment feedingFragment = new FeedingFragment();
        feedingFragment.updateTextView(result);
    }
}
