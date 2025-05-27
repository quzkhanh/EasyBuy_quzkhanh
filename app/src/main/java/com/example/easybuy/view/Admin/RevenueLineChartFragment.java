package com.example.easybuy.view.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.easybuy.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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
        float[] revenues = chartManager.getMonthlyRevenues();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new Entry(i + 1, revenues[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%,.0f VNĐ", value);
            }
        });
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Tháng 1-12
            }
        });
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }
}