package com.example.easybuy.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybuy.Database.UserDAO;
import com.example.easybuy.Model.User;
import com.example.easybuy.R;

public class UserCustomDialogChangePassword {
    private Context context;
    private Dialog dialog;
    private EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnCancelPassword, btnChangePassword;
    private SessionManager sessionManager;
    private OnPasswordChangeListener listener;

    public interface OnPasswordChangeListener {
        void onPasswordChanged();
    }

    public UserCustomDialogChangePassword(Context context, OnPasswordChangeListener listener) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.listener = listener;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_change_password); // Bạn cần tạo layout này
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        edtCurrentPassword = dialog.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = dialog.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = dialog.findViewById(R.id.edtConfirmPassword);
        btnCancelPassword = dialog.findViewById(R.id.btnCancelPassword);
        btnChangePassword = dialog.findViewById(R.id.btnChangePassword);

        // Xử lý sự kiện
        btnCancelPassword.setOnClickListener(v -> dialog.dismiss());

        btnChangePassword.setOnClickListener(v -> {
            String currentPassword = edtCurrentPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();
            if (validatePassword(currentPassword, newPassword, confirmPassword)) {
                changePassword(currentPassword, newPassword);
            }
        });

        dialog.show();
    }

    private boolean validatePassword(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(context, "Mật khẩu mới và xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(context, "Mật khẩu mới phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changePassword(String currentPassword, String newPassword) {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            UserDAO userDAO = new UserDAO(context);
            userDAO.open();
            User user = userDAO.getUserById(userId);
            if (user != null) {
                // Kiểm tra mật khẩu hiện tại
                if (!user.getPassword().equals(currentPassword)) {
                    Toast.makeText(context, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                    userDAO.close();
                    return;
                }
                // Cập nhật mật khẩu mới
                user.setPassword(newPassword);
                int rowsAffected = userDAO.updateUser(user);
                userDAO.close();
                if (rowsAffected > 0) {
                    Toast.makeText(context, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    listener.onPasswordChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                userDAO.close();
            }
        } else {
            Toast.makeText(context, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
        }
    }
}