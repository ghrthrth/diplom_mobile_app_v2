package com.example.myapplication.ui.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.example.myapplication.databinding.FragmentSlideshowBinding;

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
        final CircleImageView сircleImageView = binding.imageView3;
        final Button send = binding.send;
        сircleImageView.setImageResource(R.drawable.test_new);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username and dbm values
                String lat = "lat"; // Replace with the actual username
                String lon = "lon"; // Replace with the actual username
                int dbm = myPhoneStateListener.getDbm();

                // Send the username and dbm values to the server
                sendToServer(lat, lon, dbm);
            }
        });

        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendToServer(String lat, String lon, int dbm) {
        // Create an instance of the HttpRequestTask and execute it
        HttpRequestTask httpRequestTask = new HttpRequestTask();
        httpRequestTask.execute(lat, lon, String.valueOf(dbm));
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
