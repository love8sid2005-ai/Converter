package com.example.currencyconverter.bottomsheet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.currencyconverter.R;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.views.AmountEditText;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QrScannerBottomSheet extends BottomSheetDialogFragment {

    public interface OnQrScannedListener {
        void onQrScanned(String result);
    }

    private OnQrScannedListener qrScannedListener;

    public void setOnQrScannedListener(OnQrScannedListener listener) {
        this.qrScannedListener = listener;
    }

    private ImageView ivQrDisplay, ivClose;
    private TextView tvScanResult, tvInstructions;
    private Button btnManualInput, btnGenerateQr, btnCancel;
    private TextView preset10, preset50, preset100, preset500;
    private AmountEditText etCustomAmount;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_qr_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        initializeViews(view);
        setupClickListeners();
        generatePlaceholderQrCode();
        setupPresets();
    }

    private void initializeViews(View view) {
        ivQrDisplay = view.findViewById(R.id.ivQrDisplay);
        ivClose = view.findViewById(R.id.ivClose);
        tvScanResult = view.findViewById(R.id.tvScanResult);
        tvInstructions = view.findViewById(R.id.tvInstructions);
        btnManualInput = view.findViewById(R.id.btnManualInput);
        btnGenerateQr = view.findViewById(R.id.btnGenerateQr);
        btnCancel = view.findViewById(R.id.btnCancel);

        preset10 = view.findViewById(R.id.preset10);
        preset50 = view.findViewById(R.id.preset50);
        preset100 = view.findViewById(R.id.preset100);
        preset500 = view.findViewById(R.id.preset500);
        etCustomAmount = view.findViewById(R.id.etCustomAmount);
    }

    private void setupPresets() {
        preset10.setOnClickListener(v -> etCustomAmount.setAmount(10));
        preset50.setOnClickListener(v -> etCustomAmount.setAmount(50));
        preset100.setOnClickListener(v -> etCustomAmount.setAmount(100));
        preset500.setOnClickListener(v -> etCustomAmount.setAmount(500));
    }

    private void generatePlaceholderQrCode() {
        Bitmap placeholder = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(placeholder);
        canvas.drawColor(Color.parseColor("#1E2229"));

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#F7A600"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);

        canvas.drawRect(20, 20, 280, 280, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);
        paint.setColor(Color.parseColor("#F7A600"));
        canvas.drawText("QR-КОД", 110, 150, paint);

        ivQrDisplay.setImageBitmap(placeholder);
    }

    private void setupClickListeners() {
        ivQrDisplay.setOnClickListener(v -> {
            double amount = etCustomAmount.getAmount();
            if (amount <= 0) {
                Toast.makeText(requireContext(), "Введите сумму платежа", Toast.LENGTH_SHORT).show();
                return;
            }
            String demoResult = "PAYMENT:USER123:AMOUNT:" + (int)amount;
            processQrResult(demoResult);
        });

        btnManualInput.setOnClickListener(v -> {
            double amount = etCustomAmount.getAmount();
            if (amount <= 0) {
                Toast.makeText(requireContext(), "Введите сумму для платежа", Toast.LENGTH_SHORT).show();
                return;
            }
            String qrData = "PAYMENT:" + sessionManager.getUserEmail() + ":AMOUNT:" + (int)amount;
            processQrResult(qrData);
        });

        btnGenerateQr.setOnClickListener(v -> showGenerateQrDialog());
        btnCancel.setOnClickListener(v -> dismiss());
        ivClose.setOnClickListener(v -> dismiss());
    }

    private void showGenerateQrDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_generate_qr, null);
        builder.setView(dialogView);

        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        ImageView ivPreview = dialogView.findViewById(R.id.ivPreview);
        Button btnGenerate = dialogView.findViewById(R.id.btnGenerate);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancel);
        Button btnShare = dialogView.findViewById(R.id.btnShare);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        btnGenerate.setOnClickListener(v -> {
            String amount = etAmount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (amount.isEmpty()) {
                Toast.makeText(requireContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap placeholder = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(placeholder);
            canvas.drawColor(Color.parseColor("#1E2229"));

            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#F7A600"));
            paint.setTextSize(16);
            canvas.drawText("QR ДЛЯ: $" + amount, 30, 100, paint);
            if (!description.isEmpty()) {
                canvas.drawText(description, 30, 130, paint);
            }

            ivPreview.setImageBitmap(placeholder);
            ivPreview.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "QR-код сгенерирован", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "QR-код сохранен", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancelDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void processQrResult(String qrData) {
        double amount = etCustomAmount.getAmount();
        tvScanResult.setText("РЕЗУЛЬТАТ: " + qrData);
        tvInstructions.setText("QR-код успешно распознан");

        if (qrScannedListener != null) {
            qrScannedListener.onQrScanned(qrData);
        }

        if (sessionManager.getUserId() != -1) {
            Transaction transaction = new Transaction(
                    0,
                    sessionManager.getUserId(),
                    "QR-платеж",
                    "Сумма: $" + (int)amount,
                    0.0,
                    0.0,
                    "",
                    "",
                    "qr_payment",
                    "completed",
                    System.currentTimeMillis(),
                    R.drawable.ic_qr
            );
            dbHelper.addTransaction(transaction);
        }

        Toast.makeText(requireContext(), "Платеж на сумму $" + (int)amount + " выполнен!", Toast.LENGTH_LONG).show();
        vibrate();
        dismiss();
    }

    private void vibrate() {
        try {
            android.os.Vibrator vibrator = (android.os.Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(100);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}