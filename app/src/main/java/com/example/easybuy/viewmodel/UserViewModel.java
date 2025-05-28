package com.example.easybuy.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.easybuy.database.dao.UserDAO;
import com.example.easybuy.model.User;

public class UserViewModel extends AndroidViewModel {
    private final UserDAO userDAO;
    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteStatus = new MutableLiveData<>();
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        userDAO = new UserDAO(application.getApplicationContext());
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

    public LiveData<User> getUserData() {
        return userData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Kiểm tra đăng nhập người dùng
    public void loginUser(String email, String password) {
        try {
            User user = userDAO.checkLogin(email, password);
            if (user != null) {
                loginStatus.setValue(true);
                userData.setValue(user);
            } else {
                loginStatus.setValue(false);
                errorMessage.setValue("Email hoặc mật khẩu không đúng!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi đăng nhập: " + e.getMessage());
            loginStatus.setValue(false);
        }
    }

    // Thêm người dùng mới
    public void signUpUser(String fullName, String email, String password, String phoneNumber) {
        try {
            if (userDAO.isEmailExists(email)) {
                errorMessage.setValue("Email đã tồn tại!");
                signUpStatus.setValue(false);
            } else {
                User user = new User(0, fullName, email, password, phoneNumber);
                long id = userDAO.addUser(user);
                if (id > 0) {
                    user.setUserId((int) id);
                    userData.setValue(user);
                    signUpStatus.setValue(true);
                } else {
                    errorMessage.setValue("Không thể thêm người dùng!");
                    signUpStatus.setValue(false);
                }
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi đăng ký: " + e.getMessage());
            signUpStatus.setValue(false);
        }
    }

    // Cập nhật thông tin người dùng
    public void updateUser(User user) {
        try {
            int rowsAffected = userDAO.updateUser(user);
            if (rowsAffected > 0) {
                userData.setValue(user);
                updateStatus.setValue(true);
            } else {
                errorMessage.setValue("Không thể cập nhật thông tin người dùng!");
                updateStatus.setValue(false);
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi cập nhật: " + e.getMessage());
            updateStatus.setValue(false);
        }
    }

    // Xóa người dùng
    public void deleteUser(int userId) {
        try {
            int rowsAffected = userDAO.deleteUser(userId);
            if (rowsAffected > 0) {
                deleteStatus.setValue(true);
            } else {
                errorMessage.setValue("Không thể xóa người dùng!");
                deleteStatus.setValue(false);
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi xóa: " + e.getMessage());
            deleteStatus.setValue(false);
        }
    }

    // Lấy thông tin người dùng theo email
    public void getUserByEmail(String email) {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user != null) {
                userData.setValue(user);
            } else {
                errorMessage.setValue("Không tìm thấy người dùng với email này!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi lấy dữ liệu: " + e.getMessage());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        userDAO.close(); // Đóng UserDAO khi ViewModel bị hủy
    }
}