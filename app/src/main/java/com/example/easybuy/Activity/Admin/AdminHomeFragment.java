package com.example.easybuy.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easybuy.Database.ProductAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private TextView tvEmptyList;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Khởi tạo các view
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        // Khởi tạo ProductDAO
        productDAO = new ProductDAO(requireContext());

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Thiết lập sự kiện cho FAB
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        // Load danh sách sản phẩm
        loadProducts();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Product> initialProducts = productDAO.getAllProducts();
        productAdapter = new ProductAdapter(getContext(), initialProducts, product -> {
            // Xử lý khi click vào sản phẩm (tùy chọn)
            // Ví dụ: Mở màn hình chi tiết hoặc chỉnh sửa sản phẩm
        });
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
        productAdapter.setProductList(products); // Cập nhật danh sách sản phẩm

        // Hiển thị hoặc ẩn TextView "Không có sản phẩm nào"
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
        // Cập nhật lại danh sách khi quay lại Fragment (sau khi thêm sản phẩm mới)
        loadProducts();
    }
}