package com.example.easybuy.Activity.User.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easybuy.Database.ProductAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductDAO productDAO;
    private ProductAdapter productAdapter;
    private TextView tvEmptyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các thành phần
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        productDAO = new ProductDAO(requireContext());

        // Cấu hình RecyclerView với GridLayoutManager (2 cột)
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 sản phẩm trên mỗi hàng
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), product -> {
            // Xử lý khi nhấp vào sản phẩm
            Toast.makeText(getContext(), "Clicked: " + product.getProductName(), Toast.LENGTH_SHORT).show();
        });
        productRecyclerView.setAdapter(productAdapter);

        // Tải danh sách sản phẩm
        loadProducts();

        return view;
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
        productAdapter.setProductList(products);
        tvEmptyList.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        productRecyclerView.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProducts(); // Cập nhật khi quay lại
    }
}