package com.example.myapplication.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {
    private final MutableLiveData<String> mText = new MutableLiveData<>();

    public void setData(String value) {
        mText.setValue("Data: " + value);
    }

    public LiveData<String> getData() {
        return mText;
    }

}