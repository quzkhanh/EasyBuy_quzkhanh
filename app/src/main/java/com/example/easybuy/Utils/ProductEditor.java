package com.example.easybuy.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.Adapter.ImageAdapter;
import com.example.easybuy.Database.DAO.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class ProductEditor {
    private static final String TAG = "ProductEditor";
    private final Fragment fragment;
    private final int adminId;
    private ProductDAO productDAO;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private View dialogView;
    private ImageView ivDialogProductImage;
    private EditText etDialogProductName;
    private EditText etDialogPrice;
    private EditText etDialogDescription;
    private ImageButton ibPickDialogImage;
    private RecyclerView recyclerViewImages;
    private ImageButton ibPickAdditionalImage;
    private Button btnEditProduct;
    private Button btnDeleteProduct;
    private final List<Uri> additionalImageUris = new ArrayList<>();
    private final Runnable onProductUpdated;
    private AlertDialog dialog;
    private ImageAdapter imageAdapter;

    public ProductEditor(Fragment fragment, int adminId, ActivityResultLauncher<Intent> imagePickerLauncher, Runnable onProductUpdated) {
        this.fragment = fragment;
        this.adminId = adminId;
        this.imagePickerLauncher = imagePickerLauncher != null ? imagePickerLauncher : fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null) {
                            Log.d(TAG, "Selected URI: " + selectedUri);
                            ImageButton clickedButton = (ImageButton) dialogView.getTag();
                            if (clickedButton == ibPickDialogImage) {
                                Glide.with(fragment.requireContext())
                                        .load(selectedUri)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .placeholder(R.drawable.product_placeholder)
                                        .into(ivDialogProductImage);
                                ivDialogProductImage.setTag(selectedUri.toString());
                                Toast.makeText(fragment.requireContext(), "Ảnh chính đã được chọn!", Toast.LENGTH_SHORT).show();
                            } else if (clickedButton == ibPickAdditionalImage) {
                                additionalImageUris.add(selectedUri);
                                updateImageAdapter();
                                Toast.makeText(fragment.requireContext(), "Ảnh bổ sung đã được chọn!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Selected URI is null");
                        }
                    } else {
                        Log.w(TAG, "Image selection canceled or failed");
                    }
                }
        );
        this.onProductUpdated = onProductUpdated;
        this.productDAO = new ProductDAO(fragment.requireContext());

        this.requestPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Permission granted, opening gallery");
                        openGallery();
                    } else {
                        Log.w(TAG, "Permission denied");
                        Toast.makeText(fragment.requireContext(), "Cần quyền truy cập bộ nhớ để chọn ảnh!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void showProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.requireContext());
        LayoutInflater inflater = LayoutInflater.from(fragment.requireContext());
        dialogView = inflater.inflate(R.layout.dialog_admin_product_detail, null);

        ivDialogProductImage = dialogView.findViewById(R.id.ivDialogProductImage);
        etDialogProductName = dialogView.findViewById(R.id.etDialogProductName);
        etDialogPrice = dialogView.findViewById(R.id.etDialogPrice);
        etDialogDescription = dialogView.findViewById(R.id.etDialogDescription);
        ibPickDialogImage = dialogView.findViewById(R.id.ibPickDialogImage);
        recyclerViewImages = dialogView.findViewById(R.id.recyclerViewImages);
        ibPickAdditionalImage = dialogView.findViewById(R.id.ibPickAdditionalImage);
        btnEditProduct = dialogView.findViewById(R.id.btnEditProduct);
        btnDeleteProduct = dialogView.findViewById(R.id.btnDeleteProduct);

        final String originalImageUrl = product.getImageUrl();
        Glide.with(fragment.requireContext())
                .load(originalImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.product_placeholder)
                .into(ivDialogProductImage);
        ivDialogProductImage.setTag(originalImageUrl);
        etDialogProductName.setText(product.getProductName());
        etDialogPrice.setText(String.valueOf(product.getPrice()));
        etDialogDescription.setText(product.getDescription());

        List<String> initialImageUrls = productDAO.getAdditionalImages(product.getProductId());
        additionalImageUris.clear();
        for (String imageUrl : initialImageUrls) {
            additionalImageUris.add(Uri.parse(imageUrl));
        }
        Log.d(TAG, "Initial additional images: " + additionalImageUris);

        recyclerViewImages.setLayoutManager(new LinearLayoutManager(fragment.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(initialImageUrls, imageUrl -> {}, false);
        recyclerViewImages.setAdapter(imageAdapter);

        final boolean[] isEditing = {false};

        btnEditProduct.setOnClickListener(v -> {
            if (!isEditing[0]) {
                etDialogProductName.setEnabled(true);
                etDialogPrice.setEnabled(true);
                etDialogDescription.setEnabled(true);
                ibPickDialogImage.setEnabled(true);
                ibPickAdditionalImage.setEnabled(true);
                btnEditProduct.setText("Lưu");
                isEditing[0] = true;
                Log.d(TAG, "Edit mode enabled");
            } else {
                boolean isUpdated = saveProductChanges(product);
                if (isUpdated && dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        btnDeleteProduct.setOnClickListener(v -> {
            new AlertDialog.Builder(fragment.requireContext())
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                    .setPositiveButton("Có", (dialogInterface, i) -> {
                        boolean deleted = productDAO.deleteProduct(product.getProductId(), adminId);
                        if (deleted) {
                            Toast.makeText(fragment.requireContext(), "Sản phẩm đã được xóa!", Toast.LENGTH_SHORT).show();
                            if (onProductUpdated != null) {
                                onProductUpdated.run();
                            }
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(fragment.requireContext(), "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        ibPickDialogImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                dialogView.setTag(ibPickDialogImage);
                pickImage();
            } else {
                Toast.makeText(fragment.requireContext(), "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        ibPickAdditionalImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                dialogView.setTag(ibPickAdditionalImage);
                pickImage();
            } else {
                Toast.makeText(fragment.requireContext(), "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    private void pickImage() {
        if (!checkPermission()) {
            requestPermissionLauncher.launch(getPermission());
            return;
        }
        openGallery();
    }

    private boolean checkPermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private String getPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
        Log.d(TAG, "Opening gallery with ACTION_GET_CONTENT");
    }

    private void updateImageAdapter() {
        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : additionalImageUris) {
            imageUrls.add(uri.toString());
        }
        Log.d(TAG, "Updating RecyclerView with images: " + imageUrls);
        imageAdapter.updateImages(imageUrls);
    }

    private boolean saveProductChanges(Product product) {
        String productName = etDialogProductName.getText().toString().trim();
        String priceStr = etDialogPrice.getText().toString().trim();
        String description = etDialogDescription.getText().toString().trim();
        String newImageUrl = (String) ivDialogProductImage.getTag();

        if (productName.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(fragment.requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                Toast.makeText(fragment.requireContext(), "Giá phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Cập nhật thông tin sản phẩm
            product.setProductName(productName);
            product.setPrice(price);
            product.setDescription(description);
            if (newImageUrl != null && !newImageUrl.equals(product.getImageUrl())) {
                product.setImageUrl(newImageUrl);
                Log.d(TAG, "Main image updated to: " + newImageUrl);
            }

            // Lưu ảnh phụ
            boolean additionalImagesChanged = !additionalImageUris.isEmpty();
            if (additionalImagesChanged) {
                productDAO.updateProductImages(product.getProductId(), additionalImageUris);
                Log.d(TAG, "Updated additional images: " + additionalImageUris);
            }

            // Lưu sản phẩm
            int rowsAffected = productDAO.updateProduct(product, adminId);
            if (rowsAffected > 0 || additionalImagesChanged) {
                Toast.makeText(fragment.requireContext(), "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                if (onProductUpdated != null) {
                    onProductUpdated.run();
                }
                // Đồng bộ ảnh phụ từ cơ sở dữ liệu
                List<String> updatedImageUrls = productDAO.getAdditionalImages(product.getProductId());
                additionalImageUris.clear();
                for (String imageUrl : updatedImageUrls) {
                    additionalImageUris.add(Uri.parse(imageUrl));
                }
                updateImageAdapter();
                // Cập nhật lại ảnh chính trên giao diện
                Glide.with(fragment.requireContext())
                        .load(product.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.product_placeholder)
                        .into(ivDialogProductImage);
                ivDialogProductImage.setTag(product.getImageUrl());
                return true;
            } else {
                Toast.makeText(fragment.requireContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(fragment.requireContext(), "Giá không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}