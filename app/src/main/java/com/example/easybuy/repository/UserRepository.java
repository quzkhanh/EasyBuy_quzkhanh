package com.example.easybuy.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.model.User;
import com.example.easybuy.network.ApiClient;
import com.example.easybuy.network.UserApi;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserApi userApi;

    public UserRepository() {
        userApi = ApiClient.getClient().create(UserApi.class);
    }

    public void loginUser(String email, String password,
                          MutableLiveData<Boolean> loginStatus,
                          MutableLiveData<User> userData,
                          MutableLiveData<String> errorMessage) {

        HashMap<String, String> req = new HashMap<>();
        req.put("email", email);
        req.put("password", password);

        userApi.loginUser(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<String, Object> body = response.body();
                    HashMap<String, Object> userMap = (HashMap<String, Object>) body.get("user");

                    User user = new User();
                    user.setUserId((int) (double) userMap.get("id")); // cần ép kiểu vì parse từ JSON
                    user.setFullName((String) userMap.get("full_name"));
                    user.setEmail((String) userMap.get("email"));
                    user.setPhoneNumber((String) userMap.get("phoneNumber"));

                    loginStatus.setValue(true);
                    userData.setValue(user);
                } else {
                    errorMessage.setValue("Đăng nhập thất bại!");
                    loginStatus.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
                loginStatus.setValue(false);
            }
        });
    }

    public void registerUser(User user,
                             MutableLiveData<Boolean> signUpStatus,
                             MutableLiveData<User> userData,
                             MutableLiveData<String> errorMessage) {

        userApi.registerUser(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userData.setValue(user);
                    signUpStatus.setValue(true);
                } else {
                    signUpStatus.setValue(false);
                    errorMessage.setValue("Đăng ký thất bại!");
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                signUpStatus.setValue(false);
                errorMessage.setValue("Lỗi: " + t.getMessage());
            }
        });
    }

    public void updateUser(int id, User user,
                           MutableLiveData<Boolean> updateStatus,
                           MutableLiveData<String> errorMessage) {
        userApi.updateUser(id, user).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                updateStatus.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                updateStatus.setValue(false);
                errorMessage.setValue("Lỗi khi cập nhật: " + t.getMessage());
            }
        });
    }

    public void deleteUser(int id,
                           MutableLiveData<Boolean> deleteStatus,
                           MutableLiveData<String> errorMessage) {
        userApi.deleteUser(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                deleteStatus.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                deleteStatus.setValue(false);
                errorMessage.setValue("Lỗi khi xóa: " + t.getMessage());
            }
        });
    }
}
