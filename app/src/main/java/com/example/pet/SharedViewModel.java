package com.example.pet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet.UserClass;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<UserClass> selectUserInfo = new MutableLiveData<>();

    public void setUserClass(UserClass userClass){
        selectUserInfo.setValue(userClass);
    }

    public LiveData<UserClass> getUserClass() {
        return selectUserInfo;
    }
}