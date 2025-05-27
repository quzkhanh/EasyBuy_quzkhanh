package com.example.easybuy.utils;

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

import com.example.easybuy.view.Adapter.ImageAdapter;
import com.example.easybuy.database.dao.ProductDAO;
import com.example.easybuy.model.Product;
import com.example.easybuy.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class ProductEditor {
    private static final String TAG = "ProductEditor";
    private final Fragment fragment;
    private final int adminId;
    private final ProductDAO productDAO;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private View dialogView;
    private ImageView ivDialogProductImage;
    private EditText etDialogProductName, etDialogPrice, etDialogDescription;
    private ImageButton ibPickDialogImage, ibPickAdditionalImage;
    private RecyclerView recyclerViewImages;
    private Button btnEditProduct, btnDeleteProduct;
    private List<Uri> additionalImageUris = new ArrayList<>();
    private Runnable onProductUpdated;
    private AlertDialog dialog;
    private ImageAdapter imageAdapter;
    private boolean isMainImage = true;
    private boolean isEditing = false;

    public ProductEditor(Fragment fragment, int adminId, ActivityResultLauncher<Intent> imagePickerLauncher, Runnable onProductUpdated) {
        // Constructor remains unchanged
        this.fragment = fragment;
        this.adminId = adminId;
        this.onProductUpdated = onProductUpdated;
        this.productDAO = new ProductDAO(fragment.requireContext());

        this.imagePickerLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null) {
                            Log.d(TAG, "Selected URI: " + selectedUri);
                            if (isMainImage) {
                                Glide.with(fragment.requireContext())
                                        .load(selectedUri)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .placeholder(R.drawable.product_placeholder)
                                        .into(ivDialogProductImage);
                                ivDialogProductImage.setTag(selectedUri.toString());
                                Toast.makeText(fragment.requireContext(), "Ảnh chính đã được chọn!", Toast.LENGTH_SHORT).show();
                            } else {
                                additionalImageUris.add(selectedUri);
                                updateImageAdapter();
                                Toast.makeText(fragment.requireContext(), "Ảnh bổ sung đã được chọn!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        this.requestPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
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

        // Initially disable EditTexts and image buttons
        setEditMode(false);

        Glide.with(fragment.requireContext())
                .load(product.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.product_placeholder)
                .into(ivDialogProductImage);
        ivDialogProductImage.setTag(product.getImageUrl());

        etDialogProductName.setText(product.getProductName());
        etDialogPrice.setText(String.valueOf(product.getPrice()));
        etDialogDescription.setText(product.getDescription());

        List<String> initialImageUrls = productDAO.getAdditionalImages(product.getProductId());
        additionalImageUris.clear();
        for (String imageUrl : initialImageUrls) {
            additionalImageUris.add(Uri.parse(imageUrl));
        }

        recyclerViewImages.setLayoutManager(new LinearLayoutManager(fragment.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(initialImageUrls, imageUrl -> {}, false);
        recyclerViewImages.setAdapter(imageAdapter);
        recyclerViewImages.requestLayout();

        btnEditProduct.setOnClickListener(v -> {
            if (!isEditing) {
                // Enter edit mode
                setEditMode(true);
                btnEditProduct.setText("Lưu");
                Toast.makeText(fragment.requireContext(), "Bắt đầu chỉnh sửa sản phẩm", Toast.LENGTH_SHORT).show();
            } else {
                // Save changes
                saveProductChanges(product);
            }
        });

        btnDeleteProduct.setOnClickListener(v -> confirmDeleteProduct(product));

        ibPickDialogImage.setOnClickListener(v -> {
            isMainImage = true;
            pickImage();
        });

        ibPickAdditionalImage.setOnClickListener(v -> {
            isMainImage = false;
            pickImage();
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    private void setEditMode(boolean enabled) {
        isEditing = enabled;
        etDialogProductName.setEnabled(enabled);
        etDialogPrice.setEnabled(enabled);
        etDialogDescription.setEnabled(enabled);
        ibPickDialogImage.setEnabled(enabled);
        ibPickAdditionalImage.setEnabled(enabled);
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
    }

    private void updateImageAdapter() {
        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : additionalImageUris) {
            imageUrls.add(uri.toString());
        }
        imageAdapter.updateImages(imageUrls);
        recyclerViewImages.requestLayout();
    }

    private void saveProductChanges(Product product) {
        String productName = etDialogProductName.getText().toString().trim();
        String priceStr = etDialogPrice.getText().toString().trim();
        String description = etDialogDescription.getText().toString().trim();
        String newImageUrl = (String) ivDialogProductImage.getTag();

        if (productName.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(fragment.requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            product.setProductName(productName);
            product.setPrice(price);
            product.setDescription(description);
            product.setImageUrl(newImageUrl);

            productDAO.updateProduct(product, adminId);
            productDAO.updateProductImages(product.getProductId(), additionalImageUris);
            onProductUpdated.run();

            Toast.makeText(fragment.requireContext(), "Đã cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            setEditMode(false);
            btnEditProduct.setText("Sửa");
            dialog.dismiss();
        } catch (NumberFormatException e) {
            Toast.makeText(fragment.requireContext(), "Giá không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteProduct(Product product) {
        productDAO.deleteProduct(product.getProductId(), adminId);
        onProductUpdated.run();
        Toast.makeText(fragment.requireContext(), "Đã xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}