package com.example.easybuy.view.Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.easybuy.view.Login.Admin.AdminLoginActivity;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private FragmentManager fragmentManager;
    private Fragment activeFragment;
    private SessionManager sessionManager;
    private int adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        sessionManager = new SessionManager(this);
        adminId = sessionManager.getAdminId();

        // Chỉ chuyển hướng nếu không có phiên đăng nhập nào (bao gồm cả tạm thời)
        if (!sessionManager.isLoggedIn() || adminId == -1) {
            Intent intent = new Intent(this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_admin_main);

        navView = findViewById(R.id.nav_view);
        fragmentManager = getSupportFragmentManager();

        setDefaultFragment();

        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                replaceFragment(new AdminHomeFragment());
            }
            else if (itemId == R.id.nav_products) {
                replaceFragment(new AdminProductFragment());
            }
            else if (itemId == R.id.nav_orders) {
                replaceFragment(AdminOrderFragment.newInstance(adminId));
            }
            else if (itemId == R.id.nav_settings) {
                replaceFragment(new AdminSettingsFragment());
            }
            return true;
        });
    }

    private void setDefaultFragment() {
        replaceFragment(new AdminHomeFragment());
    }

    private void replaceFragment(Fragment fragment) {
        if (activeFragment != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            activeFragment = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}