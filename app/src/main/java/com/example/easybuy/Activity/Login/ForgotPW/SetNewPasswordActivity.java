package com.example.easybuy.Activity.Login.ForgotPW;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.R;

public class SetNewPasswordActivity extends AppCompatActivity {

    private EditText etPassword, etConfirmPassword;
    private Button btnUpdatePassword;
    private ImageButton btnBack;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");

        // Khởi tạo các thành phần
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btnBack = findViewById(R.id.btnBack);

        // Xử lý nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý nút Update Password
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                if (password.equals(confirmPassword) && password.length() >= 6) {
                    // Cập nhật mật khẩu trong cơ sở dữ liệu (giả định)
                    if (updatePasswordInDatabase(email, password)) {
                        Intent intent = new Intent(SetNewPasswordActivity.this, PasswordResetSuccessActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SetNewPasswordActivity.this, "Lỗi khi cập nhật mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SetNewPasswordActivity.this, "Mật khẩu không khớp hoặc quá ngắn!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Cập nhật mật khẩu trong cơ sở dữ liệu (giả định)
    private boolean updatePasswordInDatabase(String email, String password) {
        // Thay bằng logic thực tế để cập nhật bảng user_account
        // Ví dụ: Sử dụng DatabaseHelper.updatePassword(email, password);
        return true; // Giả định
    }
}