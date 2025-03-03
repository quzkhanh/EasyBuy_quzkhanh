package com.example.easybuy_qz.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy_qz.Activity.Login.Admin.AdminLoginActivity;
import com.example.easybuy_qz.Activity.Login.User.UserLoginActivity;
import com.example.easybuy_qz.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnUser, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Ánh xạ các button
        btnUser = findViewById(R.id.btnUser);
        btnAdmin = findViewById(R.id.btnAdmin);

        // Xử lý sự kiện khi nhấn nút "Tôi là người mua hàng"
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, UserLoginActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện khi nhấn nút "Tôi là người bán"
        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}
