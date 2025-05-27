package com.example.easybuy.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybuy.database.dao.AdminDAO;
import com.example.easybuy.model.Admin;
import com.example.easybuy.R;

import org.mindrot.jbcrypt.BCrypt;

public class AdminCustomDialogPassword {
    private Context context;
    private Dialog dialog;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnCancelPassword, btnChangePassword;
    private SessionManager sessionManager;

    public AdminCustomDialogPassword(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_admin_change_password);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        edtOldPassword = dialog.findViewById(R.id.edtOldPassword);
        edtNewPassword = dialog.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = dialog.findViewById(R.id.edtConfirmPassword);
        btnCancelPassword = dialog.findViewById(R.id.btnCancelPassword);
        btnChangePassword = dialog.findViewById(R.id.btnChangePassword);

        // Xử lý sự kiện
        btnCancelPassword.setOnClickListener(v -> dialog.dismiss());

        btnChangePassword.setOnClickListener(v -> {
            String oldPassword = edtOldPassword.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (validateInputs(oldPassword, newPassword, confirmPassword)) {
                changePassword(oldPassword, newPassword);
            }
        });

        dialog.show();
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
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

    private void changePassword(String oldPassword, String newPassword) {
        int adminId = sessionManager.getAdminId();
        if (adminId != -1) {
            AdminDAO adminDAO = new AdminDAO(context);
            String adminEmail = sessionManager.getEmail();
            Log.d("CustomDialogPassword", "Admin email from SessionManager: " + adminEmail);
            Admin admin = adminDAO.getAdminByEmail(adminEmail);
            if (admin != null) {
                Log.d("CustomDialogPassword", "Stored password hash: " + admin.getPassword());
                if (BCrypt.checkpw(oldPassword, admin.getPassword())) {
                    admin.setId(adminId);
                    admin.setPassword(newPassword);
                    boolean isUpdated = adminDAO.updateAdmin(admin);
                    if (isUpdated) {
                        Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Admin không tồn tại cho email: " + adminEmail, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Không tìm thấy ID admin!", Toast.LENGTH_SHORT).show();
        }
    }
}