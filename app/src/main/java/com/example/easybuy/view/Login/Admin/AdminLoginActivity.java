package com.example.easybuy.view.Login.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.network.AdminApi;
import com.example.easybuy.network.ApiClient;
import com.example.easybuy.view.Admin.AdminMainActivity;
import com.example.easybuy.view.Login.ForgotPW.ForgotPasswordActivity;
import com.example.easybuy.database.dao.AdminDAO;
import com.example.easybuy.model.Admin;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText edtAdminEmail, edtAdminPassword;
    private Button btnAdminLogin;
    private TextView tvSignup, txtForgetPW;
    private CheckBox btnSaveLogin; // Ánh xạ CheckBox
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
        btnSaveLogin = findViewById(R.id.btnSaveLogin); // Ánh xạ CheckBox

        // Khởi tạo DAO và SessionManager
        adminDAO = new AdminDAO(this);
        sessionManager = new SessionManager(this);

        // Nếu đã đăng nhập và phiên được lưu, chuyển hướng sang AdminMainActivity
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

        // Gọi API login
        AdminApi api = ApiClient.getClient().create(AdminApi.class);
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        api.login(loginData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    Map<String, Object> adminMap = (Map<String, Object>) body.get("admin");

                    String fullName = (String) adminMap.get("full_name");
                    String email = (String) adminMap.get("email");
                    int id;

                    // Nếu backend trả _id là String (MongoDB)
                    try {
                        id = adminMap.get("id") != null
                                ? ((Double) adminMap.get("id")).intValue()  // nếu bạn dùng SQLite hoặc số
                                : fullName.hashCode(); // fallback nếu không có id
                    } catch (Exception e) {
                        id = fullName.hashCode();
                    }

                    sessionManager.createAdminLoginSession(id, email, fullName);

                    Toast.makeText(AdminLoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminLoginActivity.this, AdminMainActivity.class));
                    finish();
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
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