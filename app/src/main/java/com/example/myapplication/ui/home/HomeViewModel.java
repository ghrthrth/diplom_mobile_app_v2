package com.example.myapplication.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel(double lat, double lon) {
        mText = new MutableLiveData<>();
        mText.setValue("Ваши координаты - " + lat + "долготы " + lon + "широты");
        //Log.d("Sex", "fe" + id);
    }

    public LiveData<String> getText() {
        return mText;
    }
}