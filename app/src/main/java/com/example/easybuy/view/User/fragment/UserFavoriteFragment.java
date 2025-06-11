package com.example.easybuy.view.User.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.view.Adapter.FavoriteAdapter;
import com.example.easybuy.database.dao.FavoriteDAO;
import com.example.easybuy.model.Favorite;
import com.example.easybuy.R;
import com.example.easybuy.utils.SessionManager;
import com.example.easybuy.view.User.UserMainActivity;

import java.util.ArrayList;
import java.util.List;

public class UserFavoriteFragment extends Fragment {
    private RecyclerView recyclerViewFavorites;
    private LinearLayout emptyFavoriteLayout;
    private ImageView ivEmptyFavorite;
    private TextView tvNoFavorites;
    private TextView tvFavoriteListHeading;
    private Button btnExploreNow;
    private FavoriteAdapter favoriteAdapter;
    private FavoriteDAO favoriteDAO;
    private SessionManager sessionManager;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_favorite, container, false);

        // Ánh xạ các view
        tvFavoriteListHeading = view.findViewById(R.id.tvFavoriteListHeading);
        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
        emptyFavoriteLayout = view.findViewById(R.id.emptyFavoriteLayout);
        ivEmptyFavorite = view.findViewById(R.id.ivEmptyFavorite);
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites);
        btnExploreNow = view.findViewById(R.id.btnExploreNow);

        // Khởi tạo SessionManager và FavoriteDAO
        sessionManager = new SessionManager(requireContext());
        favoriteDAO = new FavoriteDAO(requireContext());

        // Lấy userId từ SessionManager
        userId = sessionManager.getUserId();

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Tải danh sách yêu thích
        loadFavorites();

        // Xử lý nút Khám phá ngay
        btnExploreNow.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UserMainActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteAdapter = new FavoriteAdapter(getContext(), new ArrayList<>());
        recyclerViewFavorites.setAdapter(favoriteAdapter);

        // Thêm animation khi item xuất hiện
        recyclerViewFavorites.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                getContext(), R.anim.layout_animation_fall_down));

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
                emptyFavoriteLayout.setVisibility(View.VISIBLE);
                recyclerViewFavorites.setVisibility(View.GONE);
                tvNoFavorites.setText("Bạn chưa tìm được sản phẩm yêu thích ư?");
                emptyFavoriteLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
            } else {
                emptyFavoriteLayout.setVisibility(View.GONE);
                recyclerViewFavorites.setVisibility(View.VISIBLE);
                favoriteAdapter.setFavoriteList(favorites);
                recyclerViewFavorites.scheduleLayoutAnimation();
            }
        } else {
            emptyFavoriteLayout.setVisibility(View.VISIBLE);
            recyclerViewFavorites.setVisibility(View.GONE);
            tvNoFavorites.setText("Đăng nhập để xem yêu thích ! 😎");
            // Hình khác cho trạng thái chưa đăng nhập
            emptyFavoriteLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
}