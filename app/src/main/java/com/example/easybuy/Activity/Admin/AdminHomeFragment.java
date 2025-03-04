package com.example.easybuy.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        productDAO = new ProductDAO(requireContext());

        setupRecyclerView();

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        loadProducts();

        return view;
    }

    private void setupRecyclerView() {
        // Sử dụng GridLayoutManager với 2 cột
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<Product> initialProducts = productDAO.getAllProducts();
        productAdapter = new ProductAdapter(getContext(), initialProducts, product -> {
            // Xử lý khi click vào sản phẩm (tùy chọn)
            // Ví dụ: Toast.makeText(getContext(), "Clicked: " + product.getProductName(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
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
    }
}