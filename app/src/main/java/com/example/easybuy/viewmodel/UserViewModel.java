package com.example.easybuy.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.model.User;
import com.example.easybuy.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;

    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteStatus = new MutableLiveData<>();
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository();
    }

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

    public void loginUser(String email, String password) {
        repository.loginUser(email, password, loginStatus, userData, errorMessage);
    }

    public void signUpUser(User user) {
        repository.registerUser(user, signUpStatus, userData, errorMessage);
    }

    public void updateUser(int id, User user) {
        repository.updateUser(id, user, updateStatus, errorMessage);
    }

    public void deleteUser(int id) {
        repository.deleteUser(id, deleteStatus, errorMessage);
    }

    public void signUpUser(String fullName, String email, String password, String s) {
    }
}
