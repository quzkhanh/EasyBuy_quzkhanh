package com.example.easybuy.Utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybuy.Database.UserDAO;
import com.example.easybuy.Model.User;
import com.example.easybuy.R;

public class UserCustomDialogChangeEmail {
    private Context context;
    private Dialog dialog;
    private EditText edtNewEmail;
    private Button btnCancelEmail, btnChangeEmail;
    private SessionManager sessionManager;
    private OnEmailChangeListener listener;

    public interface OnEmailChangeListener {
        void onEmailChanged(String newEmail);
    }

    public UserCustomDialogChangeEmail(Context context, OnEmailChangeListener listener) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.listener = listener;
    }

    public void show(String currentEmail) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_admin_change_email); // Bạn cần tạo layout này
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        edtNewEmail = dialog.findViewById(R.id.edtNewEmail);
        btnCancelEmail = dialog.findViewById(R.id.btnCancelEmail);
        btnChangeEmail = dialog.findViewById(R.id.btnChangeEmail);

        // Hiển thị email hiện tại
        if (currentEmail != null && !currentEmail.isEmpty()) {
            edtNewEmail.setText(currentEmail);
        }

        // Xử lý sự kiện
        btnCancelEmail.setOnClickListener(v -> dialog.dismiss());

        btnChangeEmail.setOnClickListener(v -> {
            String newEmail = edtNewEmail.getText().toString().trim();
            if (validateEmail(newEmail)) {
                changeEmail(newEmail);
            }
        });

        dialog.show();
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changeEmail(String newEmail) {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            UserDAO userDAO = new UserDAO(context);
            userDAO.open();
            User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setEmail(newEmail);
                int rowsAffected = userDAO.updateUser(user);
                userDAO.close();
                if (rowsAffected > 0) {
                    sessionManager.setEmail(newEmail); // Cập nhật session
                    listener.onEmailChanged(newEmail); // Cập nhật UI
                    Toast.makeText(context, "Cập nhật email thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Cập nhật email thất bại!", Toast.LENGTH_SHORT).show();
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