package com.example.pet;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet.UserClass;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<UserClass> selectUserInfo = new MutableLiveData<>();
    private MutableLiveData<Bitmap> petAvatar;
    private MutableLiveData<Bitmap> userAvatar;
    private MutableLiveData<String> petName;
    private MutableLiveData<Integer> petAge;
    private MutableLiveData<Float> petWeight;

    public void setUserClass(UserClass userClass){
        selectUserInfo.setValue(userClass);
    }
    public void setPetAvatar(Bitmap bitmap) {
        getPetAvatar().setValue(bitmap);
    }
    public void setUserAvatar(Bitmap bitmap) {
        getUserAvatar().postValue(bitmap);
    }
    public void setPetName(String name) {
        getPetName().postValue(name);
    }
    public void setPetAge(Integer age) {
        getPetAge().postValue(age);
    }
    public void setPetWeight(Float weight) {
        getPetWeight().postValue(weight);
    }

    public LiveData<UserClass> getUserClass() {
        return selectUserInfo;
    }
    public MutableLiveData<Bitmap> getPetAvatar() {
        if (petAvatar == null) {
            petAvatar = new MutableLiveData<>();
        }
        return petAvatar;
    }
    public MutableLiveData<Bitmap> getUserAvatar() {
        if (userAvatar == null) {
            userAvatar = new MutableLiveData<>();
        }
        return userAvatar;
    }
    public MutableLiveData<String> getPetName() {
        if (petName == null) {
            petName = new MutableLiveData<>();
        }
        return petName;
    }
    public MutableLiveData<Integer> getPetAge() {
        if (petAge == null) {
            petAge = new MutableLiveData<>();
        }
        return petAge;
    }
    public MutableLiveData<Float> getPetWeight() {
        if (petWeight == null) {
            petWeight = new MutableLiveData<>();
        }
        return petWeight;
    }
}