package com.example.easybuy.Activity.Login.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Activity.User.UserMainActivity;
import com.example.easybuy.Database.UserDAO;
import com.example.easybuy.Model.User;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class UserLoginActivity extends AppCompatActivity {
    private UserDAO userDAO;
    private TextInputEditText edtUserEmail, edtUserPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        userDAO = new UserDAO(this);
        userDAO.open();

        sessionManager = new SessionManager(this);

        // Kiểm tra nếu đã đăng nhập, chuyển hướng sang UserMainActivity
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
            return;
        }

        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserPassword = findViewById(R.id.edtUserPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, UserSignUpActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = edtUserEmail.getText().toString().trim();
        String password = edtUserPassword.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        User user = userDAO.checkLogin(email, password);
        if (user != null) {
            // Sử dụng SessionManager để lưu phiên đăng nhập
            sessionManager.createUserLoginSession(user.getUserId(), user.getEmail(), user.getFullName());

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
        }
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
        userDAO.close();
    }
}