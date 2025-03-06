package com.example.easybuy.Activity.Login.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Database.AdminDAO;
import com.example.easybuy.Model.Admin;
import com.example.easybuy.R;

public class AdminSignUpActivity extends AppCompatActivity {
    private EditText edtAdminName, edtAdminEmail, edtAdminPassword, edtRepeatAdminPW;
    private Button btnSignUp;
    private AdminDAO adminDAO; // Sử dụng AdminDAO thay vì DatabaseHelper

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

        // Khởi tạo AdminDAO
        adminDAO = new AdminDAO(this);

        // Xử lý sự kiện đăng ký
        btnSignUp.setOnClickListener(v -> registerAdmin());
    }

    private void registerAdmin() {
        String fullName = edtAdminName.getText().toString().trim();
        String email = edtAdminEmail.getText().toString().trim();
        String password = edtAdminPassword.getText().toString().trim();
        String repeatPassword = edtRepeatAdminPW.getText().toString().trim();

        // Kiểm tra nhập liệu
        if (!validateInputs(fullName, email, password, repeatPassword)) return;

        // Kiểm tra email đã tồn tại
        if (adminDAO.checkAdminEmail(email)) { // Sử dụng checkAdminEmail từ AdminDAO
            Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Admin mới
        Admin newAdmin = new Admin(fullName, email, password);
        long adminId = adminDAO.addAdmin(newAdmin);

        if (adminId > 0) {
            // Lưu adminId vào SharedPreferences để sử dụng sau này
            saveAdminSession((int) adminId);

            Toast.makeText(this, "Đăng ký Admin thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String fullName, String email, String password, String repeatPassword) {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveAdminSession(int adminId) {
        SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("adminId", adminId);
        editor.apply();
    }
}
