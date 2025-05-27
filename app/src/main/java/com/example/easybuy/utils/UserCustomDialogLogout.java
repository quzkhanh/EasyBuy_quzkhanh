package com.example.easybuy.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

import com.example.easybuy.R;

public class UserCustomDialogLogout {
    private Context context;
    private Dialog dialog;
    private OnLogoutListener listener;

    public interface OnLogoutListener {
        void onLogoutConfirmed();
    }

    public UserCustomDialogLogout(Context context, OnLogoutListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ View
        Button btnCancelLogout = dialog.findViewById(R.id.btnCancelLogout);
        Button btnConfirmLogout = dialog.findViewById(R.id.btnConfirmLogout);

        // Xử lý sự kiện
        btnCancelLogout.setOnClickListener(v -> dialog.dismiss());

        btnConfirmLogout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLogoutConfirmed(); // Gọi callback khi xác nhận đăng xuất
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}