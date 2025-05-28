package com.example.easybuy.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.easybuy.database.dao.FavoriteDAO;
import com.example.easybuy.model.Favorite;
import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {
    private final FavoriteDAO favoriteDAO;
    private final MutableLiveData<Boolean> addFavoriteStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Favorite>> userFavorites = new MutableLiveData<>();
    private final MutableLiveData<Boolean> removeFavoriteStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteDAO = new FavoriteDAO(application.getApplicationContext());
    }

    // Getter cho LiveData
    public LiveData<Boolean> getAddFavoriteStatus() {
        return addFavoriteStatus;
    }

    public LiveData<List<Favorite>> getUserFavorites() {
        return userFavorites;
    }

    public LiveData<Boolean> getRemoveFavoriteStatus() {
        return removeFavoriteStatus;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Thêm sản phẩm vào danh sách yêu thích
    public void addToFavorites(int userId, int productId) {
        try {
            long id = favoriteDAO.addToFavorites(userId, productId);
            if (id > 0) {
                addFavoriteStatus.setValue(true);
                fetchFavoritesByUserId(userId); // Cập nhật danh sách yêu thích sau khi thêm
            } else {
                addFavoriteStatus.setValue(false);
                errorMessage.setValue("Không thể thêm sản phẩm vào danh sách yêu thích!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi thêm sản phẩm: " + e.getMessage());
            addFavoriteStatus.setValue(false);
        }
    }

    // Lấy danh sách yêu thích theo userId
    public void fetchFavoritesByUserId(int userId) {
        try {
            List<Favorite> favorites = favoriteDAO.getFavoritesByUserId(userId);
            userFavorites.setValue(favorites);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi lấy danh sách yêu thích: " + e.getMessage());
        }
    }

    // Xóa sản phẩm khỏi danh sách yêu thích
    public void removeFromFavorites(int userId, int productId) {
        try {
            boolean success = favoriteDAO.removeFromFavorites(userId, productId);
            removeFavoriteStatus.setValue(success);
            if (success) {
                fetchFavoritesByUserId(userId); // Cập nhật danh sách yêu thích sau khi xóa
            } else {
                errorMessage.setValue("Không thể xóa sản phẩm khỏi danh sách yêu thích!");
            }
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi xóa sản phẩm: " + e.getMessage());
            removeFavoriteStatus.setValue(false);
        }
    }

    // Kiểm tra sản phẩm đã nằm trong danh sách yêu thích chưa
    public void checkIsFavorite(int userId, int productId) {
        try {
            boolean favorite = favoriteDAO.isFavorite(userId, productId);
            isFavorite.setValue(favorite);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi kiểm tra sản phẩm yêu thích: " + e.getMessage());
            isFavorite.setValue(false);
        }
    }
}