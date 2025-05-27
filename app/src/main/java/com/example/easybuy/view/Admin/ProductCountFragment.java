package com.example.easybuy.view.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.easybuy.R;

import java.util.Calendar;

public class ProductCountFragment extends Fragment {

    private TextView tvProductCount;
    private ChartManager chartManager;
    private int adminId;

    public ProductCountFragment(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_count, container, false);
        tvProductCount = view.findViewById(R.id.tvProductCount);

        chartManager = new ChartManager(requireContext(), adminId, Calendar.getInstance().get(Calendar.YEAR));
        tvProductCount.setText(String.valueOf(chartManager.getProductCount()));

        return view;
    }
}