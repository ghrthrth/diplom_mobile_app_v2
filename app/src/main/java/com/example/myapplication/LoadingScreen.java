package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingScreen extends AppCompatActivity {
    private static final int DELAY_MILLIS = 3000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        // Check if the user is already signed in

        new Handler().postDelayed(() -> {
                    LoadingScreen.this.startActivity(new Intent(LoadingScreen.this, MainActivity.class));
                },
                DELAY_MILLIS);
    }
}
