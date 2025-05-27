package com.example.easybuy.view.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.easybuy.database.dao.ProductDAO;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class AdminHomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Spinner spinnerYear;
    private TextView tvProductCount;
    private TextView tvOrderCount;
    private TextView tvSoldCount;
    private SessionManager sessionManager;
    private ProductDAO productDAO;
    private int adminId;
    private int selectedYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Khởi tạo các thành phần UI
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        spinnerYear = view.findViewById(R.id.spinnerYear);
        tvProductCount = view.findViewById(R.id.tvProductCount);
        tvOrderCount = view.findViewById(R.id.tvOrderCount);
        tvSoldCount = view.findViewById(R.id.tvSoldCount);

        // Khởi tạo SessionManager và ProductDAO
        sessionManager = new SessionManager(requireContext());
        productDAO = new ProductDAO(requireContext());
        adminId = getAdminId();

        if (adminId == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập với tư cách admin!", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Thiết lập Spinner để chọn năm
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                setupViewPager();
                updateStatistics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = Calendar.getInstance().get(Calendar.YEAR);
                setupViewPager();
                updateStatistics();
            }
        });

        // Thiết lập năm mặc định
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        spinnerYear.setSelection(getYearIndex(selectedYear));

        setupViewPager();
        updateStatistics();

        return view;
    }

    private int getYearIndex(int year) {
        String[] years = getResources().getStringArray(R.array.years_array);
        for (int i = 0; i < years.length; i++) {
            if (Integer.parseInt(years[i]) == year) {
                return i;
            }
        }
        return 0;
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, adminId, selectedYear);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Đơn hàng");
                            break;
                        case 1:
                            tab.setText("Doanh thu");
                            break;
                        case 2:
                            tab.setText("Sản phẩm");
                            break;
                    }
                }).attach();
    }

    private int getAdminId() {
        int adminId = sessionManager.getAdminId();
        if (adminId == -1) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin admin!", Toast.LENGTH_SHORT).show();
        }
        return adminId;
    }

    private void updateStatistics() {
        // Số lượng sản phẩm
        int productCount = productDAO.getProductsByAdmin(adminId).size();
        tvProductCount.setText("Số lượng sản phẩm: " + productCount);

        // Số lượng đơn hàng (cần OrderDAO)
        // Ví dụ: int orderCount = orderDAO.getOrderCountByAdminAndYear(adminId, selectedYear);
        tvOrderCount.setText("Số lượng đơn hàng: " + "0"); // Placeholder - cần OrderDAO

        // Số sản phẩm đã bán (cần OrderDetailDAO hoặc tương tự)
        // Ví dụ: int soldCount = orderDetailDAO.getSoldProductCountByAdminAndYear(adminId, selectedYear);
        tvSoldCount.setText("Số sản phẩm đã bán: " + "0"); // Placeholder - cần OrderDetailDAO
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViewPager();
        updateStatistics();
    }

    private static class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        private int adminId;
        private int selectedYear;

        public ViewPagerAdapter(Fragment fragment, int adminId, int selectedYear) {
            super(fragment);
            this.adminId = adminId;
            this.selectedYear = selectedYear;
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new OrderPieChartFragment(adminId, selectedYear);
                case 1:
                    return new RevenueLineChartFragment(adminId, selectedYear);
                case 2:
                    return new ProductCountFragment(adminId);
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}