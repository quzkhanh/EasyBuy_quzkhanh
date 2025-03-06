package com.example.easybuy.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybuy.R;

public class CustomDialogName {
    private Context context;
    private Dialog dialog;
    private EditText edtName;
    private Button btnSave, btnCancel;
    private OnNameChangeListener listener;

    public interface OnNameChangeListener {
        void onNameChanged(String newName);
    }

    public CustomDialogName(Context context, OnNameChangeListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show(String currentName) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_edit_name);
        dialog.setCancelable(false); // Không thể tắt khi bấm ra ngoài
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        edtName = dialog.findViewById(R.id.edtName);
        btnSave = dialog.findViewById(R.id.btnSave);
        btnCancel = dialog.findViewById(R.id.btnCancel);

        // Hiển thị tên hiện tại
        edtName.setText(currentName);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (!newName.isEmpty()) {
                listener.onNameChanged(newName);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
