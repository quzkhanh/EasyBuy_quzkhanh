package com.example.easybuy.Activity.Login.User;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Database.UserDAO;
import com.example.easybuy.Model.User;
import com.example.easybuy.R;

public class UserSignUpActivity extends AppCompatActivity {
    private EditText edtUserName, edtUserEmail, edtUserPassword, edtRepeatPW;
    private Button btnSignUp;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        // Ánh xạ view
        edtUserName = findViewById(R.id.edtUserName);
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserPassword = findViewById(R.id.edtUserPassword);
        edtRepeatPW = findViewById(R.id.edtRepeatPW);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Khởi tạo UserDAO
        userDAO = new UserDAO(this);
        userDAO.open(); // Mở database

        // Xử lý sự kiện click vào nút đăng ký
        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullName = edtUserName.getText().toString().trim();
        String email = edtUserEmail.getText().toString().trim();
        String password = edtUserPassword.getText().toString().trim();
        String repeatPassword = edtRepeatPW.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu nhập lại
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra email đã tồn tại chưa
        if (userDAO.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng User và thêm vào database
        User newUser = new User(0, fullName, email, password, "");
        long result = userDAO.addUser(newUser);

        if (result > 0) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserLoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDAO.close(); // Đóng database khi activity bị hủy
    }
}
