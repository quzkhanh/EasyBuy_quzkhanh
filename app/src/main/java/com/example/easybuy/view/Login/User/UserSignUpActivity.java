package com.example.easybuy.view.Login.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.easybuy.R;
import com.example.easybuy.viewmodel.UserViewModel;

public class UserSignUpActivity extends AppCompatActivity {
    private EditText edtUserName, edtUserEmail, edtUserPassword, edtRepeatPW;
    private Button btnSignUp;
    private UserViewModel viewModel;

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

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Quan sát trạng thái đăng ký
        viewModel.getSignUpStatus().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, UserLoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát thông báo lỗi
        viewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

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

        // Kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu nhập lại
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi ViewModel để đăng ký người dùng
        viewModel.signUpUser(fullName, email, password, "");
    }
}