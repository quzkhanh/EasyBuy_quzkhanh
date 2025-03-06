package com.example.easybuy.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.easybuy.Activity.WelcomeActivity;
import com.example.easybuy.Database.AdminDAO;
import com.example.easybuy.Model.Admin;
import com.example.easybuy.R;
import com.example.easybuy.Utils.CustomDialogName;
import com.example.easybuy.Utils.CustomDialogPassword;
import com.example.easybuy.Utils.SessionManager;

public class AdminSettingsFragment extends Fragment {

    private TextView btnLogout;
    private TextView tvUserName;
    private TextView tvPassword;
    private TextView tvNotification;
    private SessionManager sessionManager;

    public AdminSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvPassword = view.findViewById(R.id.tvPassword); // Khởi tạo tvPassword
        tvNotification = view.findViewById(R.id.tvNotification); // Khởi tạo tvNotification (nếu có)

        // Cập nhật tvUserName từ SessionManager
        updateUserName();

        btnLogout.setOnClickListener(v -> logout());

        // Hiển thị dialog khi nhấn vào tên
        tvUserName.setOnClickListener(v -> showEditNameDialog());

        // Hiển thị dialog đổi mật khẩu khi nhấn vào tvPassword
        tvPassword.setOnClickListener(v -> showChangePasswordDialog());

        return view;
    }

    private void updateUserName() {
        String userName = sessionManager.getUserName();
        if (!userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            AdminDAO adminDAO = new AdminDAO(requireContext());
            String adminEmail = sessionManager.getAdminEmail();
            if (!adminEmail.isEmpty()) {
                Admin admin = adminDAO.getAdminByEmail(adminEmail);
                if (admin != null) {
                    tvUserName.setText(admin.getFullName());
                    sessionManager.setUserName(admin.getFullName());
                } else {
                    Log.e("AdminSettingsFragment", "Admin not found for email: " + adminEmail);
                }
            } else {
                Log.e("AdminSettingsFragment", "Admin email not found in SessionManager");
            }
        }
    }

    private void showEditNameDialog() {
        CustomDialogName dialog = new CustomDialogName(requireContext(), newName -> {
            tvUserName.setText(newName);
            int adminId = sessionManager.getAdminId();
            if (adminId != -1) {
                Admin admin = new Admin();
                admin.setId(adminId);
                admin.setFullName(newName);
                AdminDAO adminDAO = new AdminDAO(requireContext());
                boolean isUpdated = adminDAO.updateAdmin(admin);
                if (isUpdated) {
                    sessionManager.setUserName(newName);
                    Toast.makeText(requireContext(), "Cập nhật tên thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Cập nhật tên thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy ID admin!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(tvUserName.getText().toString());
    }

    private void showChangePasswordDialog() {
        CustomDialogPassword dialog = new CustomDialogPassword(requireContext());
        dialog.show();
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}