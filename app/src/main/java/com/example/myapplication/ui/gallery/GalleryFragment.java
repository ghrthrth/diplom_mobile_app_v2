package com.example.myapplication.ui.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.databinding.FragmentSlideshowBinding;

import java.util.List;


public class GalleryFragment extends Fragment {
    MyPhoneStateListener myPhoneStateListener;
    public TelephonyManager tel;
    private FragmentSlideshowBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        final TextView textView = binding.textSlideshow;
        galleryViewModel.getData().observe(getViewLifecycleOwner(), textView::setText);
        myPhoneStateListener = new MyPhoneStateListener(galleryViewModel); // Initialize the MyPhoneStateListener and pass the galleryViewModel

        // Register the PhoneStateListener to listen for signal strengths
        tel = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);


/*        if (tel != null) {
            CellInfo cellInfo = tel.getAllCellInfo().get(0);
            if (cellInfo instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsmSignalStrength = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.d("Penis", "GSM signal strength: " + gsmSignalStrength.getRssi() + " dBm");
                }
            } else if (cellInfo instanceof CellInfoLte) {
                CellSignalStrengthLte lteSignalStrength = ((CellInfoLte) cellInfo).getCellSignalStrength();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("Penis", "LTE signal param" + lteSignalStrength.getRsrq() + "rsrq" + lteSignalStrength.getRsrp() + "rsrp" + lteSignalStrength.getRssnr() + "rssnr" + lteSignalStrength.getCqi() + "cqi" + lteSignalStrength.getDbm() + "dbm");
                    //galleryViewModel.setData("LTE signal param" + lteSignalStrength.getRsrq() + "rsrq" + lteSignalStrength.getRsrp() + "rsrp" + lteSignalStrength.getRssi() + "rssi" + lteSignalStrength.getRssnr() + "rssnr" + lteSignalStrength.getCqi() + "cqi" + lteSignalStrength.getDbm() + "dbm");
                }
            } else if (cellInfo instanceof CellInfoCdma) {
                CellSignalStrengthCdma cdmaSignalStrength = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                Log.d("Penis", "CDMA signal strength: " + cdmaSignalStrength.getCdmaDbm() + " dBm");
            }
        }*/
        tel.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        return root;
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
                Log.d("Penis", "fer" +signalStrengthPercent);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
