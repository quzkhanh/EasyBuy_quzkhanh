package com.example.easybuy.Activity.Admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.Adapter.ProductAdapter;
import com.example.easybuy.Database.DAO.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import com.example.easybuy.Utils.ProductEditor;
import com.example.easybuy.Utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminProductFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private TextView tvEmptyList;
    private EditText etSearchProduct;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;
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
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        etSearchProduct = view.findViewById(R.id.etSearchProduct);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        sessionManager = new SessionManager(requireContext());
        adminId = getAdminId();

        if (adminId == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập với tư cách admin!", Toast.LENGTH_SHORT).show();
            return view;
        }

        productDAO = new ProductDAO(requireContext());
        // Truyền this::loadProducts làm onProductUpdated
        productEditor = new ProductEditor(this, adminId, imagePickerLauncher, this::loadProducts);

        setupRecyclerView();
        setupSearchListener();

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            intent.putExtra("adminId", adminId);
            startActivity(intent);
        });

        loadProducts();

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
        // Hàm này thiết lập trình nghe sự kiện cho EditText etSearchProduct.
        // Mỗi khi nội dung trong EditText thay đổi, nó sẽ gọi phương thức filterProducts.
        // Cụ thể:
        // - beforeTextChanged: Được gọi trước khi văn bản trong EditText thay đổi.
        // - onTextChanged: Được gọi khi văn bản trong EditText thay đổi.
        //   Trong phương thức này, ta sẽ lấy chuỗi văn bản mới (s) và chuyển nó thành String (s.toString()).
        //   Sau đó, ta gọi phương thức filterProducts với chuỗi này để lọc danh sách sản phẩm.
        // - afterTextChanged: Được gọi sau khi văn bản trong EditText đã thay đổi.
        //
        // Tóm lại, hàm này cho phép lọc danh sách sản phẩm dựa trên nội dung được nhập vào EditText etSearchProduct.
        // Khi người dùng nhập liệu, danh sách sản phẩm sẽ tự động được lọc để hiển thị các sản phẩm phù hợp.

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
        // Phương thức này dùng để lọc danh sách sản phẩm dựa trên chuỗi truy vấn (query).
        // Các bước thực hiện:
        // 1. Lấy danh sách tất cả sản phẩm của admin hiện tại từ cơ sở dữ liệu.
        // 2. Tạo một danh sách mới để chứa các sản phẩm đã được lọc.
        // 3. Duyệt qua từng sản phẩm trong danh sách tất cả sản phẩm.
        // 4. Đối với mỗi sản phẩm, chuyển tên sản phẩm và chuỗi truy vấn thành chữ thường để so sánh không phân biệt hoa thường.
        // 5. Kiểm tra xem tên sản phẩm có chứa chuỗi truy vấn không.
        // 6. Nếu có chứa, thêm sản phẩm đó vào danh sách đã lọc.
        // 7. Sau khi duyệt xong, cập nhật danh sách sản phẩm trong adapter với danh sách đã lọc.
        // 8. Hiển thị/ẩn TextView "tvEmptyList" tùy thuộc vào việc danh sách đã lọc có trống hay không.
        //    - Nếu danh sách trống, hiển thị "tvEmptyList".
        //    - Nếu danh sách không trống, ẩn "tvEmptyList".

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
            productEditor.showProductDialog(product);
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
    }
}