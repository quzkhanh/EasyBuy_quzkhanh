package com.example.easybuy.Activity.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.easybuy.Activity.WelcomeActivity;
import com.example.easybuy.Database.UserDAO;
import com.example.easybuy.Model.User;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;
import com.example.easybuy.Utils.UserCustomDialogName;
import com.example.easybuy.Utils.UserCustomDialogChangeEmail;
import com.example.easybuy.Utils.UserCustomDialogChangePassword;

public class UserSettingsFragment extends Fragment {
    private TextView btnLogout;
    private TextView tvUserName;
    private TextView tvPassword;
    private TextView tvEmail;
    private SessionManager sessionManager;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        // Ánh xạ các view
        btnLogout = view.findViewById(R.id.btnLogout);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvEmail = view.findViewById(R.id.tvEmail);

        // Cập nhật thông tin người dùng
        updateUserName();
        updateEmail();

        // Thêm sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> logout());

        // Thêm sự kiện đổi tên
        tvUserName.setOnClickListener(v -> showEditNameDialog());

        // Thêm sự kiện đổi email
        tvEmail.setOnClickListener(v -> showEditEmailDialog());

        // Thêm sự kiện đổi mật khẩu
        tvPassword.setOnClickListener(v -> showEditPasswordDialog());

        return view;
    }

    private void updateUserName() {
        String userName = sessionManager.getUserName();
        if (!userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            UserDAO userDAO = new UserDAO(requireContext());
            userDAO.open();
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    tvUserName.setText(user.getFullName());
                    sessionManager.setUserName(user.getFullName());
                } else {
                    tvUserName.setText("Người dùng");
                    Log.e("UserSettingsFragment", "User not found for userId: " + userId);
                }
            } else {
                tvUserName.setText("Người dùng");
                Log.e("UserSettingsFragment", "User ID not found in SessionManager");
            }
            userDAO.close();
        }
    }

    private void updateEmail() {
        String email = sessionManager.getEmail();
        if (!email.isEmpty()) {
            tvEmail.setText(email);
        } else {
            UserDAO userDAO = new UserDAO(requireContext());
            userDAO.open();
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    tvEmail.setText(user.getEmail());
                    sessionManager.setEmail(user.getEmail());
                } else {
                    tvEmail.setText("Chưa có email");
                    Log.e("UserSettingsFragment", "User not found for userId: " + userId);
                }
            } else {
                tvEmail.setText("Chưa có email");
                Log.e("UserSettingsFragment", "User ID not found in SessionManager");
            }
            userDAO.close();
        }
    }

    private void showEditNameDialog() {
        UserCustomDialogName dialog = new UserCustomDialogName(requireContext(), newName -> {
            tvUserName.setText(newName);
            sessionManager.setUserName(newName);
        });
        dialog.show(sessionManager.getUserName());
    }

    private void showEditEmailDialog() {
        UserCustomDialogChangeEmail dialog = new UserCustomDialogChangeEmail(requireContext(), newEmail -> {
            tvEmail.setText(newEmail);
            sessionManager.setEmail(newEmail);
        });
        dialog.show(sessionManager.getEmail());
    }

    private void showEditPasswordDialog() {
        UserCustomDialogChangePassword dialog = new UserCustomDialogChangePassword(requireContext(), () -> {
            // Không cần cập nhật UI cho mật khẩu, chỉ thông báo thành công
        });
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