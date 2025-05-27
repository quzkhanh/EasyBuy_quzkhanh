package com.example.easybuy.view.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybuy.database.dao.FavoriteDAO;
import com.example.easybuy.model.Favorite;
import com.example.easybuy.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Adapter để hiển thị danh sách các sản phẩm yêu thích trong RecyclerView.
 * Nó xử lý việc hiển thị chi tiết sản phẩm, vuốt để xóa và tương tác
 * với FavoriteDAO để thực hiện các thao tác cơ sở dữ liệu.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private Context context;
    private List<Favorite> favoriteList;
    private FavoriteDAO favoriteDAO;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.favoriteDAO = new FavoriteDAO(context); // Khởi tạo FavoriteDAO
    }

    public void setFavoriteList(List<Favorite> favoriteList) {
        this.favoriteList = favoriteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);

        holder.tvFavoriteName.setText(favorite.getProductName());
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        holder.tvFavoritePrice.setText("Giá: " + df.format(favorite.getPrice()));

        Glide.with(context)
                .load(favorite.getImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .into(holder.ivFavoriteImage);
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFavoriteImage;
        TextView tvFavoriteName, tvFavoritePrice;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFavoriteImage = itemView.findViewById(R.id.ivFavoriteImage);
            tvFavoriteName = itemView.findViewById(R.id.tvFavoriteName);
            tvFavoritePrice = itemView.findViewById(R.id.tvFavoritePrice);
        }
    }

    // Swipe to delete callback với dialog xác nhận
    public static class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private FavoriteAdapter adapter;
        private FavoriteDAO favoriteDAO;
        private Context context;

        public SwipeToDeleteCallback(FavoriteAdapter adapter, Context context) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
            this.favoriteDAO = new FavoriteDAO(context); // Sử dụng FavoriteDAO
            this.context = context;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Favorite favorite = adapter.favoriteList.get(position);

            // Hiển thị dialog xác nhận
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa '" + favorite.getProductName() + "' khỏi danh sách yêu thích không?")
                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Xóa khỏi database nếu xác nhận
                            if (favoriteDAO.deleteFavorite(favorite.getFavoriteId())) {
                                adapter.favoriteList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                                adapter.notifyItemChanged(position); // Khôi phục nếu xóa thất bại
                            }
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Khôi phục lại mục khi hủy
                            adapter.notifyItemChanged(position);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }
}