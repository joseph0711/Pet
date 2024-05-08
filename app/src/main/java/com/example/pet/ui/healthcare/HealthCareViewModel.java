package com.example.pet.ui.healthcare;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HealthCareViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public HealthCareViewModel() {
        mText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }
}