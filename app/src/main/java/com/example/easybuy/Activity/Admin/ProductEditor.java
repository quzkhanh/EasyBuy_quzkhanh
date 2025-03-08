package com.example.easybuy.Activity.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybuy.Database.DAO.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class ProductEditor {

    private Context context;
    private ProductDAO productDAO;
    private int adminId;
    private AlertDialog currentDialog;
    private List<Uri> additionalImageUris = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private View dialogView;

    // Các thành phần UI trong dialog
    private ImageView ivDialogProductImage;
    private EditText etDialogProductName;
    private EditText etDialogPrice;
    private EditText etDialogDescription;
    private ImageButton ibPickDialogImage;
    private RecyclerView recyclerViewImages;
    private ImageButton ibPickAdditionalImage;
    private Button btnEditProduct;
    private Button btnDeleteProduct;

    public ProductEditor(Context context, int adminId, ActivityResultLauncher<Intent> imagePickerLauncher) {
        this.context = context;
        this.adminId = adminId;
        this.imagePickerLauncher = imagePickerLauncher;
        this.productDAO = new ProductDAO(context);
    }

    public void showProductDialog(Product product, Runnable onProductUpdated) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_product_detail, null);

        // Khởi tạo các thành phần UI
        ivDialogProductImage = dialogView.findViewById(R.id.ivDialogProductImage);
        etDialogProductName = dialogView.findViewById(R.id.etDialogProductName);
        etDialogPrice = dialogView.findViewById(R.id.etDialogPrice);
        etDialogDescription = dialogView.findViewById(R.id.etDialogDescription);
        ibPickDialogImage = dialogView.findViewById(R.id.ibPickDialogImage);
        recyclerViewImages = dialogView.findViewById(R.id.recyclerViewImages);
        ibPickAdditionalImage = dialogView.findViewById(R.id.ibPickAdditionalImage);
        btnEditProduct = dialogView.findViewById(R.id.btnEditProduct);
        btnDeleteProduct = dialogView.findViewById(R.id.btnDeleteProduct);

        // Hiển thị thông tin sản phẩm
        Glide.with(context).load(product.getImageUrl()).into(ivDialogProductImage);
        ivDialogProductImage.setTag(product.getImageUrl());
        etDialogProductName.setText(product.getProductName());
        etDialogPrice.setText(String.valueOf(product.getPrice()));
        etDialogDescription.setText(product.getDescription());

        // Load ảnh bổ sung
        additionalImageUris.clear();
        List<ProductImage> additionalImages = productDAO.getProductImages(product.getProductId());
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (ProductImage image : additionalImages) {
                additionalImageUris.add(Uri.parse(image.getImageUrl()));
            }
        }
        recyclerViewImages.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerViewImages.setAdapter(new AdditionalImageAdapter());

        final boolean[] isEditing = {false};

        ibPickDialogImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                dialogView.setTag(v);
                openGallery();
            } else {
                Toast.makeText(context, "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        ibPickAdditionalImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                dialogView.setTag(v);
                openGallery();
            } else {
                Toast.makeText(context, "Vui lòng nhấn Sửa để chọn ảnh bổ sung", Toast.LENGTH_SHORT).show();
            }
        });

        btnEditProduct.setOnClickListener(v -> {
            if (!isEditing[0]) {
                etDialogProductName.setEnabled(true);
                etDialogPrice.setEnabled(true);
                etDialogDescription.setEnabled(true);
                ibPickDialogImage.setVisibility(View.VISIBLE);
                ibPickAdditionalImage.setVisibility(View.VISIBLE);
                btnEditProduct.setText("Lưu");
                isEditing[0] = true;
            } else {
                if (saveProductChanges(product)) {
                    etDialogProductName.setEnabled(false);
                    etDialogPrice.setEnabled(false);
                    etDialogDescription.setEnabled(false);
                    ibPickDialogImage.setVisibility(View.GONE);
                    ibPickAdditionalImage.setVisibility(View.GONE);
                    btnEditProduct.setText("Sửa");
                    isEditing[0] = false;
                    Toast.makeText(context, "Sản phẩm đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    if (onProductUpdated != null) {
                        onProductUpdated.run(); // Gọi callback để cập nhật danh sách sản phẩm
                    }
                } else {
                    Toast.makeText(context, "Bạn không có quyền sửa sản phẩm này hoặc lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteProduct.setOnClickListener(v -> {
            showDeleteConfirmationDialog(product, onProductUpdated);
        });

        builder.setView(dialogView);
        currentDialog = builder.create();
        currentDialog.show();
    }

    private boolean saveProductChanges(Product product) {
        String productName = etDialogProductName.getText().toString().trim();
        String priceStr = etDialogPrice.getText().toString().trim();
        String description = etDialogDescription.getText().toString().trim();

        if (productName.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                Toast.makeText(context, "Giá phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return false;
            }
            product.setProductName(productName);
            product.setPrice(price);
            product.setDescription(description);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Giá không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String newImageUrl = (String) ivDialogProductImage.getTag();
        if (newImageUrl != null && !newImageUrl.isEmpty()) {
            product.setImageUrl(newImageUrl);
        }

        List<Uri> additionalUris = new ArrayList<>(additionalImageUris);
        productDAO.updateProductImages(product.getProductId(), additionalUris);

        int rowsAffected = productDAO.updateProduct(product, adminId);
        return rowsAffected > 0;
    }

    private void showDeleteConfirmationDialog(Product product, Runnable onProductUpdated) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    boolean deleted = productDAO.deleteProduct(product.getProductId(), adminId);
                    if (deleted) {
                        if (onProductUpdated != null) {
                            onProductUpdated.run(); // Cập nhật danh sách sản phẩm
                        }
                        Toast.makeText(context, "Sản phẩm đã bị xóa!", Toast.LENGTH_SHORT).show();
                        if (currentDialog != null && currentDialog.isShowing()) {
                            currentDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(context, "Bạn không có quyền xóa sản phẩm này hoặc lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        } else {
            Toast.makeText(context, "Không tìm thấy ứng dụng chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private class AdditionalImageAdapter extends RecyclerView.Adapter<AdditionalImageAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_additional_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Uri imageUri = additionalImageUris.get(position);
            Glide.with(holder.itemView.getContext()).load(imageUri).into(holder.imageView);
            holder.ibRemoveImage.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    additionalImageUris.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    Toast.makeText(holder.itemView.getContext(), "Ảnh đã được xóa", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return additionalImageUris.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageButton ibRemoveImage;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivAdditionalImage);
                ibRemoveImage = itemView.findViewById(R.id.ibRemoveImage);
            }
        }
    }
}