package com.example.myapplication.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PlacemarkCreatedCallback;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.Objects;


public class HomeFragment extends Fragment {

private FragmentHomeBinding binding;
private MapView mapView;
private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private double lat;
    private double lon;
    private boolean isPlacemarkAdded = false;


    private final LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                lat = latitude;
                lon = longitude;
                ModelFactory factory = new ModelFactory(lat, lon);
                HomeViewModel homeViewModel = new ViewModelProvider(HomeFragment.this, factory).get(HomeViewModel.class);
                final TextView textView = binding.textHome;
                homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
                final ProgressBar progressBar = binding.progressBar;
                progressBar.setVisibility(View.GONE);

                if (!isPlacemarkAdded) {
                    PlacemarkMapObject placemark = mapView.getMap().getMapObjects().addPlacemark(new Point(latitude, longitude));

                    placemark.setIcon(ImageProvider.fromResource(getContext(), R.drawable.pins));

                    placemark.setOpacity(0.8f);
                    placemark.setDraggable(true);

                    isPlacemarkAdded = true;
                    Animation animation = new Animation(Animation.Type.LINEAR, 2f);
                    CameraPosition cameraPosition = new CameraPosition(new Point(latitude, longitude), 15f, 0f, 0f);
                    mapView.getMap().move(cameraPosition, animation, null);

                }
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
    mapView = root.findViewById(R.id.mapview);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(binding.getRoot().getContext());
        // Проверяем разрешение на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions((Activity) binding.getRoot().getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Если разрешение уже предоставлено, запускаем метод получения местоположения
            getDeviceLocation();
        }


        return root;
    }

    public void getDeviceLocation() {
        // Запрашиваем обновления местоположения
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Время между обновлениями в миллисекундах

        if (ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);
    }

    @SuppressLint("ShowToast")
    public void onResume() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(binding.getRoot().getContext());
            builder.setMessage("GPS выключен, пожалуйста, включите его для продолжения")
                    .setCancelable(false)
                    .setPositiveButton("Перейти в настройки", (dialog, id) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        super.onResume();
        if (ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions((Activity) binding.getRoot().getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(binding.getRoot().getContext(), "Необходимо предоставить разрешение на доступ к местоположению в настройках приложения", Toast.LENGTH_SHORT).show();
            // Создаем задержку в 3 секунды

                    // Удаляем сообщение об ошибке
                    Toast.makeText(binding.getRoot().getContext(), "", Toast.LENGTH_SHORT).cancel();
                    // Завершаем приложение
                    requireActivity().finishAffinity();
        } else {
            // Если разрешение уже предоставлено, запускаем метод получения местоположения
            getDeviceLocation();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        isPlacemarkAdded = false;
        binding = null;
    }
}