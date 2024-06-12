package com.example.pet;

import com.example.pet.ui.feeding.FeedingFragment;

public class MessageArrivedListener {
    public String result;
    private FeedingFragment feedingFragment;

    public MessageArrivedListener(FeedingFragment feedingFragment) {
        this.feedingFragment = feedingFragment;
    }
    public void messageArrived() {
        if (feedingFragment != null) {
            feedingFragment.updateTextView(result);
        }
    }
}
