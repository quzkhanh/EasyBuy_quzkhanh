package com.example.easybuy.Utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybuy.Database.AdminDAO;
import com.example.easybuy.Model.Admin;
import com.example.easybuy.R;

public class AdminCustomDialogEmail {
    private Context context; // Khai báo biến instance context
    private Dialog dialog;
    private EditText edtNewEmail;
    private Button btnCancelEmail, btnChangeEmail;
    private SessionManager sessionManager;
    private OnEmailChangeListener listener;

    public interface OnEmailChangeListener {
        void onEmailChanged(String newEmail);
    }

    public AdminCustomDialogEmail(Context context, OnEmailChangeListener listener) {
        this.context = context; // Khởi tạo context
        this.sessionManager = new SessionManager(context);
        this.listener = listener;
    }

    public void show(String currentEmail) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_admin_change_email);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        edtNewEmail = dialog.findViewById(R.id.edtNewEmail);
        btnCancelEmail = dialog.findViewById(R.id.btnCancelEmail);
        btnChangeEmail = dialog.findViewById(R.id.btnChangeEmail);

        // Hiển thị email hiện tại
        edtNewEmail.setText(currentEmail);

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
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Kiểm tra email đã tồn tại
        AdminDAO adminDAO = new AdminDAO(context);
        if (adminDAO.checkAdminEmail(email)) {
            Toast.makeText(context, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changeEmail(String newEmail) {
        int adminId = sessionManager.getAdminId();
        if (adminId != -1) {
            AdminDAO adminDAO = new AdminDAO(context);
            Admin admin = adminDAO.getAdminById(adminId);
            if (admin != null) {
                Log.d("CustomDialogEmail", "Current email: " + admin.getEmail());
                admin.setEmail(newEmail);
                boolean isUpdated = adminDAO.updateAdmin(admin);
                if (isUpdated) {
                    Log.d("CustomDialogEmail", "Email updated to: " + newEmail);
                    sessionManager.setEmail(newEmail);
                    listener.onEmailChanged(newEmail);
                    Toast.makeText(context, "Đổi email thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Đổi email thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Admin không tồn tại!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Không tìm thấy ID admin!", Toast.LENGTH_SHORT).show();
        }
    }
}