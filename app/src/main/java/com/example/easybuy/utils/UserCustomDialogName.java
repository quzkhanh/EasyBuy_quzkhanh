package com.example.easybuy.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybuy.database.dao.UserDAO;
import com.example.easybuy.model.User;
import com.example.easybuy.R;

public class UserCustomDialogName {
    private Context context;
    private Dialog dialog;
    private EditText edtNewName;
    private Button btnCancelName, btnChangeName;
    private SessionManager sessionManager;
    private OnNameChangeListener listener;
    private UserDAO userDAO; // Quản lý UserDAO như một trường

    public interface OnNameChangeListener {
        void onNameChanged(String newName);
    }

    public UserCustomDialogName(Context context, OnNameChangeListener listener) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.listener = listener;
        this.userDAO = new UserDAO(context); // Khởi tạo UserDAO một lần
    }

    public void show(String currentName) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_user_change_name);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        edtNewName = dialog.findViewById(R.id.edtNewName);
        btnCancelName = dialog.findViewById(R.id.btnCancelName);
        btnChangeName = dialog.findViewById(R.id.btnChangeName);

        // Hiển thị tên hiện tại
        if (currentName != null && !currentName.isEmpty()) {
            edtNewName.setText(currentName);
        }

        // Xử lý sự kiện
        btnCancelName.setOnClickListener(v -> dialog.dismiss());

        btnChangeName.setOnClickListener(v -> {
            String newName = edtNewName.getText().toString().trim();
            if (validateName(newName)) {
                changeName(newName);
            }
        });

        dialog.show();
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.length() < 2) { // Thêm kiểm tra độ dài tối thiểu
            Toast.makeText(context, "Tên phải có ít nhất 2 ký tự!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changeName(String newName) {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setFullName(newName);
                int rowsAffected = userDAO.updateUser(user);
                if (rowsAffected > 0) {
                    sessionManager.setUserName(newName); // Cập nhật session
                    listener.onNameChanged(newName); // Cập nhật UI
                    Toast.makeText(context, "Cập nhật tên thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Cập nhật tên thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
        }
    }

    // Đóng tài nguyên khi Dialog bị hủy
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (userDAO != null) {
            userDAO.close(); // Đóng UserDAO khi Dialog bị hủy
        }
    }
}