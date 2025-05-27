package com.example.easybuy.database.dao;

import android.content.Context;

import com.example.easybuy.database.helper.DatabaseHelper;
import com.example.easybuy.database.helper.FavoriteDatabaseHelper; // Sử dụng FavoriteDatabaseHelper
import com.example.easybuy.model.Favorite;

import java.util.List;

public class FavoriteDAO {
    private final FavoriteDatabaseHelper favoriteDbHelper; // Sửa kiểu từ FavoriteAdapter thành FavoriteDatabaseHelper

    public FavoriteDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.favoriteDbHelper = new FavoriteDatabaseHelper(dbHelper); // Khởi tạo đúng với FavoriteDatabaseHelper
    }

    // Thêm sản phẩm vào danh sách yêu thích
    public long addToFavorites(int userId, int productId) {
        return favoriteDbHelper.addFavorite(userId, productId);
    }

    // Lấy danh sách sản phẩm yêu thích theo userId
    public List<Favorite> getFavoritesByUserId(int userId) {
        return favoriteDbHelper.getFavoritesByUserId(userId);
    }

    // Xóa sản phẩm khỏi danh sách yêu thích dựa trên favoriteId
    public boolean deleteFavorite(int favoriteId) {
        return favoriteDbHelper.deleteFavorite(favoriteId);
    }

    // Xóa sản phẩm khỏi danh sách yêu thích dựa trên userId và productId
    public boolean removeFromFavorites(int userId, int productId) {
        List<Favorite> favorites = favoriteDbHelper.getFavoritesByUserId(userId);
        for (Favorite favorite : favorites) {
            if (favorite.getProductId() == productId) {
                return favoriteDbHelper.deleteFavorite(favorite.getFavoriteId()); // Sửa getId thành getFavoriteId
            }
        }
        return false;
    }

    // Kiểm tra xem sản phẩm đã nằm trong danh sách yêu thích chưa
    public boolean isFavorite(int userId, int productId) {
        List<Favorite> favorites = favoriteDbHelper.getFavoritesByUserId(userId);
        for (Favorite favorite : favorites) {
            if (favorite.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }
}