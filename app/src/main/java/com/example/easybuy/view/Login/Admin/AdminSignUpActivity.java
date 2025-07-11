package com.example.easybuy.view.Login.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.database.dao.AdminDAO;
import com.example.easybuy.model.Admin;
import com.example.easybuy.R;
import com.example.easybuy.network.AdminApi;
import com.example.easybuy.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSignUpActivity extends AppCompatActivity {
    private EditText edtAdminName, edtAdminEmail, edtAdminPassword, edtRepeatAdminPW;
    private Button btnSignUp;
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
        // Xử lý sự kiện đăng ký
        btnSignUp.setOnClickListener(v -> registerAdmin());
    }

    private void registerAdmin() {
        String fullName       = edtAdminName.getText().toString().trim();
        String email          = edtAdminEmail.getText().toString().trim();
        String password       = edtAdminPassword.getText().toString().trim();
        String repeatPassword = edtRepeatAdminPW.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ① Chuẩn bị Retrofit
        AdminApi api = ApiClient.getClient().create(AdminApi.class);

        Map<String,String> body = new HashMap<>();
        body.put("full_name", fullName);
        body.put("email", email);
        body.put("password", password);

        // ② Gọi API
        api.register(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> res) {
                if (res.isSuccessful()) {
                    Toast.makeText(AdminSignUpActivity.this, "Đăng ký Admin thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminSignUpActivity.this, AdminLoginActivity.class));
                    finish();
                } else if (res.code() == 400) {
                    Toast.makeText(AdminSignUpActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminSignUpActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AdminSignUpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
