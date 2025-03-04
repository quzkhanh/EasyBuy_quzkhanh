package com.example.easybuy.Activity.Login.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Database.DatabaseHelper;
import com.example.easybuy.Model.Admin;
import com.example.easybuy.R;

public class AdminSignUpActivity extends AppCompatActivity {
    private EditText edtAdminName, edtAdminEmail, edtAdminPassword, edtRepeatAdminPW;
    private Button btnSignUp;
    private DatabaseHelper databaseHelper; // Thay AdminDAO bằng DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

        // Ánh xạ view
        edtAdminName = findViewById(R.id.edtUserName);
        edtAdminEmail = findViewById(R.id.edtUserEmail);
        edtAdminPassword = findViewById(R.id.edtUserPassword);
        edtRepeatAdminPW = findViewById(R.id.edtRepeatPW);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Xử lý sự kiện đăng ký
        btnSignUp.setOnClickListener(v -> registerAdmin());
    }

    private void registerAdmin() {
        String fullName = edtAdminName.getText().toString().trim();
        String email = edtAdminEmail.getText().toString().trim();
        String password = edtAdminPassword.getText().toString().trim();
        String repeatPassword = edtRepeatAdminPW.getText().toString().trim();

        // Kiểm tra nhập liệu
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra email đã tồn tại
        if (databaseHelper.checkAdminEmail(email)) { // Sử dụng checkAdminEmail từ DatabaseHelper
            Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Admin mới
        Admin newAdmin = new Admin(fullName, email, password);
        long adminId = databaseHelper.addAdmin(newAdmin);

        if (adminId > 0) {
            // Lưu adminId vào SharedPreferences để sử dụng sau này
            SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("adminId", (int) adminId);
            editor.apply();

            Toast.makeText(this, "Đăng ký Admin thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không cần close() riêng vì DatabaseHelper tự quản lý kết nối
    }
}