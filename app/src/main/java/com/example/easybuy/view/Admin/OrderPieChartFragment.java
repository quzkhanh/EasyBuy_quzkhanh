package com.example.easybuy.view.Admin;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.easybuy.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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
        // Tạo danh sách entries cho PieChart
        List<PieEntry> entries = new ArrayList<>();
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
        int[] counts = chartManager.getOrderCountsByStatus();

        for (int i = 0; i < statuses.length; i++) {
            if (counts[i] > 0) {
                entries.add(new PieEntry(counts[i], statuses[i]));
            }
        }

        // Nếu không có dữ liệu, hiển thị "No Data"
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "No Data"));
        }

        // Tùy chỉnh PieDataSet
        PieDataSet dataSet = new PieDataSet(entries, "Tỷ lệ trạng thái đơn hàng");

        // Custom màu sắc (màu tươi sáng, hiện đại)
        int[] customColors = {
                Color.parseColor("#FF6F61"), // Coral (Pending)
                Color.parseColor("#6B5B95"), // Purple (Processing)
                Color.parseColor("#88B04B"), // Green (Shipped)
                Color.parseColor("#F7CAC9"), // Pink (Delivered)
                Color.parseColor("#92A8D1")  // Blue (Cancelled)
        };
        dataSet.setColors(customColors);

        // Hiển thị giá trị dưới dạng phần trăm
        dataSet.setValueFormatter(new PercentFormatter(pieChart));
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        // Tùy chỉnh khoảng cách giữa các phần của PieChart
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f); // Khi nhấn vào phần, nó sẽ phóng to ra

        // Tạo PieData
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Tùy chỉnh PieChart
        pieChart.setUsePercentValues(true); // Hiển thị giá trị dưới dạng phần trăm
        pieChart.getDescription().setEnabled(false); // Tắt description
        pieChart.setHoleRadius(55f); // Lỗ giữa to hơn
        pieChart.setTransparentCircleRadius(55f); // Vòng trong suốt lớn hơn
        pieChart.setHoleColor(Color.WHITE); // Lỗ giữa màu trắng
        pieChart.setCenterText("Đơn Hàng\n" + selectedYear); // Text ở giữa
        pieChart.setCenterTextSize(17f);
        pieChart.setCenterTextColor(Color.BLACK);

        // Tùy chỉnh Legend (chú thích)
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);

        // Animation xịn hơn
        pieChart.animateY(1500, Easing.EaseInOutQuad);
        pieChart.spin(1000, 0, 360, Easing.EaseInOutQuad); // Hiệu ứng xoay

        // Làm mới chart
        pieChart.invalidate();
    }
}