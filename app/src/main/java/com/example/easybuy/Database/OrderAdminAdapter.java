package com.example.easybuy.Database;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Model.Order;
import com.example.easybuy.R;

import java.util.List;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;
    private OrderDAO orderDAO;
    private Context context;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdminAdapter(List<Order> orderList, OnOrderClickListener listener, Context context) {
        this.orderList = orderList;
        this.listener = listener;
        this.context = context;
        this.orderDAO = new OrderDAO(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvUserId, tvProductId, tvStatus, tvOrderDate;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    showOrderDetailDialog(orderList.get(position), position);
                }
            });
        }

        void bind(Order order) {
            tvOrderId.setText("Mã đơn: " + order.getOrderId());
            tvUserId.setText("Người dùng: " + order.getUserId());
            tvProductId.setText("Sản phẩm: " + order.getProductId());
            tvStatus.setText("Trạng thái: " + order.getStatus());
            tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        }

        private void showOrderDetailDialog(Order order, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_admin_order_detail, null);
            builder.setView(dialogView);

            TextView tvOrderId = dialogView.findViewById(R.id.tvOrderId);
            TextView tvUserId = dialogView.findViewById(R.id.tvUserId);
            TextView tvProductId = dialogView.findViewById(R.id.tvProductId);
            TextView tvQuantity = dialogView.findViewById(R.id.tvQuantity);
            TextView tvTotalPrice = dialogView.findViewById(R.id.tvTotalPrice);
            TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
            TextView tvOrderDate = dialogView.findViewById(R.id.tvOrderDate);
            TextView tvShippingAddress = dialogView.findViewById(R.id.tvShippingAddress);
            TextView tvPhoneNumber = dialogView.findViewById(R.id.tvPhoneNumber);
            TextView tvPaymentMethod = dialogView.findViewById(R.id.tvPaymentMethod);
            Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
            Button btnUpdateStatus = dialogView.findViewById(R.id.btnUpdateStatus);
            Button btnCancelOrder = dialogView.findViewById(R.id.btnCancelOrder);
            Button btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);

            // Đổ dữ liệu vào dialog
            tvOrderId.setText("Mã đơn: " + order.getOrderId());
            tvUserId.setText("Người dùng: " + order.getUserId());
            tvProductId.setText("Sản phẩm: " + order.getProductId());
            tvQuantity.setText("Số lượng: " + order.getQuantity());
            tvTotalPrice.setText("Tổng giá: " + order.getTotalPrice());
            tvStatus.setText("Trạng thái: " + order.getStatus());
            tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
            tvShippingAddress.setText("Địa chỉ giao hàng: " + order.getShippingAddress());
            tvPhoneNumber.setText("Số điện thoại: " + order.getPhoneNumber());
            tvPaymentMethod.setText("Phương thức thanh toán: " + order.getPaymentMethod());

            // Cài đặt Spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.order_status_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);
            spinnerStatus.setSelection(getIndex(spinnerStatus, order.getStatus()));

            // Xử lý cập nhật trạng thái
            btnUpdateStatus.setOnClickListener(v -> {
                String newStatus = spinnerStatus.getSelectedItem().toString();
                if (orderDAO.updateOrderStatus(order.getOrderId(), newStatus)) {
                    order.setStatus(newStatus);
                    tvStatus.setText("Trạng thái: " + newStatus);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            // Xử lý hủy đơn
            btnCancelOrder.setOnClickListener(v -> {
                if (orderDAO.deleteOrder(order.getOrderId())) {
                    orderList.remove(order);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                    builder.create().dismiss(); // Đóng dialog khi hủy thành công
                } else {
                    Toast.makeText(context, "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            });

            // Đóng dialog
            btnCloseDialog.setOnClickListener(v -> {
                AlertDialog dialog = builder.create(); // Tạo và lưu instance dialog
                dialog.dismiss(); // Đóng dialog
            });

            AlertDialog dialog = builder.create(); // Tạo instance dialog
            dialog.show(); // Hiển thị dialog
        }

        private int getIndex(Spinner spinner, String value) {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                    return i;
                }
            }
            return 0;
        }
    }
}