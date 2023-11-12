package com.example.myapplication.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private double latitude;
    private double longitude;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment" + "::");
        Log.d("Sex", "ferf" + latitude + " " + longitude);
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LiveData<String> getText() {
        return mText;
    }
}