package com.example.easybuy.Activity.User;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.easybuy.Activity.User.fragment.UserFavoriteFragment;
import com.example.easybuy.Activity.User.fragment.UserHomeFragment;
import com.example.easybuy.Activity.User.fragment.UserOrderFragment;
import com.example.easybuy.Activity.User.fragment.UserSettingsFragment;
import com.example.easybuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_main);

        // Khởi tạo BottomNavigationView
        BottomNavigationView bottomNavView = findViewById(R.id.nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

        // Đặt fragment mặc định là HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new UserHomeFragment())
                    .commit();
        }

        // Xử lý EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new UserHomeFragment();
                } else if (item.getItemId() == R.id.nav_order) {
                    selectedFragment = new UserOrderFragment();
                } else if (item.getItemId() == R.id.nav_favorite) {
                    selectedFragment = new UserFavoriteFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new UserSettingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, selectedFragment)
                            .commit();
                }
                return true;
            };
}