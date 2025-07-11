package com.example.easybuy_qz.Activity.Login.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy_qz.Activity.AdminMainActivity;
import com.example.easybuy_qz.Activity.Login.ForgotPW.ForgotPasswordActivity;
import com.example.easybuy_qz.Database.AdminDAO;
import com.example.easybuy_qz.R;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText edtAdminEmail, edtAdminPassword;
    private Button btnAdminLogin;
    private AdminDAO adminDAO;
    private TextView tvSignup;
    private TextView txtForgetPW;

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

        // Khởi tạo AdminDAO
        adminDAO = new AdminDAO(this);
        adminDAO.open(); // Mở database

        // Xử lý sự kiện đăng nhập
        btnAdminLogin.setOnClickListener(v -> loginAdmin());

        // Xử lý sự kiện quên mật khẩu
        txtForgetPW.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
            finish();
        });
        // Xử lý sự kiện đăng ký
        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminSignUpActivity.class));
            finish();
        });
    }

    private void loginAdmin() {
        String email = edtAdminEmail.getText().toString().trim();
        String password = edtAdminPassword.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        AdminApi api = ApiClient.getClient().create(AdminApi.class);
        api.login(credentials).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    Toast.makeText(AdminLoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminLoginActivity.this, AdminMainActivity.class));
                    finish();
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AdminLoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
