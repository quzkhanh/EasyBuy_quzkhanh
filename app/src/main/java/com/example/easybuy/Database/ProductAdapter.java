package com.example.easybuy.Database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private OnProductClickListener onProductClickListener; // Thêm listener cho sự kiện click

    // Interface cho sự kiện click
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Constructor với listener tùy chọn
    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>(); // Đảm bảo không null
        this.onProductClickListener = listener;
    }

    // Constructor không có listener (giữ tương thích với code cũ)
    public ProductAdapter(Context context, List<Product> productList) {
        this(context, productList, null);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getProductName());
        holder.tvPrice.setText(String.format("%,.0f VNĐ", product.getPrice()));

        // Load ảnh với Glide, thêm placeholder và error
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.product_placeholder) // Ảnh placeholder khi đang tải
                .error(R.drawable.product_placeholder) // Ảnh mặc định nếu lỗi
                .into(holder.ivProductImage);

        // Thêm sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Phương thức cập nhật danh sách sản phẩm
    public void setProductList(List<Product> newProductList) {
        this.productList = newProductList != null ? newProductList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Lấy danh sách sản phẩm hiện tại (nếu cần)
    public List<Product> getProductList() {
        return productList;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }
    }
}