package com.example.easybuy.view.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.R;
import com.example.easybuy.utils.ProductEditor;
import com.example.easybuy.utils.SessionManager;
import com.example.easybuy.view.Adapter.ProductAdapter;
import com.example.easybuy.viewmodel.ProductViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdminProductFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private LinearLayout emptyListContainer;
    private TextView tvEmptyList;
    private EditText etSearchProduct;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private SessionManager sessionManager;
    private int adminId;
    private ProductEditor productEditor;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Không cần xử lý kết quả ở đây vì ProductEditor sẽ xử lý
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        emptyListContainer = view.findViewById(R.id.emptyListContainer);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        etSearchProduct = view.findViewById(R.id.etSearchProduct);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        sessionManager = new SessionManager(requireContext());
        adminId = getAdminId();

        if (adminId == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập với tư cách admin!", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Khởi tạo ProductViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.init(requireContext());

        // Truyền this::setupRecyclerView làm onProductUpdated
        productEditor = new ProductEditor(this, adminId, imagePickerLauncher, () -> setupRecyclerView());

        setupRecyclerView();
        setupSearchListener();

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            intent.putExtra("adminId", adminId);
            startActivity(intent);
        });

        return view;
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
                // Gửi query tìm kiếm đến ViewModel
                productViewModel.filterProducts(s.toString(), adminId);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int currentAdminId = sessionManager.getAdminId();
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), currentAdminId, product -> {
            productEditor.showProductDialog(product);
        });
        recyclerViewProducts.setAdapter(productAdapter);

        // Quan sát danh sách sản phẩm từ ViewModel
        productViewModel
                .getAllProductsFromServer()          // hoặc getProductsByAdminFromServer(adminId)
                .observe(getViewLifecycleOwner(), products -> {
                    productAdapter.setProductList(products);
                    if (products.isEmpty()) {
                        emptyListContainer.setVisibility(View.VISIBLE);
                        recyclerViewProducts.setVisibility(View.GONE);
                    } else {
                        emptyListContainer.setVisibility(View.GONE);
                        recyclerViewProducts.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Không cần gọi loadProducts() nữa vì đã observe LiveData
    }
}