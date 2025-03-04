package com.example.easybuy.Activity.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.DatabaseHelper;
import com.example.easybuy.Database.ImageAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Order;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    // Views
    private ImageView ivProductImage;
    private EditText etProductName, etPrice, etDescription;
    private RecyclerView recyclerViewImages;
    private Button btnAddToCart, btnBuyNow;

    // Data
    private Product product;
    private DatabaseHelper databaseHelper;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ views
        initializeViews();

        // Lấy thông tin sản phẩm từ Intent
        getProductDetails();

        // Thiết lập RecyclerView cho ảnh bổ sung
        setupAdditionalImagesRecyclerView();

        // Thiết lập sự kiện cho nút Mua ngay
        setupBuyNowButton();
    }

    private void initializeViews() {
        ivProductImage = findViewById(R.id.ivDialogProductImage);
        etProductName = findViewById(R.id.etDialogProductName);
        etPrice = findViewById(R.id.etDialogPrice);
        etDescription = findViewById(R.id.etDialogDescription);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }

    private void getProductDetails() {
        // Lấy thông tin sản phẩm từ Intent
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thêm code để lấy sản phẩm
        ProductDAO productDAO = new ProductDAO(this);
        product = productDAO.getProductById(productId); // Thêm phương thức này vào ProductDAO

        if (product == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Điền thông tin sản phẩm vào các view
        etProductName.setText(product.getProductName());
        etPrice.setText(String.format(Locale.getDefault(), "%,d VNĐ", (long)product.getPrice()));
        etDescription.setText(product.getDescription());
    }
    private void setupAdditionalImagesRecyclerView() {
        // Lấy danh sách ảnh bổ sung (giả định bạn có phương thức này)
        // List<String> additionalImages = productDAO.getAdditionalImages(product.getProductId());

        recyclerViewImages.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        // imageAdapter = new ProductImageAdapter(this, additionalImages);
        // recyclerViewImages.setAdapter(imageAdapter);
    }

    private void setupBuyNowButton() {
        btnBuyNow.setOnClickListener(v -> {
            // Lấy userId từ SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);

            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đơn hàng
            Order order = createOrder(userId);

            // Lưu đơn hàng
            long orderId = databaseHelper.addOrder(order);

            if (orderId != -1) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity sau khi đặt hàng
            } else {
                Toast.makeText(this, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Order createOrder(int userId) {
        // Lấy thời gian hiện tại
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(product.getPrice());
        order.setStatus("Pending");
        order.setOrderDate(currentDate);

        return order;
    }
}