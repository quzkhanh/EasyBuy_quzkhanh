package com.example.easybuy.Activity.Login.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Activity.Admin.AdminMainActivity;
import com.example.easybuy.Activity.Login.ForgotPW.ForgotPasswordActivity;
import com.example.easybuy.Database.AdminDAO;
import com.example.easybuy.Model.Admin;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;

import org.mindrot.jbcrypt.BCrypt;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText edtAdminEmail, edtAdminPassword;
    private Button btnAdminLogin;
    private TextView tvSignup, txtForgetPW;
    private SessionManager sessionManager;
    private AdminDAO adminDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Ánh xạ View
        edtAdminEmail = findViewById(R.id.edtAdminEmail);
        edtAdminPassword = findViewById(R.id.edtAdminPassword);
        btnAdminLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.txtSignup);
        txtForgetPW = findViewById(R.id.txtForgetPW);

        // Khởi tạo DAO và SessionManager
        adminDAO = new AdminDAO(this);
        sessionManager = new SessionManager(this);

        // Nếu đã đăng nhập, chuyển hướng sang AdminMainActivity
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            return;
        }

        btnAdminLogin.setOnClickListener(v -> loginAdmin());
        txtForgetPW.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvSignup.setOnClickListener(v -> startActivity(new Intent(this, AdminSignUpActivity.class)));
    }

    private void loginAdmin() {
        String email = edtAdminEmail.getText().toString().trim();
        String password = edtAdminPassword.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        try {
            Admin admin = adminDAO.getAdminByEmail(email);
            if (admin != null) {
                Log.d("AdminLogin", "Stored password hash: " + admin.getPassword());
                if (BCrypt.checkpw(password, admin.getPassword())) {
                    sessionManager.createAdminLoginSession(admin.getId(), admin.getEmail(), admin.getFullName());
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, AdminMainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("AdminLogin", "Login error: " + e.getMessage());
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}