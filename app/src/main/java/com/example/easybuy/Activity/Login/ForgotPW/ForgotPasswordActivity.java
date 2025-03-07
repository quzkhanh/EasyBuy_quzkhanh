package com.example.easybuy.Activity.Login.ForgotPW;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Database.DatabaseHelper.DatabaseHelper;
import com.example.easybuy.Database.DatabaseHelper.UserDatabaseHelper;
import com.example.easybuy.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnResetPassword;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private UserDatabaseHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo DatabaseHelper và UserDatabaseHelper
        dbHelper = new DatabaseHelper(this);
        userDbHelper = new UserDatabaseHelper(dbHelper);

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (isValidEmail(email)) {
                if (userDbHelper.checkEmail(email)) {
                    if (userDbHelper.generateAndSaveOTP(email)) {
                        Toast.makeText(this, "Mã OTP đã được gửi (kiểm tra email để lấy OTP)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, CheckEmailActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Không thể tạo OTP, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern) && !email.isEmpty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDbHelper != null) {
            userDbHelper.close(); // Đóng UserDatabaseHelper
        }
    }
}