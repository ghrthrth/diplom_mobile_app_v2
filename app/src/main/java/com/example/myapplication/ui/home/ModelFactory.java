package com.example.myapplication.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final double lat;
    private final double lon;

    public ModelFactory(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(lat, lon);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}