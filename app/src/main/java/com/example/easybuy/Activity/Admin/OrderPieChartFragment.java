package com.example.easybuy.Activity.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.easybuy.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class OrderPieChartFragment extends Fragment {

    private PieChart pieChart;
    private ChartManager chartManager;
    private int adminId;
    private int selectedYear;

    public OrderPieChartFragment(int adminId, int selectedYear) {
        this.adminId = adminId;
        this.selectedYear = selectedYear;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_pie_chart, container, false);
        pieChart = view.findViewById(R.id.pieChart);

        chartManager = new ChartManager(requireContext(), adminId, selectedYear);
        setupPieChart();

        return view;
    }

    private void setupPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
        int[] counts = chartManager.getOrderCountsByStatus();

        for (int i = 0; i < statuses.length; i++) {
            if (counts[i] > 0) {
                entries.add(new PieEntry(counts[i], statuses[i]));
            }
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "No Data"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Tỷ lệ trạng thái đơn hàng");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}