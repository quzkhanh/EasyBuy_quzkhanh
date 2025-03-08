package com.example.easybuy.Database.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ImageAdapter này được sử dụng để hiển thị danh sách các hình ảnh trong RecyclerView.
 * Nó có thể được sử dụng cho cả người dùng thông thường và admin, với khả năng hiển thị/ẩn nút xóa ảnh.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private static final String TAG = "ImageAdapter";

    private List<String> imageUrls;
    private OnImageClickListener onImageClickListener;
    private boolean isAdmin = false; // Flag để kiểm soát hiển thị nút xóa
    private OnRemoveImageListener onRemoveImageListener; // Callback để thông báo xóa ảnh

    // Interface để xử lý sự kiện click vào ảnh
    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    // Interface để xử lý sự kiện xóa ảnh
    public interface OnRemoveImageListener {
        void onRemoveImage(int position);
    }

    public ImageAdapter(List<String> imageUrls, OnImageClickListener listener, boolean isAdmin) {
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
        this.onImageClickListener = listener;
        this.isAdmin = isAdmin;
    }

    // Setter cho OnRemoveImageListener
    public void setOnRemoveImageListener(OnRemoveImageListener listener) {
        this.onRemoveImageListener = listener;
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
        Log.d(TAG, "Binding image at position " + position + " with URL: " + imageUrl);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .into(holder.imageView);

        // Xử lý sự kiện click vào ảnh
        holder.itemView.setOnClickListener(v -> {
            if (onImageClickListener != null) {
                onImageClickListener.onImageClick(imageUrl);
            }
        });

        // Hiển thị/ẩn nút xóa dựa trên isAdmin
        if (holder.btnRemove != null) {
            holder.btnRemove.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            holder.btnRemove.setOnClickListener(v -> {
                if (onRemoveImageListener != null) {
                    onRemoveImageListener.onRemoveImage(position);
                    Log.d(TAG, "Remove button clicked at position: " + position);
                } else {
                    Log.w(TAG, "onRemoveImageListener is null, cannot remove image");
                }
            });
        } else {
            Log.w(TAG, "btnRemove is null at position " + position + ", check layout item_additional_image.xml");
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    /**
     * Cập nhật danh sách ảnh và làm mới RecyclerView.
     * @param newImageUrls Danh sách URL ảnh mới.
     */
    public void updateImages(List<String> newImageUrls) {
        this.imageUrls = newImageUrls != null ? new ArrayList<>(newImageUrls) : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Xóa ảnh tại vị trí cụ thể.
     * @param position Vị trí của ảnh cần xóa.
     */
    public void removeImageAt(int position) {
        if (position >= 0 && position < imageUrls.size()) {
            imageUrls.remove(position);
            notifyItemRemoved(position);
            Log.d(TAG, "Image removed at position " + position + ", remaining count: " + imageUrls.size());
        }
    }

    /**
     * ViewHolder để giữ các thành phần giao diện cho mỗi item ảnh.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnRemove;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivAdditionalImage);
            btnRemove = itemView.findViewById(R.id.ibRemoveImage); // Đảm bảo ID này khớp với layout
        }
    }
}