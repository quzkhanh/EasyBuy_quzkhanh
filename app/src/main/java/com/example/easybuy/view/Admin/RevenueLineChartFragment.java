package com.example.easybuy.view.Admin;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.easybuy.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RevenueLineChartFragment extends Fragment {

    private LineChart lineChart;
    private ChartManager chartManager;
    private int adminId;
    private int selectedYear;

    public RevenueLineChartFragment(int adminId, int selectedYear) {
        this.adminId = adminId;
        this.selectedYear = selectedYear;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_revenue_line_chart, container, false);
        lineChart = view.findViewById(R.id.lineChart);

        chartManager = new ChartManager(requireContext(), adminId, selectedYear);
        setupLineChart();

        return view;
    }

    private void setupLineChart() {
        // Lấy dữ liệu doanh thu
        float[] revenues = chartManager.getMonthlyRevenues();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new Entry(i + 1, revenues[i]));
        }

        // Tạo LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu " + selectedYear);

        // Custom màu sắc và gradient
        dataSet.setColor(Color.parseColor("#FF6F61")); // Coral cho đường line
        dataSet.setCircleColor(Color.parseColor("#FF6F61")); // Chấm trên line
        dataSet.setLineWidth(2.5f); // Độ dày của đường line
        dataSet.setCircleRadius(5f); // Kích thước chấm
        dataSet.setDrawCircleHole(true); // Vẽ lỗ trong chấm
        dataSet.setCircleHoleColor(Color.WHITE);

        // Fill gradient bên dưới line
        dataSet.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill);
            dataSet.setFillDrawable(drawable);
        } else {
            dataSet.setFillColor(Color.parseColor("#80FF6F61")); // Fallback nếu API thấp
        }

        // Hiển thị giá trị trên điểm
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%,.0f VNĐ", value);
            }
        });

        // Tạo LineData
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Tùy chỉnh trục X (hiển thị tháng)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] months = {"Th1", "Th2", "Th3", "Th4", "Th5", "Th6",
                    "Th7", "Th8", "Th9", "Th10", "Th11", "Th12"};
            @Override
            public String getFormattedValue(float value) {
                int month = (int) value;
                if (month >= 1 && month <= 12) {
                    return months[month - 1];
                }
                return "";
            }
        });

        // Tùy chỉnh trục Y (doanh thu)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%,.0f", value);
            }
        });
        lineChart.getAxisRight().setEnabled(false); // Tắt trục Y bên phải

        // Tùy chỉnh chart
        lineChart.getDescription().setEnabled(false); // Tắt description
        lineChart.setDrawGridBackground(false); // Tắt grid background
        lineChart.getLegend().setTextSize(12f);
        lineChart.getLegend().setTextColor(Color.BLACK);
        lineChart.setTouchEnabled(true); // Cho phép tương tác
        lineChart.setPinchZoom(true); // Cho phép zoom

        // Animation
        lineChart.animateXY(1500, 1500, Easing.EaseInOutQuad);

        // Làm mới chart
        lineChart.invalidate();
    }
}