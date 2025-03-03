package com.example.easybuy_qz.Activity.Login.ForgotPW;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy_qz.Database.DatabaseHelper;
import com.example.easybuy_qz.R;

public class CheckEmailActivity extends AppCompatActivity {

    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5;
    private Button btnVerifyCode;
    private TextView tvResendEmail;
    private ImageButton btnBack;
    private String email;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_email);

        dbHelper = new DatabaseHelper(this);
        email = getIntent().getStringExtra("email");

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        tvResendEmail = findViewById(R.id.tvResendEmail);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        setupOTPInputs();

        btnVerifyCode.setOnClickListener(v -> {
            String otp = etOtp1.getText().toString() +
                    etOtp2.getText().toString() +
                    etOtp3.getText().toString() +
                    etOtp4.getText().toString() +
                    etOtp5.getText().toString();
            if (otp.length() == 5) {
                if (dbHelper.verifyOTP(email, otp)) {
                    Intent intent = new Intent(CheckEmailActivity.this, SetNewPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Mã OTP không đúng!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ 5 chữ số!", Toast.LENGTH_SHORT).show();
            }
        });

        tvResendEmail.setOnClickListener(v -> {
            String newOtp = dbHelper.generateAndSaveOTP(email);
            Toast.makeText(this, "Đã gửi lại OTP: " + newOtp, Toast.LENGTH_SHORT).show(); // Hiển thị OTP (cho test)
        });
    }

    private void setupOTPInputs() {
        TextWatcher otpWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    if (etOtp1 == null) return;
                    if (etOtp1.hasFocus()) etOtp2.requestFocus();
                    else if (etOtp2.hasFocus()) etOtp3.requestFocus();
                    else if (etOtp3.hasFocus()) etOtp4.requestFocus();
                    else if (etOtp4.hasFocus()) etOtp5.requestFocus();
                } else if (s.length() == 0) {
                    if (etOtp5.hasFocus()) etOtp4.requestFocus();
                    else if (etOtp4.hasFocus()) etOtp3.requestFocus();
                    else if (etOtp3.hasFocus()) etOtp2.requestFocus();
                    else if (etOtp2.hasFocus()) etOtp1.requestFocus();
                }
            }
        };
        etOtp1.addTextChangedListener(otpWatcher);
        etOtp2.addTextChangedListener(otpWatcher);
        etOtp3.addTextChangedListener(otpWatcher);
        etOtp4.addTextChangedListener(otpWatcher);
        etOtp5.addTextChangedListener(otpWatcher);
    }
}}