package com.example.myapplication.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    GalleryFragment galleryFragment;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("ferf");
    }



    public LiveData<String> getText() {
        return mText;
    }


}