package com.example.easybuy.Activity.Login.ForgotPW;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easybuy.Database.DatabaseHelper.DatabaseHelper;
import com.example.easybuy.Database.DatabaseHelper.UserDatabaseHelper; // Sử dụng UserDatabaseHelper
import com.example.easybuy.R;
public class CheckEmailActivity extends AppCompatActivity {
    private UserDatabaseHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_email);

        // Khởi tạo UserDatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDbHelper = new UserDatabaseHelper(dbHelper);

        // Ví dụ sử dụng
        String email = "user@example.com";
        String otp = "123456";

        if (userDbHelper.generateAndSaveOTP(email)) {
            // OTP đã được tạo và lưu thành công
            if (userDbHelper.verifyOTP(email, otp)) {
                // OTP đã được xác minh thành công
                Toast.makeText(this, "Xác minh OTP thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "OTP không hợp lệ hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không thể tạo OTP!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDbHelper != null) {
            userDbHelper.close(); // Đảm bảo đóng helper khi activity bị hủy
        }
    }
}