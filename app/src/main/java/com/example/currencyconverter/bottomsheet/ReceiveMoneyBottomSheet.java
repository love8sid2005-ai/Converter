package com.example.currencyconverter.bottomsheet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.currencyconverter.R;
import com.example.currencyconverter.utils.SessionManager;

public class ReceiveMoneyBottomSheet extends BaseBottomSheet {

    private ImageView ivQrCode;
    private TextView tvUserInfo;
    private Button btnShare, btnClose;

    private SessionManager sessionManager;

    @Override
    protected int getLayoutId() {
        return R.layout.bottom_sheet_receive_money;
    }

    @Override
    protected void setupViews(@NonNull View view) {
        sessionManager = new SessionManager(requireContext());

        ivQrCode = view.findViewById(R.id.ivQrCode);
        tvUserInfo = view.findViewById(R.id.tvUserInfo);
        btnShare = view.findViewById(R.id.btnShare);
        btnClose = view.findViewById(R.id.btnClose);

        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        tvUserInfo.setText(userName + " · " + userEmail);

        // Временно используем заглушку
        ivQrCode.setImageResource(R.drawable.ic_qr_placeholder);

        btnShare.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Функция поделиться", Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(v -> dismissSheet());
    }
}