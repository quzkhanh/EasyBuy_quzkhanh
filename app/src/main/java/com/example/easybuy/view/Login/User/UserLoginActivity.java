package com.example.easybuy.view.Login.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.view.User.UserMainActivity;
import com.example.easybuy.database.helper.DatabaseHelper;
import com.example.easybuy.database.helper.UserDatabaseHelper;
import com.example.easybuy.model.User;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;

public class UserLoginActivity extends AppCompatActivity {
    private EditText edtUserEmail, edtUserPassword;
    private Button btnUserLogin;
    private CheckBox btnSaveLogin;
    private TextView tvSignup;
    private DatabaseHelper dbHelper;
    private UserDatabaseHelper userDbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Ánh xạ View
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserPassword = findViewById(R.id.edtUserPassword);
        btnUserLogin = findViewById(R.id.btnLogin);
        btnSaveLogin = findViewById(R.id.btnSaveLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // Khởi tạo DatabaseHelper và UserDatabaseHelper
        dbHelper = new DatabaseHelper(this);
        userDbHelper = new UserDatabaseHelper(dbHelper);
        sessionManager = new SessionManager(this);

        // Nếu đã đăng nhập và phiên được lưu, chuyển hướng sang MainActivity
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
            return;
        }

        btnUserLogin.setOnClickListener(v -> loginUser());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(this, UserSignUpActivity.class)));
    }
    private void loginUser() {
        String email = edtUserEmail.getText().toString().trim();
        String password = edtUserPassword.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        User user = userDbHelper.checkLogin(email, password);
        if (user != null) {
            String fullName = user.getFullName();

            // Luôn lưu phiên đăng nhập
            sessionManager.createUserLoginSession(user.getUserId(), email, fullName);

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDbHelper != null) {
            userDbHelper.close(); // Đóng UserDatabaseHelper
        }
    }
}