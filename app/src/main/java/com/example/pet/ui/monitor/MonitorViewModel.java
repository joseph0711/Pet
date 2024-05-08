package com.example.pet.ui.monitor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MonitorViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MonitorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is monitor fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}