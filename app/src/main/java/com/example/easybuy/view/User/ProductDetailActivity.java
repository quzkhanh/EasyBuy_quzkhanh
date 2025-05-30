package com.example.easybuy.view.User;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.easybuy.view.Adapter.ImageAdapter;
import com.example.easybuy.database.dao.OrderDAO;
import com.example.easybuy.database.dao.ProductDAO;
import com.example.easybuy.model.Order;
import com.example.easybuy.model.Product;
import com.example.easybuy.model.ProductImage;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = ProductDetailActivity.class.getSimpleName();

    // Views
    private ImageView ivDialogProductImage;
    private EditText etDialogProductName, etDialogPrice, etQuantity;
    private RecyclerView recyclerViewImages;
    private Button btnBuyNow, btnDecrease, btnIncrease;
    private TextView tvTotalPrice;

    // Data
    private Product product;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private ImageAdapter imageAdapter;
    private int quantity = 1;
    private double totalPrice;
    private SessionManager sessionManager; // Thêm SessionManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo SessionManager và DAO
        sessionManager = new SessionManager(this);
        orderDAO = new OrderDAO(this);
        productDAO = new ProductDAO(this);

        // Ánh xạ views
        initializeViews();

        // Kiểm tra xem các view có null không
        if (ivDialogProductImage == null || etDialogProductName == null || etDialogPrice == null ||
                recyclerViewImages == null || btnBuyNow == null || etQuantity == null ||
                tvTotalPrice == null || btnDecrease == null || btnIncrease == null) {
            Log.e(TAG, "One or more views are null. Check layout IDs.");
            Toast.makeText(this, "Lỗi tải giao diện sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thông tin sản phẩm từ Intent
        getProductDetails();

        // Thiết lập RecyclerView cho ảnh bổ sung
        setupAdditionalImagesRecyclerView();

        // Thiết lập sự kiện cho nút Mua ngay
        setupBuyNowButton();
    }

    private void initializeViews() {
        ivDialogProductImage = findViewById(R.id.ivDialogProductImage);
        etDialogProductName = findViewById(R.id.etDialogProductName);
        etDialogPrice = findViewById(R.id.etDialogPrice);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        etQuantity = findViewById(R.id.etQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);

        // Nút tăng/giảm số lượng
        btnDecrease.setOnClickListener(v -> {
            try {
                quantity = Integer.parseInt(etQuantity.getText().toString().trim());
                if (quantity > 1) {
                    quantity--;
                    etQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice();
                }
            } catch (NumberFormatException e) {
                quantity = 1;
                etQuantity.setText("1");
                updateTotalPrice();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            try {
                quantity = Integer.parseInt(etQuantity.getText().toString().trim());
                quantity++;
                etQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            } catch (NumberFormatException e) {
                quantity = 1;
                etQuantity.setText("1");
                updateTotalPrice();
            }
        });
    }

    private void updateTotalPrice() {
        if (product == null) return;
        totalPrice = product.getPrice() * quantity;
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        tvTotalPrice.setText("Tổng giá: " + df.format(totalPrice));
    }

    private void getProductDetails() {
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        Log.d(TAG, "Fetching product details for productId: " + productId);
        if (productId == -1) {
            Log.e(TAG, "Invalid productId received from Intent");
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            product = productDAO.getProductById(productId);
            if (product == null) {
                Log.e(TAG, "Product is null for productId: " + productId);
                Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "Product loaded: " + product.getProductName() + ", ImageUrl: " + product.getImageUrl());
            // Gán thông tin sản phẩm vào views
            if (TextUtils.isEmpty(product.getImageUrl())) {
                ivDialogProductImage.setImageResource(R.drawable.product_placeholder);
                Log.w(TAG, "ImageUrl is empty or null for product: " + product.getProductName());
            } else {
                Glide.with(this)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.product_placeholder)
                        .error(R.drawable.product_placeholder)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Glide failed to load image: " + model + ", Error: " + (e != null ? e.getMessage() : "Unknown error"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Glide loaded image successfully: " + model);
                                return false;
                            }
                        })
                        .into(ivDialogProductImage);
            }

            etDialogProductName.setText(product.getProductName());
            DecimalFormat df = new DecimalFormat("#,### VNĐ");
            etDialogPrice.setText(df.format(product.getPrice()));
            etQuantity.setText("1");
            updateTotalPrice();
        } catch (Exception e) {
            Log.e(TAG, "Error loading product details: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupAdditionalImagesRecyclerView() {
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (product != null) {
            List<ProductImage> additionalImages = productDAO.getProductImages(product.getProductId());
            List<String> imageUrls = new ArrayList<>();
            HashSet<String> uniqueImageUrls = new HashSet<>();

            // Thêm ảnh bổ sung từ ProductImage, giới hạn tối đa 1 ảnh bổ sung
            if (additionalImages != null) {
                for (ProductImage image : additionalImages) {
                    String url = image.getImageUrl();
                    if (!TextUtils.isEmpty(url) && !uniqueImageUrls.contains(url) && imageUrls.size() < 1) {
                        imageUrls.add(url);
                        uniqueImageUrls.add(url);
                    } else {
                        Log.w(TAG, "Skipped invalid or duplicate image URL: " + (url == null ? "null" : url));
                    }
                }
            }

            // Chỉ thêm ảnh chính nếu nó hợp lệ, không trùng, và không vượt quá giới hạn
            String mainImageUrl = product.getImageUrl();
            if (!TextUtils.isEmpty(mainImageUrl) && !uniqueImageUrls.contains(mainImageUrl) && imageUrls.size() < 2) {
                imageUrls.add(mainImageUrl);
            } else if (TextUtils.isEmpty(mainImageUrl)) {
                Log.w(TAG, "Main image URL is empty or null, skipping addition to RecyclerView");
            }

            boolean isAdmin = sessionManager.getAdminId() != -1; // Sử dụng SessionManager để kiểm tra admin
            imageAdapter = new ImageAdapter(imageUrls, new ImageAdapter.OnImageClickListener() {
                @Override
                public void onImageClick(String imageUrl) {
                    if (!TextUtils.isEmpty(imageUrl)) {
                        Glide.with(ProductDetailActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.product_placeholder)
                                .error(R.drawable.product_placeholder)
                                .into(ivDialogProductImage);
                    } else {
                        Log.w(TAG, "URL ảnh null hoặc rỗng khi nhấp vào");
                        Toast.makeText(ProductDetailActivity.this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }, isAdmin);

            recyclerViewImages.setAdapter(imageAdapter);
            Log.d(TAG, "RecyclerView loaded with " + imageUrls.size() + " images: " + imageUrls);
        } else {
            Log.w(TAG, "Sản phẩm null, không thể thiết lập RecyclerView");
        }
    }

    private void setupBuyNowButton() {
        btnBuyNow.setOnClickListener(v -> showOrderConfirmationDialog());
    }

    private void showOrderConfirmationDialog() {
        int userId = sessionManager.getUserId(); // Lấy userId từ SessionManager

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog orderDialog = new Dialog(this);
        orderDialog.setContentView(R.layout.order_confirmation);
        orderDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etShippingAddress = orderDialog.findViewById(R.id.etShippingAddress);
        EditText etPhoneNumber = orderDialog.findViewById(R.id.etPhoneNumber);
        Spinner spinnerPaymentMethod = orderDialog.findViewById(R.id.spinnerPaymentMethod);
        Button btnConfirmOrder = orderDialog.findViewById(R.id.btnConfirmOrder);
        Button btnCancel = orderDialog.findViewById(R.id.btnCancel);

        // Hiển thị tổng giá trong dialog
        TextView tvTotalPriceDialog = orderDialog.findViewById(R.id.tvTotalPrice);
        if (tvTotalPriceDialog != null) {
            DecimalFormat df = new DecimalFormat("#,### VNĐ");
            tvTotalPriceDialog.setText("Tổng giá: " + df.format(totalPrice));
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        btnConfirmOrder.setOnClickListener(v -> {
            String shippingAddress = etShippingAddress.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

            if (TextUtils.isEmpty(shippingAddress)) {
                etShippingAddress.setError("Vui lòng nhập địa chỉ giao hàng");
                return;
            }
            if (TextUtils.isEmpty(phoneNumber) || !Patterns.PHONE.matcher(phoneNumber).matches()) {
                etPhoneNumber.setError("Vui lòng nhập số điện thoại hợp lệ");
                return;
            }

            Order order = createOrder(userId, shippingAddress, phoneNumber, paymentMethod);
            long orderId = orderDAO.addOrder(order);

            if (orderId != -1) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                orderDialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> orderDialog.dismiss());

        orderDialog.show();
    }

    private Order createOrder(int userId, String shippingAddress, String phoneNumber, String paymentMethod) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(product.getProductId());
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus("Pending");
        order.setOrderDate(currentDate);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setPaymentMethod(paymentMethod);
        return order;
    }
}