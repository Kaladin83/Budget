package com.example.maratbe.budget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maratbe.budget.Objects.Item;
import com.example.maratbe.budget.Objects.Statistics;
import com.example.maratbe.budget.interfaces.Constants;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Charts class - create charts
 */

public class Charts extends Fragment implements View.OnClickListener, Constants {
    private int chartHeight, radiosHeight;
    private View mainView;
    private ArrayList<Item> listOfNonParents;
    private PieChart pieChart;
    private BarChart barChart;
    private RadioButton categoryRadio, subCategoryRadio;
    private Spinner monthSpinner, categorySpinner;
    private HashMap<String,String> monthsMap = new HashMap<>();
    private String chosenDate, chosenCategory;
    private RelativeLayout pieLayout, barLayout, pieSpinnerLayout, barSpinnerLayout;
    private TextView barTxt, pieTxt;
    private HashMap<Float, Double> mapOfPercents = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.charts, container, false);

        RelativeLayout noDataLayout = mainView.findViewById(R.id.no_data_layout);
        RelativeLayout dataLayout = mainView.findViewById(R.id.data_layout);
        if (MainActivity.getListOfItems().size() > 0)
        {
            populateDataLayout();
            dataLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        }
        else
        {
            noDataLayout.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);
        }

        return mainView;
    }

    private void populateDataLayout() {
        //charts = this;
        MainActivity.getDbInstance().fetchAllMonthsFromStatistics();
        Utils.fillUpMonthMap(monthsMap);
        String month = monthsMap.get(String.valueOf(MainActivity.getCurrentPayDate()).substring(4));
        String year = String.valueOf(MainActivity.getCurrentPayDate()).substring(0,4);
        chosenDate = month + " " + year;
        listOfNonParents = Utils.getItemsOfNonParents();
        pieSpinnerLayout = mainView.findViewById(R.id.pie_spinner_layout);
        barSpinnerLayout = mainView.findViewById(R.id.bar_spinner_layout);
        LinearLayout choiceLayout = mainView.findViewById(R.id.choice_layout);
        choiceLayout.setBackground(Utils.createBorder(5, Color.TRANSPARENT, 2));

        View separator = mainView.findViewById(R.id.separator);
        separator.setBackground(Utils.createBorder(0, Color.TRANSPARENT, 1));

        barTxt = mainView.findViewById(R.id.bar_txt);
        barTxt.setOnClickListener(this);
        pieTxt = mainView.findViewById(R.id.pie_txt);
        pieTxt.setOnClickListener(this);

        categoryRadio = mainView.findViewById(R.id.category_radio);
        categoryRadio.setOnClickListener(this);
        subCategoryRadio = mainView.findViewById(R.id.subcategory_radio);
        subCategoryRadio.setOnClickListener(this);

        calculateDimensions();
        createMonthSpinner();
        createCategorySpinner();
        createPieChart();
        createBarChart();

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(), chartHeight+ radiosHeight);
        rParams.addRule(RelativeLayout.BELOW, R.id.header_layout);
        rParams.setMargins(0,20,0,10);

        pieLayout = mainView.findViewById(R.id.pie_layout);
        barLayout = mainView.findViewById(R.id.bar_layout);
        pieLayout.setLayoutParams(new FrameLayout.LayoutParams(MainActivity.getScreenWidth(), chartHeight+ radiosHeight));
        barLayout.setLayoutParams(new FrameLayout.LayoutParams(MainActivity.getScreenWidth(), chartHeight+ radiosHeight));
        FrameLayout chartsLayout = mainView.findViewById(R.id.charts_layout);
        chartsLayout.setLayoutParams(rParams);
        changeSelection(BLUE_4, View.VISIBLE, View.VISIBLE, Color.WHITE, View.GONE, View.GONE);
    }

    private void calculateDimensions() {
        int chartsScreenHeight = MainActivity.getScreenHeight() - 60* MainActivity.getLogicalDensity();
        int topLayoutHeight = 153 * MainActivity.getLogicalDensity();
        radiosHeight = 40 * MainActivity.getLogicalDensity();
        chartHeight = chartsScreenHeight - topLayoutHeight - radiosHeight;
    }

    private void createMonthSpinner() {
        monthSpinner = mainView.findViewById(R.id.pieSpinner);
        //monthSpinner.setBackground(Utils.createBorder(5, Color.TRANSPARENT, 1));
        // monthSpinner.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.toolBar), PorterDuff.Mode.SRC_ATOP);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //enableFields(false, false, true,true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fillUpMonthSpinner();
    }

    private void fillUpMonthSpinner() {
        String[] arrayOfValues = new String[MainActivity.getListOfMonths().size()];
        for (int i = 0; i < MainActivity.getListOfMonths().size(); i++) {
            int date = MainActivity.getListOfMonths().get(i);
            String month = monthsMap.get(String.valueOf(date).substring(4));
            String year = String.valueOf(date).substring(0,4);
            arrayOfValues[i] = month + " " +year;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arrayOfValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
    }

    private void createCategorySpinner() {
        categorySpinner = mainView.findViewById(R.id.columnSpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCategory = adapterView.getSelectedItem().toString();
                loadBarData();
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fillUpCategorySpinner();
        chosenCategory = categorySpinner.getAdapter().getItem(0).toString();
    }

    private void fillUpCategorySpinner() {
        ArrayList<Item> stats = Utils.getParentStatistics();
        String[] arrayOfValues = new String[stats.size()];
        for (int i = 0; i < stats.size(); i++) {
            arrayOfValues[i] = stats.get(i).getCategory();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arrayOfValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void createPieChart() {
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(), chartHeight);
        rParams.setMargins(10 * MainActivity.getLogicalDensity(), 0, 10 * MainActivity.getLogicalDensity(), 0);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        pieChart = mainView.findViewById(R.id.pie_chart);
        pieChart.setCenterText("Pie charts");
        pieChart.setLayoutParams(rParams);
        pieChart.setCenterTextSize(12);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(50);
        pieChart.setDrawEntryLabels(true);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (categoryRadio.isChecked())
                {
                    displayPieData(e, MainActivity.getListOfParentItems());
                }
                else
                {
                    displayPieData(e, listOfNonParents);
                }
            }

            private void displayPieData(Entry e, ArrayList<Item> list) {
                int index = 0;

                for (int i = 0; i< list.size(); i++)
                {
                    if (list.get(i).getAmount() == mapOfPercents.get(e.getY()))
                    {
                        index = i;
                    }
                }

                String str = list.get(index).getCategory()+": "+ list.get(index).getAmount()+" NIS";
                Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        loadPieData(MainActivity.getListOfParentItems());
        rParams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(), radiosHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.setMargins(0,30,0,0);
        RelativeLayout radioLayout = mainView.findViewById(R.id.radio_group);
        radioLayout.setLayoutParams(rParams);
        radioLayout.setGravity(Gravity.BOTTOM);
    }

    private void loadPieData(ArrayList<Item> items) {
        ArrayList<PieEntry> entryY = new ArrayList<>();
        LegendEntry[] lEntries = new LegendEntry[items.size()];
        int[] arrayOfColors = new int[items.size()];

        getPieDataSet(entryY, lEntries, arrayOfColors, items);
        PieDataSet dataSet = new PieDataSet(entryY, "");
        dataSet.setColors(arrayOfColors);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) ->
                value > 0 ? String.valueOf(value).substring(0, String.valueOf(value).indexOf(".") + 2) + "%" : "");
        dataSet.setSliceSpace(2);
        dataSet.setValueTextSize(13);

        pieChart.getLegend().setCustom(lEntries);
        pieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        pieChart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieChart.getDescription().setText("Expenses for " + chosenDate + " (shown in percents)");
        pieChart.getDescription().setTextSize(12);
        pieChart.getDescription().setYOffset(-7);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void getPieDataSet(ArrayList<PieEntry> entryY, LegendEntry[] lEntries, int[] arrayOfColors, ArrayList<Item> items) {
        double totalSum = 0;
        mapOfPercents = new HashMap<>();
        for (Statistics s : MainActivity.getListOfStatistics()) {
            if (s.getCategory().equals(getString(R.string.total_sum))) {
                totalSum = s.getSum();
                break;
            }
        }

        int i = 0;
        String[] arrayOfCategories = new String[items.size()];
        for (Item item : items) {
            double percent = item.getAmount() / totalSum * 100;
            mapOfPercents.put((float)Math.round(percent), item.getAmount() );
            entryY.add(new PieEntry(Math.round(percent)));
            arrayOfColors[i] = Utils.findColor(item.getCategory());
            arrayOfCategories[i] = item.getCategory();
            LegendEntry entry = new LegendEntry();
            entry.label = arrayOfCategories[i];
            entry.formColor = arrayOfColors[i];
            lEntries[i] = entry;
            i++;
        }
    }

    private void createBarChart() {
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(MainActivity.getScreenWidth(), chartHeight);
        rParams.setMargins(10 * MainActivity.getLogicalDensity(), 0, 10 * MainActivity.getLogicalDensity(), 0);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        barChart = mainView.findViewById(R.id.bar_chart);
        barChart.setDrawValueAboveBar(true);
        barChart.setLayoutParams(rParams);
        barChart.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        barChart.setFitBars(true);

        loadBarData();
    }

    private void loadBarData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, Utils.findSumOfCategory(chosenCategory)));
        barEntries.add(new BarEntry(1,120f));
        barEntries.add(new BarEntry(2,100f));
        barEntries.add(new BarEntry(3,35f));
        barEntries.add(new BarEntry(4,111f));
        barEntries.add(new BarEntry(5,78f));

        final String[] dates = new String[]{"Jul 2018", "Aug 2018", "Sep 2018", "Oct 2018", "Nov 2018", "Dec 2018"};

        BarDataSet dataSet = new BarDataSet(barEntries, "data set 1");
        dataSet.setColor(BLUE_3);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        barData.setValueTextSize(12);

        if (chosenCategory.equals(getString(R.string.total_sum)))
        {
            dataSet.setColors(Utils.getArrayOfCategoryColors());
        }
        else
        {
            int color = Utils.findColor(chosenCategory);
            dataSet.setColor(color);
        }

        barChart.setData(barData);
        barChart.getDescription().setYOffset(-55);
        barChart.getDescription().setXOffset(-25);
        barChart.getDescription().setText("Distribution by dates for "+chosenCategory);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelRotationAngle(45f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisLineWidth(1f);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(dates));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.pie_txt:
                changeSelection(BLUE_4, View.VISIBLE, View.VISIBLE, Color.WHITE, View.GONE, View.GONE); break;
            case R.id.bar_txt:
                changeSelection(Color.WHITE, View.GONE, View.GONE, BLUE_4, View.VISIBLE, View.VISIBLE); break;
            case R.id.category_radio:
                changeSelection(true, false);
                loadPieData(MainActivity.getListOfParentItems()); break;
            case R.id.subcategory_radio:
                changeSelection(false, true);
                loadPieData(listOfNonParents); break;
        }
    }

    private void changeSelection(boolean b, boolean b1) {
        categoryRadio.setChecked(b);
        subCategoryRadio.setChecked(b1);
    }

    private void changeSelection(int color1, int visibleChart1, int visibleSpinner1, int color2, int visibleChart2, int visibleSpinner2) {
        pieLayout.setVisibility(visibleChart1);
        pieSpinnerLayout.setVisibility(visibleSpinner1);
        pieTxt.setBackgroundColor(color1);
        barLayout.setVisibility(visibleChart2);
        barSpinnerLayout.setVisibility(visibleSpinner2);
        barTxt.setBackgroundColor(color2);
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            String month ="";
            if (value >= 0)
            {
                month = mValues[(int) value % mValues.length];
            }
            return month;
        }
    }
}
