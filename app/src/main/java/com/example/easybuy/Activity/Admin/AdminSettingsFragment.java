package com.example.easybuy.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.easybuy.Activity.WelcomeActivity;
import com.example.easybuy.R;
import com.example.easybuy.Utils.CustomDialogName;
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

        btnLogout.setOnClickListener(v -> logout());

        // Hiển thị dialog khi nhấn vào tên
        tvUserName.setOnClickListener(v -> showEditNameDialog());

        return view;
    }

    private void showEditNameDialog() {
        CustomDialogName dialog = new CustomDialogName(requireContext(), newName -> {
            tvUserName.setText(newName);
            // TODO: Cập nhật vào database nếu cần
        });
        dialog.show(tvUserName.getText().toString());
    }

//    private void showChangePasswordDialog() {
//        CustomDialogPassword dialog = new CustomDialogPassword(requireContext());
//        dialog.show();
//    }
//
//    private void showNotificationDialog() {
//        CustomDialogNotification dialog = new CustomDialogNotification(requireContext());
//        dialog.show();
//    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
