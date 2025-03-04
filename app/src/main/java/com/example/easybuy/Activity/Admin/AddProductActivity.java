package com.example.easybuy.Activity.Admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.easybuy.Database.ImageAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    private Uri mainImageUri;
    private List<Uri> additionalImageUris = new ArrayList<>();
    private EditText etProductName, etPrice, etDescription, etMainImageUrl;
    private ImageButton ibPickImage, ibPickAdditionalImage;
    private ImageView ivProductImage;
    private Button btnSaveProduct;
    private RecyclerView recyclerViewImages;
    private ImageAdapter imageAdapter;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

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
        etMainImageUrl = findViewById(R.id.etMainImageUrl);
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
                            try {
                                if (mainImageUri == null) { // Ảnh chính
                                    mainImageUri = selectedUri;
                                    ivProductImage.setVisibility(View.VISIBLE);
                                    ivProductImage.setImageURI(mainImageUri);
                                    etMainImageUrl.setText(mainImageUri.toString());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        getContentResolver().takePersistableUriPermission(
                                                mainImageUri,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        );
                                    }
                                    Log.d(TAG, "Ảnh chính đã chọn: " + mainImageUri.toString());
                                } else { // Ảnh bổ sung
                                    additionalImageUris.add(selectedUri);
                                    imageAdapter.notifyDataSetChanged();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        getContentResolver().takePersistableUriPermission(
                                                selectedUri,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        );
                                    }
                                    Log.d(TAG, "Ảnh bổ sung đã chọn: " + selectedUri.toString());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi khi hiển thị ảnh: " + e.getMessage());
                                Toast.makeText(this, "Lỗi khi hiển thị ảnh", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "URI ảnh là null!");
                            Toast.makeText(this, "Không lấy được ảnh", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Chọn ảnh bị hủy hoặc có lỗi: resultCode=" + result.getResultCode());
                    }
                }
        );
    }

    private void setupRecyclerView() {
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(additionalImageUris);
        recyclerViewImages.setAdapter(imageAdapter);
    }

    private void checkAndRequestPermission() {
        String[] permissionsToRequest;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissionsToRequest = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        boolean allPermissionsGranted = true;
        for (String permission : permissionsToRequest) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Quyền truy cập bộ nhớ được cấp!");
                Toast.makeText(this, "Quyền được cấp, bạn có thể chọn ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Quyền truy cập bộ nhớ bị từ chối!");
                Toast.makeText(this, "Quyền bị từ chối, không thể chọn ảnh. Vui lòng cấp quyền trong Settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupListeners() {
        ibPickImage.setOnClickListener(v -> {
            if (checkPermission()) {
                if (mainImageUri == null) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Ảnh chính đã được chọn, hãy chọn ảnh bổ sung", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                checkAndRequestPermission();
            }
        });
        ibPickAdditionalImage.setOnClickListener(v -> {
            if (checkPermission()) {
                openGallery();
            } else {
                Toast.makeText(this, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                checkAndRequestPermission();
            }
        });
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (checkPermission()) {
                try {
                    imagePickerLauncher.launch(intent);
                    Log.d(TAG, "Intent gallery (ACTION_GET_CONTENT) đã được khởi động thành công.");
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi khởi động Intent gallery: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi mở gallery: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                checkAndRequestPermission();
            }
        } else {
            Log.e(TAG, "Không tìm thấy ứng dụng hỗ trợ ACTION_GET_CONTENT! Các ứng dụng khả dụng: " + getPackageManager().queryIntentActivities(intent, 0));
            Toast.makeText(this, "Không tìm thấy ứng dụng hỗ trợ chọn ảnh. Vui lòng cài Google Photos.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveProduct() {
        String productName = etProductName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String mainImageUrl = (mainImageUri != null) ? mainImageUri.toString() : "";

        if (productName.isEmpty() || priceStr.isEmpty() || description.isEmpty() || mainImageUrl.isEmpty()) {
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

        Product newProduct = new Product(productName, price, mainImageUrl, description);
        ProductDAO productDAO = new ProductDAO(this);
        long productId = productDAO.addProductWithImages(newProduct, new ArrayList<>(additionalImageUris.stream().map(Uri::toString).collect(Collectors.toList())));
        Log.d(TAG, "Saved product with ID: " + productId + ", Main Image URL: " + mainImageUrl + ", Additional Images: " + additionalImageUris);

        if (productId != -1) {
            Toast.makeText(this, "Sản phẩm đã được thêm!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

}