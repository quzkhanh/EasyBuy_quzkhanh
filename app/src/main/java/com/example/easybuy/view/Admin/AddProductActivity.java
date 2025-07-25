package com.example.easybuy.view.Admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.network.ApiClient;
import com.example.easybuy.network.ProductApi;
import com.example.easybuy.view.Adapter.ImageAdapter;
import com.example.easybuy.database.dao.ProductDAO;
import com.example.easybuy.model.Product;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private Uri mainImageUri;
    private List<Uri> additionalImageUris = new ArrayList<>();
    private EditText etProductName, etPrice, etDescription;
    private ImageButton ibPickImage, ibPickAdditionalImage;
    private ImageView ivProductImage;
    private Button btnSaveProduct;
    private RecyclerView recyclerViewImages;
    private ImageAdapter imageAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        adminId = getIntent().getIntExtra("adminId", -1);
        if (adminId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin admin", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupImagePicker();
        setupRecyclerView();
        setupListeners();
        checkAndRequestPermission();
    }

    private void initViews() {
        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        ibPickImage = findViewById(R.id.ibPickImage);
        ibPickAdditionalImage = findViewById(R.id.ibPickAdditionalImage);
        ivProductImage = findViewById(R.id.ivProductImage);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null) {
                            handleImageSelection(selectedUri);
                        }
                    }
                }
        );
    }

    private void handleImageSelection(Uri selectedUri) {
        try {
            if (mainImageUri == null) {
                mainImageUri = selectedUri;
                ivProductImage.setVisibility(View.VISIBLE);
                ivProductImage.setImageURI(mainImageUri);
            } else {
                additionalImageUris.add(selectedUri);
                updateImageAdapter(); // Cập nhật adapter với danh sách mới
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(
                        selectedUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            }
            Log.d(TAG, "Ảnh đã chọn: " + selectedUri.toString());
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xử lý ảnh: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(new ArrayList<>(), imageUrl -> {
            Toast.makeText(this, "Clicked image: " + imageUrl, Toast.LENGTH_SHORT).show();
        }, true); // true cho admin
        recyclerViewImages.setAdapter(imageAdapter);
        updateImageAdapter(); // Khởi tạo với danh sách hiện tại
    }

    private void updateImageAdapter() {
        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : additionalImageUris) {
            imageUrls.add(uri.toString());
        }
        imageAdapter.updateImages(imageUrls); // Cập nhật adapter
    }

    private void checkAndRequestPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES :
                Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cần quyền truy cập để chọn ảnh", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupListeners() {
        ibPickImage.setOnClickListener(v -> pickImage(true));
        ibPickAdditionalImage.setOnClickListener(v -> pickImage(false));
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void pickImage(boolean isMainImage) {
        if (!checkPermission()) {
            checkAndRequestPermission();
            return;
        }

        if (isMainImage && mainImageUri != null) {
            Toast.makeText(this, "Đã chọn ảnh chính", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private boolean checkPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES :
                Manifest.permission.READ_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void saveProduct() {
        String productName = etProductName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (productName.isEmpty() || priceStr.isEmpty() || description.isEmpty() || mainImageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh chính", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Product newProduct = new Product(productName, price, mainImageUri.toString(), description, adminId);
        ProductApi api = ApiClient.getClient().create(ProductApi.class);
        api.addProduct(newProduct).enqueue(new retrofit2.Callback<Product>() {
            @Override
            public void onResponse(retrofit2.Call<Product> call, retrofit2.Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Product> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API error: ", t);
            }
        });

    }
}