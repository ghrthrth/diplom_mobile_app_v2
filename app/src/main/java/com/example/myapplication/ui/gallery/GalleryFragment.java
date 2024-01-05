package com.example.myapplication.ui.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.example.myapplication.databinding.FragmentSlideshowBinding;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


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
        сircleImageView.setImageResource(R.drawable.test_new);

        return root;
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private static class MyPhoneStateListener extends PhoneStateListener {
        GalleryViewModel galleryViewModel;

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
                int p = signalStrengthPercent.get(0).getDbm();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
