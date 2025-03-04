package com.example.easybuy.Activity.Admin;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easybuy.Database.ProductAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;
import com.example.easybuy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import com.example.easybuy.Model.ProductImage;
import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private TextView tvEmptyList;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private AlertDialog currentDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        productDAO = new ProductDAO(requireContext());

        setupImagePicker();
        setupRecyclerView();

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        loadProducts();

        return view;
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null && currentDialog != null) {
                            EditText etDialogMainImageUrl = currentDialog.findViewById(R.id.etDialogMainImageUrl);
                            EditText etDialogAdditionalImages = currentDialog.findViewById(R.id.etDialogAdditionalImages);
                            ImageView ivDialogProductImage = currentDialog.findViewById(R.id.ivDialogProductImage);
                            View requestingButton = getView().getTag() instanceof View ? (View) getView().getTag() : null;

                            if (requestingButton != null && requestingButton.getId() == R.id.ibPickDialogImage && etDialogMainImageUrl.isEnabled()) {
                                etDialogMainImageUrl.setText(selectedUri.toString());
                                Glide.with(this).load(selectedUri).into(ivDialogProductImage);
                                Toast.makeText(requireContext(), "Ảnh chính đã được chọn", Toast.LENGTH_SHORT).show();
                            } else if (requestingButton != null && requestingButton.getId() == R.id.ibPickAdditionalImage && etDialogAdditionalImages.isEnabled()) {
                                String currentImages = etDialogAdditionalImages.getText().toString().trim();
                                String newImage = "Item " + (currentImages.isEmpty() ? 0 : currentImages.split(",").length) + ": " + selectedUri.toString() + ", ";
                                etDialogAdditionalImages.setText(currentImages + newImage);
                                Toast.makeText(requireContext(), "Ảnh bổ sung đã được chọn", Toast.LENGTH_SHORT).show();
                            }
                            getView().setTag(null); // Reset tag sau khi xử lý
                        }
                    }
                }
        );
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<Product> initialProducts = productDAO.getAllProducts();
        productAdapter = new ProductAdapter(getContext(), initialProducts, product -> showProductDialog(product));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void showProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_detail, null);

        ImageView ivDialogProductImage = dialogView.findViewById(R.id.ivDialogProductImage);
        EditText etDialogProductName = dialogView.findViewById(R.id.etDialogProductName);
        EditText etDialogPrice = dialogView.findViewById(R.id.etDialogPrice);
        EditText etDialogDescription = dialogView.findViewById(R.id.etDialogDescription);
        EditText etDialogMainImageUrl = dialogView.findViewById(R.id.etDialogMainImageUrl);
        ImageButton ibPickDialogImage = dialogView.findViewById(R.id.ibPickDialogImage);
        EditText etDialogAdditionalImages = dialogView.findViewById(R.id.etDialogAdditionalImages);
        ImageButton ibPickAdditionalImage = dialogView.findViewById(R.id.ibPickAdditionalImage);
        Button btnEditProduct = dialogView.findViewById(R.id.btnEditProduct);
        Button btnDeleteProduct = dialogView.findViewById(R.id.btnDeleteProduct);

        // Điền thông tin sản phẩm
        Glide.with(this).load(product.getImageUrl()).into(ivDialogProductImage);
        etDialogProductName.setText(product.getProductName());
        etDialogPrice.setText(String.valueOf(product.getPrice()));
        etDialogDescription.setText(product.getDescription());
        etDialogMainImageUrl.setText(product.getImageUrl());

        // Lấy và hiển thị ảnh bổ sung
        List<ProductImage> additionalImages = productDAO.getProductImages(product.getProductId());
        if (additionalImages != null && !additionalImages.isEmpty()) {
            StringBuilder imagesText = new StringBuilder();
            for (int i = 0; i < additionalImages.size(); i++) {
                imagesText.append("Item ").append(i).append(": ").append(additionalImages.get(i).getImageUrl()).append(", ");
            }
            if (imagesText.length() > 0) {
                imagesText.setLength(imagesText.length() - 2); // Xóa dấu phẩy cuối
            }
            etDialogAdditionalImages.setText(imagesText.length() > 0 ? imagesText.toString() : "Không có ảnh bổ sung");
        }

        final boolean[] isEditing = {false};

        ibPickDialogImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                getView().setTag(v); // Lưu button hiện tại để biết nguồn gốc
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        ibPickAdditionalImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                getView().setTag(v); // Lưu button hiện tại để biết nguồn gốc
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhấn Sửa để chọn ảnh bổ sung", Toast.LENGTH_SHORT).show();
            }
        });

        btnEditProduct.setOnClickListener(v -> {
            if (!isEditing[0]) {
                etDialogProductName.setEnabled(true);
                etDialogPrice.setEnabled(true);
                etDialogDescription.setEnabled(true);
                etDialogMainImageUrl.setEnabled(true);
                etDialogAdditionalImages.setEnabled(true);
                ibPickDialogImage.setVisibility(View.VISIBLE);
                ibPickAdditionalImage.setVisibility(View.VISIBLE);
                btnEditProduct.setText("Lưu");
                isEditing[0] = true;
            } else {
                // Lưu thay đổi
                product.setProductName(etDialogProductName.getText().toString().trim());
                try {
                    product.setPrice(Double.parseDouble(etDialogPrice.getText().toString().trim()));
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                product.setDescription(etDialogDescription.getText().toString().trim());
                product.setImageUrl(etDialogMainImageUrl.getText().toString().trim());

                // Xử lý và cập nhật ảnh bổ sung (chỉ lưu text hiện tại)
                String additionalImagesText = etDialogAdditionalImages.getText().toString().trim();
                // TODO: Cần thêm logic để cập nhật bảng product_images nếu có method updateProductImages

                productDAO.updateProduct(product);
                etDialogProductName.setEnabled(false);
                etDialogPrice.setEnabled(false);
                etDialogDescription.setEnabled(false);
                etDialogMainImageUrl.setEnabled(false);
                etDialogAdditionalImages.setEnabled(false);
                ibPickDialogImage.setVisibility(View.GONE);
                ibPickAdditionalImage.setVisibility(View.GONE);
                btnEditProduct.setText("Sửa");
                isEditing[0] = false;
                Toast.makeText(requireContext(), "Sản phẩm đã được cập nhật!", Toast.LENGTH_SHORT).show();
                loadProducts(); // Cập nhật danh sách
            }
        });

        btnDeleteProduct.setOnClickListener(v -> {
            productDAO.deleteProduct(product.getProductId());
            loadProducts(); // Cập nhật danh sách
            Toast.makeText(requireContext(), "Sản phẩm đã bị xóa!", Toast.LENGTH_SHORT).show();
        });

        builder.setView(dialogView);
        currentDialog = builder.create();
        currentDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy ứng dụng chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Uri> parseAdditionalImages(String additionalImagesText) {
        List<Uri> imageUris = new ArrayList<>();
        if (additionalImagesText != null && !additionalImagesText.trim().isEmpty()) {
            String[] items = additionalImagesText.split(",");
            for (String item : items) {
                String uriStr = item.trim().replaceAll("Item \\d+:\\s*", "").trim();
                if (!uriStr.isEmpty()) {
                    imageUris.add(Uri.parse(uriStr));
                }
            }
        }
        return imageUris;
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
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