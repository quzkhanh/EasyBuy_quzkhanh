package com.example.easybuy.view.Adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.database.dao.OrderDAO;
import com.example.easybuy.database.helper.DatabaseHelper;
import com.example.easybuy.model.Order;
import com.example.easybuy.R;

import java.util.List;

/**
 * OrderAdapter là một adapter dùng để hiển thị danh sách các đơn hàng (Order) trong một RecyclerView.
 * Adapter này cung cấp các chức năng sau:
 * - Hiển thị thông tin chi tiết của từng đơn hàng trong danh sách.
 * - Cho phép người dùng vuốt sang trái để hủy đơn hàng (nếu đơn hàng đang ở trạng thái "Pending").
 * - Tương tác với cơ sở dữ liệu thông qua OrderDAO để thực hiện các thao tác như xóa đơn hàng.
 * - Cập nhật giao diện khi có sự thay đổi trong danh sách đơn hàng.
 * - Sử dụng SwipeToDeleteCallback để xử lý thao tác vuốt để xóa.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private DatabaseHelper databaseHelper;

    // Constructor không có OnOrderCancelListener
    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Mã đơn: #" + order.getOrderId());
        holder.tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.tvOrderStatus.setText("Trạng thái: " + order.getStatus());
        holder.tvOrderTotal.setText(String.format("Tổng tiền: %.0f VNĐ", order.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    public void removeOrder(int position) {
        if (position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }

    // Phương thức để tạo SwipeToDeleteCallback
    public static class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private OrderAdapter mAdapter;
        private Drawable deleteIcon;
        private final ColorDrawable background;

        public SwipeToDeleteCallback(OrderAdapter adapter, Context context) {
            super(0, ItemTouchHelper.LEFT);
            mAdapter = adapter;
            deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete2);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Order order = mAdapter.orderList.get(position);

            // Chỉ cho phép hủy đơn ở trạng thái Pending
            if ("Pending".equalsIgnoreCase(order.getStatus())) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    new AlertDialog.Builder(mAdapter.context)
                            .setTitle("Xác nhận hủy")
                            .setMessage("Bạn có chắc muốn hủy đơn hàng #" + order.getOrderId() + "?")
                            .setPositiveButton("Có", (dialog, which) -> {
                                OrderDAO orderDAO = new OrderDAO(mAdapter.context);
                                boolean isDeleted = orderDAO.deleteOrder(order.getOrderId());
                                if (isDeleted) {
                                    mAdapter.removeOrder(position);
                                    Toast.makeText(mAdapter.context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mAdapter.context, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                    mAdapter.notifyItemChanged(position);
                                }
                            })
                            .setNegativeButton("Không", (dialog, which) -> {
                                mAdapter.notifyItemChanged(position);
                            })
                            .setOnCancelListener(dialog -> {
                                mAdapter.notifyItemChanged(position);
                            })
                            .create()
                            .show();
                }, 200);
            } else {
                Toast.makeText(mAdapter.context,
                        "Chỉ được hủy đơn hàng ở trạng thái Pending",
                        Toast.LENGTH_SHORT).show();
                mAdapter.notifyItemChanged(position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c,
                                @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY,
                                int actionState,
                                boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            // Vẽ icon và background khi trượt
            if (dX < 0) { // Trượt sang trái
                background.setBounds(
                        itemView.getRight() + (int)dX - backgroundCornerOffset,
                        itemView.getTop(),
                        itemView.getRight(),
                        itemView.getBottom()
                );

                // Vẽ icon xóa
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.draw(c);
                deleteIcon.draw(c);
            }
        }
    }
}