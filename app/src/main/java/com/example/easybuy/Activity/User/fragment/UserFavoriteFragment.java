package com.example.easybuy.Activity.User.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Database.Adapter.FavoriteAdapter;
import com.example.easybuy.Database.DAO.FavoriteDAO;
import com.example.easybuy.Model.Favorite;
import com.example.easybuy.R;
import com.example.easybuy.Utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class UserFavoriteFragment extends Fragment {
    private RecyclerView recyclerViewFavorites;
    private FavoriteAdapter favoriteAdapter;
    private TextView tvNoFavorites;
    private FavoriteDAO favoriteDAO;
    private SessionManager sessionManager;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_favorite, container, false);

        // Ánh xạ các view
        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites);

        // Khởi tạo SessionManager và FavoriteDAO
        sessionManager = new SessionManager(requireContext());
        favoriteDAO = new FavoriteDAO(requireContext());

        // Lấy userId từ SessionManager
        userId = sessionManager.getUserId();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Tải danh sách yêu thích
        loadFavorites();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteAdapter = new FavoriteAdapter(getContext(), new ArrayList<>());
        recyclerViewFavorites.setAdapter(favoriteAdapter);

        // Thêm chức năng swipe-to-delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new FavoriteAdapter.SwipeToDeleteCallback(favoriteAdapter, requireContext())
        );
        itemTouchHelper.attachToRecyclerView(recyclerViewFavorites);
    }

    private void loadFavorites() {
        if (userId != -1) {
            List<Favorite> favorites = favoriteDAO.getFavoritesByUserId(userId);

            if (favorites.isEmpty()) {
                tvNoFavorites.setVisibility(View.VISIBLE);
                recyclerViewFavorites.setVisibility(View.GONE);
            } else {
                tvNoFavorites.setVisibility(View.GONE);
                recyclerViewFavorites.setVisibility(View.VISIBLE);
                favoriteAdapter.setFavoriteList(favorites);
            }
        } else {
            tvNoFavorites.setText("Vui lòng đăng nhập để xem danh sách yêu thích");
            tvNoFavorites.setVisibility(View.VISIBLE);
            recyclerViewFavorites.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(); // Cập nhật lại danh sách khi quay lại fragment
    }
}