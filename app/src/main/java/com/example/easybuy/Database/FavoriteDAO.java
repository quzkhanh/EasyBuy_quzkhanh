package com.example.easybuy.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.easybuy.Model.Favorite;

import java.util.List;

public class FavoriteDAO {
    private DatabaseHelper dbHelper;

    public FavoriteDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // Thêm sản phẩm vào danh sách yêu thích
    public long addToFavorites(int userId, int productId) {
        return dbHelper.addFavorite(userId, productId);
    }

    // Lấy danh sách sản phẩm yêu thích theo userId
    public List<Favorite> getFavoritesByUserId(int userId) {
        return dbHelper.getFavoritesByUserId(userId);
    }

    // Xóa sản phẩm khỏi danh sách yêu thích dựa trên userId và productId
    public boolean removeFromFavorites(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_FAVORITES,
                "user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
        return rowsAffected > 0;
    }

    // Kiểm tra xem sản phẩm đã nằm trong danh sách yêu thích chưa
    public boolean isFavorite(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.TABLE_FAVORITES +
                        " WHERE user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Xóa sản phẩm khỏi danh sách yêu thích dựa trên favoriteId
    public boolean deleteFavorite(int favoriteId) {
        return dbHelper.deleteFavorite(favoriteId);
    }
}