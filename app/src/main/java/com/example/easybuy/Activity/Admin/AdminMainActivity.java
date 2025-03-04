package com.example.easybuy.Activity.Admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.easybuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private FragmentManager fragmentManager;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        navView = findViewById(R.id.nav_view);
        fragmentManager = getSupportFragmentManager();

        // Đặt fragment mặc định là AdminHomeFragment
        setDefaultFragment();

        // Xử lý sự kiện khi chọn item trong BottomNavigationView
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                replaceFragment(new AdminHomeFragment());
            } else if (itemId == R.id.nav_products) {
                replaceFragment(new AdminOrderFragment());
            } else if (itemId == R.id.nav_settings) {
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
            transaction.addToBackStack(null); // Cho phép quay lại fragment trước
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