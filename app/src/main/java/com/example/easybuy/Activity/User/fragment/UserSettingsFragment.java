package com.example.easybuy.Activity.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.easybuy.Activity.WelcomeActivity;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;

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

        return view;
    }


    private void logout() {
        // Xóa phiên đăng nhập
        sessionManager.logout();
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa toàn bộ stack
        startActivity(intent);
        getActivity().finish();
    }
}