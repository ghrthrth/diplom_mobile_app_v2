package com.example.myapplication.ui.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.example.myapplication.databinding.FragmentSlideshowBinding;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.home.HomeViewModel;
import com.example.myapplication.ui.home.ModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class GalleryFragment extends Fragment {
    MyPhoneStateListener myPhoneStateListener;
    public TelephonyManager tel;
    private FragmentGalleryBinding binding;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private double lat;
    private double lon;

    private final LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                lat = latitude;
                lon = longitude;
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        myPhoneStateListener = new MyPhoneStateListener(galleryViewModel);
        tel = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);
        tel.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        final TextView textView = binding.textGallery;
        galleryViewModel.getData().observe(getViewLifecycleOwner(), textView::setText);
        final CircleImageView circleImageView = binding.imageView3;
        final Button send = binding.send;
        circleImageView.setImageResource(R.drawable.test_new);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(binding.getRoot().getContext());
        // Проверяем разрешение на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions((Activity) binding.getRoot().getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Если разрешение уже предоставлено, запускаем метод получения местоположения
            getDeviceLocation();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dbm = myPhoneStateListener.getDbm();

                // Send the username and dbm values to the server
                sendToServer(lat, lon, dbm);
            }
        });

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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private static class MyPhoneStateListener extends PhoneStateListener {
        GalleryViewModel galleryViewModel;
        private int dbm;

        public MyPhoneStateListener(GalleryViewModel galleryViewModel) {
            this.galleryViewModel = galleryViewModel;
        }

        @Override
        @SuppressLint("SetTextI18n")
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            List<CellSignalStrength> signalStrengthPercent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Now you can use the signalStrengthPercent as needed
                signalStrengthPercent = signalStrength.getCellSignalStrengths();
                galleryViewModel.setData("" + signalStrengthPercent);
                dbm = signalStrengthPercent.get(0).getDbm();
            }
        }

        public int getDbm() {
            return dbm;
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        binding = null;
    }

    private void sendToServer(double lat, double lon, int dbm) {
        // Create an instance of the HttpRequestTask and execute it
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.execute(String.valueOf(lat), String.valueOf(lon), String.valueOf(dbm));
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String lat = params[0];
            String lon = params[1];
            String dbm = params[2];

            JSONObject json = new JSONObject();

            try {
                json.put("lat", lat);
                json.put("lon", lon);
                json.put("dbm", dbm);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url("https://claimbe.store/diplom/index.php")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                assert response.body() != null;
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.has("success")) {
                    // Display a success message
                    Toast.makeText(getContext(), jsonResponse.getString("success"), Toast.LENGTH_SHORT).show();
                } else if (jsonResponse.has("error")) {
                    // Display an error message
                    Toast.makeText(getContext(), "Ошибка: " + jsonResponse.getString("error") + jsonResponse.getString("dds") + jsonResponse.getString("dda") + jsonResponse.getString("dd"), Toast.LENGTH_SHORT).show();
                } else {
                    // Handle other cases or unknown response
                    Toast.makeText(getContext(), "Неизвестный ответ от сервера", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                Toast.makeText(getContext(), "Ошибка при обработке ответа от сервера" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
