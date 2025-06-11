package com.example.easybuy.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.easybuy.database.dao.AdminDAO;
import com.example.easybuy.model.Admin;

public class AdminViewModel extends AndroidViewModel {
    private final AdminDAO adminDAO;
    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteStatus = new MutableLiveData<>();
    private final MutableLiveData<Admin> adminData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminDAO = new AdminDAO(application.getApplicationContext());
    }

    // Getter cho LiveData
    public LiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

    public LiveData<Boolean> getSignUpStatus() {
        return signUpStatus;
    }

    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }

    public LiveData<Boolean> getDeleteStatus() {
        return deleteStatus;
    }

    public LiveData<Admin> getAdminData() {
        return adminData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Kiểm tra đăng nhập admin
    public void loginAdmin(String email, String password) {
        try {
            boolean isValid = adminDAO.checkAdminLogin(email, password);
            loginStatus.setValue(isValid); // Dùng setValue thay vì postValue vì chạy trên main thread
            if (isValid) {
                Admin admin = adminDAO.getAdminByEmail(email);
                adminData.setValue(admin);
            } else {
                errorMessage.setValue("Email hoặc mật khẩu không đúng!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi đăng nhập: " + e.getMessage());
            loginStatus.setValue(false);
        }
    }

    // Thêm admin mới
    public void signUpAdmin(String fullName, String email, String password) {
        try {
            if (adminDAO.checkAdminEmail(email)) {
                errorMessage.setValue("Email đã tồn tại!");
                signUpStatus.setValue(false);
            } else {
                Admin admin = new Admin(fullName, email, password);
                long id = adminDAO.addAdmin(admin);
                signUpStatus.setValue(id > 0);
                if (id > 0) {
                    admin.setId((int) id);
                    adminData.setValue(admin);
                } else {
                    errorMessage.setValue("Không thể thêm admin!");
                }
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi đăng ký: " + e.getMessage());
            signUpStatus.setValue(false);
        }
    }

    // Cập nhật thông tin admin
    public void updateAdmin(Admin admin) {
        try {
            boolean success = adminDAO.updateAdmin(admin);
            updateStatus.setValue(success);
            if (success) {
                adminData.setValue(admin);
            } else {
                errorMessage.setValue("Không thể cập nhật thông tin admin!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi cập nhật: " + e.getMessage());
            updateStatus.setValue(false);
        }
    }

    // Xóa admin
    public void deleteAdmin(int adminId) {
        try {
            boolean success = adminDAO.deleteAdmin(adminId);
            deleteStatus.setValue(success);
            if (!success) {
                errorMessage.setValue("Không thể xóa admin!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi xóa: " + e.getMessage());
            deleteStatus.setValue(false);
        }
    }

    // Lấy thông tin admin theo email
    public void getAdminByEmail(String email) {
        try {
            Admin admin = adminDAO.getAdminByEmail(email);
            if (admin != null) {
                adminData.setValue(admin);
            } else {
                errorMessage.setValue("Không tìm thấy admin với email này!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi lấy dữ liệu: " + e.getMessage());
        }
    }
}