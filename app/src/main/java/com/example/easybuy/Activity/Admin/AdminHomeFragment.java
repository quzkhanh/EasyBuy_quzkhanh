package com.example.easybuy.Activity.Admin;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.easybuy.Database.Adapter.ProductAdapter;
import com.example.easybuy.Database.DAO.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private TextView tvEmptyList;
    private EditText etSearchProduct;
    private FloatingActionButton fabAddProduct;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Spinner spinnerYear;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;
    private SessionManager sessionManager;
    private int adminId;
    private int selectedYear;
    private ProductEditor productEditor;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Khởi tạo các thành phần UI
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        etSearchProduct = view.findViewById(R.id.etSearchProduct);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        spinnerYear = view.findViewById(R.id.spinnerYear);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(requireContext());
        adminId = getAdminId();

        if (adminId == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập với tư cách admin!", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Khởi tạo DAO và ProductEditor
        productDAO = new ProductDAO(requireContext());

        // Khởi tạo imagePickerLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null) {
                            // ProductEditor sẽ xử lý logic này
                        }
                    }
                }
        );

        productEditor = new ProductEditor(requireContext(), adminId, imagePickerLauncher);

        // Thiết lập các chức năng
        setupRecyclerView();
        setupSearchListener();

        // Thiết lập Spinner để chọn năm
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                setupViewPager();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = Calendar.getInstance().get(Calendar.YEAR);
                setupViewPager();
            }
        });

        // Thiết lập năm mặc định
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        spinnerYear.setSelection(getYearIndex(selectedYear));

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            intent.putExtra("adminId", adminId);
            startActivity(intent);
        });

        loadProducts();

        return view;
    }

    private int getYearIndex(int year) {
        String[] years = getResources().getStringArray(R.array.years_array); // Sửa tên thành years_array
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

    private void setupSearchListener() {
        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String query) {
        List<Product> allProducts = productDAO.getProductsByAdmin(adminId);
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.setProductList(filteredList);
        tvEmptyList.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int currentAdminId = sessionManager.getAdminId();
        List<Product> initialProducts = productDAO.getProductsByAdmin(adminId);
        productAdapter = new ProductAdapter(getContext(), initialProducts, currentAdminId, product -> {
            productEditor.showProductDialog(product, this::loadProducts);
        });
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        List<Product> products = productDAO.getProductsByAdmin(adminId);
        productAdapter.setProductList(products);

        if (products.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts();
        setupViewPager();
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