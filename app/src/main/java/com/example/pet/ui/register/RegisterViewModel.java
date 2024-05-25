package com.example.pet.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet.UserClass;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<UserClass> selectUserInfo = new MutableLiveData<UserClass>();

    public void setUserClass(UserClass userClass){
        selectUserInfo.setValue(userClass);
    }

    public LiveData<UserClass> getUserClass() {
        return selectUserInfo;
    }
}