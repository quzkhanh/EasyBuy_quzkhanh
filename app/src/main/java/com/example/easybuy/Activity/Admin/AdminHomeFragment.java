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
import com.example.easybuy.Database.ProductAdapter;
import com.example.easybuy.Database.ProductDAO;
import com.example.easybuy.Model.Product;
import com.example.easybuy.Model.ProductImage;
import com.example.easybuy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private TextView tvEmptyList;
    private EditText etSearchProduct; // Thêm biến cho thanh tìm kiếm
    private FloatingActionButton fabAddProduct;
    private ProductAdapter productAdapter;
    private ProductDAO productDAO;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private AlertDialog currentDialog;
    private List<Uri> additionalImageUris = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        etSearchProduct = view.findViewById(R.id.etSearchProduct); // Khởi tạo thanh tìm kiếm
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        productDAO = new ProductDAO(requireContext());

        setupImagePicker();
        setupRecyclerView();
        setupSearchListener(); // Gọi phương thức để thiết lập lắng nghe tìm kiếm

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        loadProducts();

        return view;
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
        List<Product> allProducts = productDAO.getAllProducts();
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
                                ivDialogProductImage.setTag(selectedUri.toString()); // Lưu URI mới
                                Toast.makeText(requireContext(), "Ảnh chính đã được chọn", Toast.LENGTH_SHORT).show();
                            } else if (requestingButton != null && requestingButton.getId() == R.id.ibPickAdditionalImage) {
                                additionalImageUris.add(selectedUri);
                                recyclerViewImages.getAdapter().notifyDataSetChanged(); // Làm mới RecyclerView
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
        ImageButton ibPickDialogImage = dialogView.findViewById(R.id.ibPickDialogImage);
        RecyclerView recyclerViewImages = dialogView.findViewById(R.id.recyclerViewImages);
        ImageButton ibPickAdditionalImage = dialogView.findViewById(R.id.ibPickAdditionalImage);
        Button btnEditProduct = dialogView.findViewById(R.id.btnEditProduct);
        Button btnDeleteProduct = dialogView.findViewById(R.id.btnDeleteProduct);

        // Điền thông tin sản phẩm
        Glide.with(this).load(product.getImageUrl()).into(ivDialogProductImage);
        ivDialogProductImage.setTag(product.getImageUrl()); // Lưu URI ban đầu
        etDialogProductName.setText(product.getProductName());
        etDialogPrice.setText(String.valueOf(product.getPrice()));
        etDialogDescription.setText(product.getDescription());

        // Cấu hình RecyclerView cho ảnh bổ sung
        additionalImageUris.clear(); // Xóa danh sách cũ
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
                // Cập nhật ảnh chính từ tag của ImageView
                String newImageUrl = (String) ivDialogProductImage.getTag();
                if (newImageUrl != null && !newImageUrl.isEmpty()) {
                    product.setImageUrl(newImageUrl);
                }

                // Cập nhật ảnh bổ sung vào database
                List<Uri> additionalUris = new ArrayList<>(additionalImageUris);
                productDAO.updateProductImages(product.getProductId(), additionalUris); // Giả định có phương thức này

                productDAO.updateProduct(product);
                etDialogProductName.setEnabled(false);
                etDialogPrice.setEnabled(false);
                etDialogDescription.setEnabled(false);
                ibPickDialogImage.setVisibility(View.GONE);
                ibPickAdditionalImage.setVisibility(View.GONE);
                btnEditProduct.setText("Sửa");
                isEditing[0] = false;
                Toast.makeText(requireContext(), "Sản phẩm đã được cập nhật!", Toast.LENGTH_SHORT).show();
                loadProducts(); // Cập nhật danh sách
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
                    productDAO.deleteProduct(product.getProductId());
                    loadProducts(); // Cập nhật danh sách
                    Toast.makeText(requireContext(), "Sản phẩm đã bị xóa!", Toast.LENGTH_SHORT).show();
                    if (currentDialog != null && currentDialog.isShowing()) {
                        currentDialog.dismiss();
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

    // Adapter cho RecyclerView ảnh bổ sung
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
                    additionalImageUris.remove(adapterPosition); // Xóa ảnh tại vị trí hiện tại
                    notifyItemRemoved(adapterPosition); // Cập nhật RecyclerView
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