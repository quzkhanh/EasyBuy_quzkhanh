package com.example.easybuy.view.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.easybuy.view.Login.WelcomeActivity;
import com.example.easybuy.database.dao.UserDAO;
import com.example.easybuy.model.User;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;
import com.example.easybuy.utils.UserCustomDialogName;
import com.example.easybuy.utils.UserCustomDialogChangeEmail;
import com.example.easybuy.utils.UserCustomDialogChangePassword;
import com.example.easybuy.utils.UserCustomDialogLogout;

public class UserSettingsFragment extends Fragment {
    private TextView btnLogout;
    private TextView tvUserName;
    private TextView tvPassword; // Có thể loại bỏ nếu không cần hiển thị
    private TextView tvEmail;
    private SessionManager sessionManager;
    private UserDAO userDAO;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        userDAO = new UserDAO(requireContext());
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
        btnLogout.setOnClickListener(v -> showLogoutDialog());

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
        }
    }

    private void updateEmail() {
        String email = sessionManager.getEmail();
        if (!email.isEmpty()) {
            tvEmail.setText(email);
        } else {
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
        }
    }

    private void showEditNameDialog() {
        UserCustomDialogName dialog = new UserCustomDialogName(requireContext(), newName -> {
            tvUserName.setText(newName);
            sessionManager.setUserName(newName);
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    user.setFullName(newName);
                    userDAO.updateUser(user);
                }
            }
        });
        dialog.show(sessionManager.getUserName());
    }

    private void showEditEmailDialog() {
        UserCustomDialogChangeEmail dialog = new UserCustomDialogChangeEmail(requireContext(), newEmail -> {
            tvEmail.setText(newEmail);
            sessionManager.setEmail(newEmail);
            int userId = sessionManager.getUserId();
            if (userId != -1) {
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    user.setEmail(newEmail);
                    userDAO.updateUser(user);
                }
            }
        });
        dialog.show(sessionManager.getEmail());
    }

    private void showEditPasswordDialog() {
        UserCustomDialogChangePassword dialog = new UserCustomDialogChangePassword(requireContext(), () -> {
            Toast.makeText(requireContext(), "Mật khẩu đã được thay đổi!", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void showLogoutDialog() {
        UserCustomDialogLogout dialog = new UserCustomDialogLogout(requireContext(), () -> {
            // Xử lý đăng xuất khi người dùng xác nhận
            sessionManager.logout();
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}