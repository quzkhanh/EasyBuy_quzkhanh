package com.example.easybuy.Database;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.easybuy.Model.Product;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private static final String TAG = "ProductAdapter";
    private Context context;
    private List<Product> productList;
    private OnProductClickListener onProductClickListener;
    private FavoriteDAO favoriteDAO;
    private int currentUserId;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, int currentUserId, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
        this.onProductClickListener = listener;
        this.favoriteDAO = new FavoriteDAO(context);
        this.currentUserId = currentUserId;
    }

    public ProductAdapter(Context context, List<Product> productList, int currentUserId) {
        this(context, productList, currentUserId, null);
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

        String imageUrl = product.getImageUrl();
        Log.d(TAG, "Loading image for position " + position + ": " + (imageUrl != null ? imageUrl : "null"));

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.product_placeholder)
                            .error(R.drawable.placeholder_error)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (e != null) {
                                Log.e(TAG, "Glide load failed for URL " + imageUrl + ": " + e.getMessage());
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.ivProductImage);
        } else {
            Log.e(TAG, "Image URL is null or empty for product: " + product.getProductName());
            holder.ivProductImage.setImageResource(R.drawable.placeholder_error);
        }

        // Xử lý nút Favorite
        if (currentUserId == -1) {
            holder.btnFavorite.setEnabled(false);
            holder.btnFavorite.setOnClickListener(v ->
                    Toast.makeText(context, "Vui lòng đăng nhập để thêm vào yêu thích!", Toast.LENGTH_SHORT).show());
        } else {
            boolean isFavorite = favoriteDAO.isFavorite(currentUserId, product.getProductId());
            holder.btnFavorite.setSelected(isFavorite);
            holder.btnFavorite.setOnClickListener(v -> {
                if (isFavorite) {
                    if (favoriteDAO.removeFromFavorites(currentUserId, product.getProductId())) {
                        holder.btnFavorite.setSelected(false);
                        Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    long result = favoriteDAO.addToFavorites(currentUserId, product.getProductId());
                    if (result != -1) { // Kiểm tra xem thêm thành công không
                        holder.btnFavorite.setSelected(true);
                        Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

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

    public void setProductList(List<Product> newProductList) {
        this.productList = newProductList != null ? newProductList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Product> getProductList() {
        return productList;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice;
        ImageView ivProductImage;
        ImageButton btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}