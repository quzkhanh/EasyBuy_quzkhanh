package com.example.easybuy.view.Login.ForgotPW;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.view.Login.WelcomeActivity;
import com.example.easybuy.R;

public class PasswordResetSuccessActivity extends AppCompatActivity {

    private Button btnConfirm;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_success);

        // Khởi tạo các thành phần
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        // Xử lý nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý nút Confirm
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về màn hình Login
                Intent intent = new Intent(PasswordResetSuccessActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}