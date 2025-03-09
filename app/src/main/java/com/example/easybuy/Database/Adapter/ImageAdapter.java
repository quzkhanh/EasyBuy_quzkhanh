package com.example.easybuy.Database.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.easybuy.R;

import java.util.List;

/**
 * ImageAdapter này được sử dụng để hiển thị danh sách các hình ảnh trong RecyclerView.
 * Nó có thể được sử dụng cho cả người dùng thông thường và admin, với khả năng hiển thị/ẩn nút xóa ảnh.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private OnImageClickListener onImageClickListener;
    private boolean isAdmin = false;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    public ImageAdapter(List<String> imageUrls, OnImageClickListener listener, boolean isAdmin) {
        this.imageUrls = imageUrls;
        this.onImageClickListener = listener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_additional_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(imageUrl);
            }
        });

        if (holder.btnRemove != null) {
            holder.btnRemove.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    public void updateImages(List<String> newImageUrls) {
        this.imageUrls = newImageUrls;
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnRemove;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivAdditionalImage);
            btnRemove = itemView.findViewById(R.id.ibRemoveImage);
        }
    }
}