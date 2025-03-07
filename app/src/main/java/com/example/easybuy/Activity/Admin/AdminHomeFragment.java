package com.example.easybuy.Activity.Admin;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.easybuy.Database.Adapter.ProductAdapter;
import com.example.easybuy.Database.DAO.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private TextView tvEmptyList;
    private EditText etSearchProduct;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private AlertDialog currentDialog;
    private List<Uri> additionalImageUris = new ArrayList<>();
    private SessionManager sessionManager;
    private int adminId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Khởi tạo các thành phần UI
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        etSearchProduct = view.findViewById(R.id.etSearchProduct);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        // Khởi tạo SessionManager trước khi sử dụng
        sessionManager = new SessionManager(requireContext());
        adminId = getAdminId(); // Lấy adminId sau khi sessionManager đã khởi tạo

        // Kiểm tra đăng nhập
        if (adminId == -1) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập với tư cách admin!", Toast.LENGTH_SHORT).show();
            return view; // Dừng xử lý nếu chưa đăng nhập
        }

        // Khởi tạo ProductDAO
        productDAO = new ProductDAO(requireContext());

        // Thiết lập các chức năng
        setupImagePicker();
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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();
                        if (selectedUri != null && currentDialog != null) {
                            ImageView ivDialogProductImage = currentDialog.findViewById(R.id.ivDialogProductImage);
                            RecyclerView recyclerViewImages = currentDialog.findViewById(R.id.recyclerViewImages);
                            ImageButton requestingButton = (ImageButton) getView().getTag();

                            if (requestingButton != null && requestingButton.getId() == R.id.ibPickDialogImage) {
                                Glide.with(this).load(selectedUri).into(ivDialogProductImage);
                                ivDialogProductImage.setTag(selectedUri.toString());
                                Toast.makeText(requireContext(), "Ảnh chính đã được chọn", Toast.LENGTH_SHORT).show();
                            } else if (requestingButton != null && requestingButton.getId() == R.id.ibPickAdditionalImage) {
                                additionalImageUris.add(selectedUri);
                                recyclerViewImages.getAdapter().notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Ảnh bổ sung đã được chọn", Toast.LENGTH_SHORT).show();
                            }
                            getView().setTag(null);
                        }
                    }
                }
        );
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Lấy currentAdminId từ SessionManager
        int currentAdminId = sessionManager.getAdminId();

        // Lấy danh sách sản phẩm của admin hiện tại
        List<Product> initialProducts = productDAO.getProductsByAdmin(adminId);

        // Khởi tạo ProductAdapter với currentAdminId
        productAdapter = new ProductAdapter(getContext(), initialProducts, currentAdminId, product -> {
            showProductDialog(product);
        });

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
        ImageButton ibPickDialogImage = dialogView.findViewById(R.id.ibPickDialogImage);
        RecyclerView recyclerViewImages = dialogView.findViewById(R.id.recyclerViewImages);
        ImageButton ibPickAdditionalImage = dialogView.findViewById(R.id.ibPickAdditionalImage);
        Button btnEditProduct = dialogView.findViewById(R.id.btnEditProduct);
        Button btnDeleteProduct = dialogView.findViewById(R.id.btnDeleteProduct);

        Glide.with(this).load(product.getImageUrl()).into(ivDialogProductImage);
        ivDialogProductImage.setTag(product.getImageUrl());
        etDialogProductName.setText(product.getProductName());
        etDialogPrice.setText(String.valueOf(product.getPrice()));
        etDialogDescription.setText(product.getDescription());

        additionalImageUris.clear();
        List<ProductImage> additionalImages = productDAO.getProductImages(product.getProductId());
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (ProductImage image : additionalImages) {
                additionalImageUris.add(Uri.parse(image.getImageUrl()));
            }
        }
        recyclerViewImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewImages.setAdapter(new AdditionalImageAdapter());

        final boolean[] isEditing = {false};

        ibPickDialogImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                getView().setTag(v);
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhấn Sửa để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        });

        ibPickAdditionalImage.setOnClickListener(v -> {
            if (isEditing[0]) {
                getView().setTag(v);
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
                ibPickDialogImage.setVisibility(View.VISIBLE);
                ibPickAdditionalImage.setVisibility(View.VISIBLE);
                btnEditProduct.setText("Lưu");
                isEditing[0] = true;
            } else {
                product.setProductName(etDialogProductName.getText().toString().trim());
                try {
                    product.setPrice(Double.parseDouble(etDialogPrice.getText().toString().trim()));
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                product.setDescription(etDialogDescription.getText().toString().trim());
                String newImageUrl = (String) ivDialogProductImage.getTag();
                if (newImageUrl != null && !newImageUrl.isEmpty()) {
                    product.setImageUrl(newImageUrl);
                }

                List<Uri> additionalUris = new ArrayList<>(additionalImageUris);
                productDAO.updateProductImages(product.getProductId(), additionalUris);

                int rowsAffected = productDAO.updateProduct(product, adminId);
                if (rowsAffected > 0) {
                    etDialogProductName.setEnabled(false);
                    etDialogPrice.setEnabled(false);
                    etDialogDescription.setEnabled(false);
                    ibPickDialogImage.setVisibility(View.GONE);
                    ibPickAdditionalImage.setVisibility(View.GONE);
                    btnEditProduct.setText("Sửa");
                    isEditing[0] = false;
                    Toast.makeText(requireContext(), "Sản phẩm đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(requireContext(), "Bạn không có quyền sửa sản phẩm này hoặc lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteProduct.setOnClickListener(v -> {
            showDeleteConfirmationDialog(product);
        });

        builder.setView(dialogView);
        currentDialog = builder.create();
        currentDialog.show();
    }

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    boolean deleted = productDAO.deleteProduct(product.getProductId(), adminId);
                    if (deleted) {
                        loadProducts();
                        Toast.makeText(requireContext(), "Sản phẩm đã bị xóa!", Toast.LENGTH_SHORT).show();
                        if (currentDialog != null && currentDialog.isShowing()) {
                            currentDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Bạn không có quyền xóa sản phẩm này hoặc lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
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