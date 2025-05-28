package com.example.easybuy.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.easybuy.database.dao.OrderDAO;
import com.example.easybuy.model.Order;
import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private final OrderDAO orderDAO;
    private final MutableLiveData<Boolean> addOrderStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> allOrders = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> userOrders = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> adminOrders = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderDAO = new OrderDAO(application.getApplicationContext());
    }

    // Getter cho LiveData
    public LiveData<Boolean> getAddOrderStatus() {
        return addOrderStatus;
    }

    public LiveData<List<Order>> getAllOrders() {
        return allOrders;
    }

    public LiveData<List<Order>> getUserOrders() {
        return userOrders;
    }

    public LiveData<List<Order>> getAdminOrders() {
        return adminOrders;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }

    public LiveData<Boolean> getDeleteStatus() {
        return deleteStatus;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Thêm đơn hàng mới
    public void addOrder(Order order) {
        try {
            long id = orderDAO.addOrder(order);
            if (id > 0) {
                order.setOrderId((int) id);
                addOrderStatus.setValue(true);
            } else {
                addOrderStatus.setValue(false);
                errorMessage.setValue("Không thể thêm đơn hàng!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi thêm đơn hàng: " + e.getMessage());
            addOrderStatus.setValue(false);
        }
    }

    // Lấy danh sách tất cả đơn hàng
    public void fetchAllOrders() {
        try {
            List<Order> orders = orderDAO.getAllOrders();
            allOrders.setValue(orders);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    // Lấy danh sách đơn hàng theo userId
    public void fetchOrdersByUserId(int userId) {
        try {
            List<Order> orders = orderDAO.getOrdersByUserId(userId);
            userOrders.setValue(orders);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi lấy đơn hàng của người dùng: " + e.getMessage());
        }
    }

    // Lấy danh sách đơn hàng theo adminId
    public void fetchOrdersByAdminId(int adminId) {
        try {
            List<Order> orders = orderDAO.getOrdersByAdminId(adminId);
            adminOrders.setValue(orders);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi lấy đơn hàng của admin: " + e.getMessage());
        }
    }

    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(int orderId, String status) {
        try {
            boolean success = orderDAO.updateOrderStatus(orderId, status);
            updateStatus.setValue(success);
            if (!success) {
                errorMessage.setValue("Không thể cập nhật trạng thái đơn hàng!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi cập nhật trạng thái: " + e.getMessage());
            updateStatus.setValue(false);
        }
    }

    // Xóa đơn hàng
    public void deleteOrder(int orderId) {
        try {
            boolean success = orderDAO.deleteOrder(orderId);
            deleteStatus.setValue(success);
            if (!success) {
                errorMessage.setValue("Không thể xóa đơn hàng!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi xóa đơn hàng: " + e.getMessage());
            deleteStatus.setValue(false);
        }
    }
}