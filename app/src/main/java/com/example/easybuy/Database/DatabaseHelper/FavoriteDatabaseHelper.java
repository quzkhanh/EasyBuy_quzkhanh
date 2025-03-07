package com.example.easybuy.Database.DatabaseHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.easybuy.Model.Favorite;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDatabaseHelper {
    private final DatabaseHelper dbHelper;

    public FavoriteDatabaseHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long addFavorite(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("product_id", productId);

        Cursor cursor = db.rawQuery("SELECT id FROM " + DatabaseHelper.TABLE_FAVORITES +
                        " WHERE user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        if (cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }
        cursor.close();

        return db.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
    }

    public List<Favorite> getFavoritesByUserId(int userId) {
        List<Favorite> favorites = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT f.id, f.user_id, f.product_id, p.product_name, p.price, p.image_url " +
                "FROM " + DatabaseHelper.TABLE_FAVORITES + " f " +
                "JOIN " + DatabaseHelper.TABLE_PRODUCT + " p ON f.product_id = p.product_id " +
                "WHERE f.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                int favoriteId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

                favorites.add(new Favorite(favoriteId, userId, productId, productName, price, imageUrl));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favorites;
    }

    public boolean deleteFavorite(int favoriteId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_FAVORITES, "id = ?",
                new String[]{String.valueOf(favoriteId)});
        return rowsAffected > 0;
    }
}