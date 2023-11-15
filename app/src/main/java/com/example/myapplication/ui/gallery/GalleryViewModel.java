package com.example.myapplication.ui.gallery;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.R;

import java.util.List;

public class GalleryViewModel extends ViewModel {
    private MutableLiveData<String> mText = new MutableLiveData<>();

    public void setData(String value) {
        mText.setValue("Data" + value);
    }

    public LiveData<String> getData() {
        return mText;
    }

}