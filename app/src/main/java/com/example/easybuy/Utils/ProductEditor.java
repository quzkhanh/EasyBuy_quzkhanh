package com.example.easybuy.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.bumptech.glide.Glide;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class ProductEditor {
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
                            ImageButton clickedButton = (ImageButton) dialogView.getTag();
                            if (clickedButton == ibPickDialogImage) {
                                Glide.with(fragment.requireContext()).load(selectedUri).into(ivDialogProductImage);
                                ivDialogProductImage.setTag(selectedUri.toString());
                                Toast.makeText(fragment.requireContext(), "Ảnh chính đã được chọn!", Toast.LENGTH_SHORT).show();
                            } else if (clickedButton == ibPickAdditionalImage) {
                                additionalImageUris.add(selectedUri);
                                updateImageAdapter();
                                Toast.makeText(fragment.requireContext(), "Ảnh bổ sung đã được chọn!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(fragment.requireContext(), "Không thể lấy ảnh đã chọn!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(fragment.requireContext(), "Chưa chọn ảnh nào!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        this.onProductUpdated = onProductUpdated;
        this.productDAO = new ProductDAO(fragment.requireContext());

        // Khởi tạo launcher để yêu cầu quyền
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
        dialogView = inflater.inflate(R.layout.dialog_product_detail, null);

        ivDialogProductImage = dialogView.findViewById(R.id.ivDialogProductImage);
        etDialogProductName = dialogView.findViewById(R.id.etDialogProductName);
        etDialogPrice = dialogView.findViewById(R.id.etDialogPrice);
        etDialogDescription = dialogView.findViewById(R.id.etDialogDescription);
        ibPickDialogImage = dialogView.findViewById(R.id.ibPickDialogImage);
        recyclerViewImages = dialogView.findViewById(R.id.recyclerViewImages);
        ibPickAdditionalImage = dialogView.findViewById(R.id.ibPickAdditionalImage);
        btnEditProduct = dialogView.findViewById(R.id.btnEditProduct);
        btnDeleteProduct = dialogView.findViewById(R.id.btnDeleteProduct);

        final String originalProductName = product.getProductName();
        final double originalPrice = product.getPrice();
        final String originalDescription = product.getDescription();
        final String originalImageUrl = product.getImageUrl();

        Glide.with(fragment.requireContext()).load(originalImageUrl).into(ivDialogProductImage);
        ivDialogProductImage.setTag(originalImageUrl);
        etDialogProductName.setText(originalProductName);
        etDialogPrice.setText(String.valueOf(originalPrice));
        etDialogDescription.setText(originalDescription);

        // Thiết lập RecyclerView cho ảnh bổ sung
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(fragment.requireContext(), LinearLayoutManager.HORIZONTAL, false));
        List<String> initialImageUrls = new ArrayList<>();
        imageAdapter = new ImageAdapter(initialImageUrls, imageUrl -> {}, false); // Không cần xóa ảnh ở đây
        recyclerViewImages.setAdapter(imageAdapter);

        final boolean[] isEditing = {false};

        btnEditProduct.setOnClickListener(v -> {
            if (!isEditing[0]) {
                etDialogProductName.setEnabled(true);
                etDialogPrice.setEnabled(true);
                etDialogDescription.setEnabled(true);
                btnEditProduct.setText("Lưu");
                isEditing[0] = true;
            } else {
                boolean isUpdated = saveProductChanges(product, originalProductName, originalPrice, originalDescription, originalImageUrl);
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
            if (isEditing[0] && imagePickerLauncher != null) {
                dialogView.setTag(ibPickDialogImage);
                checkStoragePermissionAndOpenGallery();
            } else if (!isEditing[0]) {
                Toast.makeText(fragment.requireContext(), "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.requireContext(), "Không thể chọn ảnh do lỗi khởi tạo", Toast.LENGTH_SHORT).show();
            }
        });

        ibPickAdditionalImage.setOnClickListener(v -> {
            if (isEditing[0] && imagePickerLauncher != null) {
                dialogView.setTag(ibPickAdditionalImage);
                checkStoragePermissionAndOpenGallery();
            } else if (!isEditing[0]) {
                Toast.makeText(fragment.requireContext(), "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.requireContext(), "Không thể chọn ảnh do lỗi khởi tạo", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }

    private void checkStoragePermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        if (imagePickerLauncher == null) {
            Toast.makeText(fragment.requireContext(), "Không thể mở gallery do lỗi khởi tạo", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(fragment.requireContext().getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        } else {
            Toast.makeText(fragment.requireContext(), "Không tìm thấy ứng dụng chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImageAdapter() {
        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : additionalImageUris) {
            imageUrls.add(uri.toString());
        }
        imageAdapter.updateImages(imageUrls);
    }

    private boolean saveProductChanges(Product product, String originalProductName, double originalPrice, String originalDescription, String originalImageUrl) {
        String productName = etDialogProductName.getText().toString().trim();
        String priceStr = etDialogPrice.getText().toString().trim();
        String description = etDialogDescription.getText().toString().trim();

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

            boolean hasChanges = false;
            if (!productName.equals(originalProductName)) {
                product.setProductName(productName);
                hasChanges = true;
            }
            if (price != originalPrice) {
                product.setPrice(price);
                hasChanges = true;
            }
            if (!description.equals(originalDescription)) {
                product.setDescription(description);
                hasChanges = true;
            }

            String newImageUrl = (String) ivDialogProductImage.getTag();
            if (newImageUrl != null) {
                product.setImageUrl(newImageUrl);
                hasChanges = true;
            }

            if (!hasChanges) {
                Toast.makeText(fragment.requireContext(), "Không có thay đổi để cập nhật!", Toast.LENGTH_SHORT).show();
                return false;
            }

            List<Uri> additionalUris = new ArrayList<>(additionalImageUris);
            productDAO.updateProductImages(product.getProductId(), additionalUris);

            int rowsAffected = productDAO.updateProduct(product, adminId);
            if (rowsAffected > 0) {
                Toast.makeText(fragment.requireContext(), "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                if (onProductUpdated != null) {
                    onProductUpdated.run();
                }
                return true;
            } else {
                Toast.makeText(fragment.requireContext(), "Cập nhật thất bại: Không tìm thấy sản phẩm hoặc lỗi database!", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(fragment.requireContext(), "Giá không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}