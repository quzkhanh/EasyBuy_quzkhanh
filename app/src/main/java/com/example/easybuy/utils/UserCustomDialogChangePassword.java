package com.example.easybuy.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.easybuy.database.dao.UserDAO;
import com.example.easybuy.model.User;
import com.example.easybuy.R;
import com.google.android.material.textfield.TextInputEditText;

import org.mindrot.jbcrypt.BCrypt;

public class UserCustomDialogChangePassword {
    private Context context;
    private Dialog dialog;
    private TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnCancelPassword, btnChangePassword;
    private SessionManager sessionManager;
    private OnPasswordChangeListener listener;
    private UserDAO userDAO;
    private boolean isAdminChangingForOther;

    public interface OnPasswordChangeListener {
        void onPasswordChanged();
    }

    public UserCustomDialogChangePassword(Context context, OnPasswordChangeListener listener) {
        this(context, listener, false);
    }

    public UserCustomDialogChangePassword(Context context, OnPasswordChangeListener listener, boolean isAdminChangingForOther) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.listener = listener;
        this.userDAO = new UserDAO(context);
        this.isAdminChangingForOther = isAdminChangingForOther;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        edtCurrentPassword = dialog.findViewById(R.id.edtOldPassword);
        edtNewPassword = dialog.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = dialog.findViewById(R.id.edtConfirmPassword);
        btnCancelPassword = dialog.findViewById(R.id.btnCancelPassword);
        btnChangePassword = dialog.findViewById(R.id.btnChangePassword);

        // Nếu là admin đổi cho người khác, ẩn trường mật khẩu hiện tại
        if (isAdminChangingForOther) {
            edtCurrentPassword.setVisibility(View.GONE);
        }

        // Xử lý sự kiện nút Hủy và Lưu
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
        if (isAdminChangingForOther) {
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Vui lòng điền mật khẩu mới và xác nhận!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return false;
            }
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
            User user = userDAO.getUserById(userId);
            if (user != null) {
                if (!isAdminChangingForOther) {
                    String storedPassword = user.getPassword();
                    if (!BCrypt.checkpw(currentPassword, storedPassword)) {
                        Toast.makeText(context, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                user.setPassword(hashedNewPassword);
                int rowsAffected = userDAO.updateUser(user);
                if (rowsAffected > 0) {
                    Toast.makeText(context, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    listener.onPasswordChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (userDAO != null) {
            userDAO.close();
        }
    }
}