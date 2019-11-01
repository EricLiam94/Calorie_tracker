package com.example.calorie_tracker.Navi_Control;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.RestClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.example.calorie_tracker.R;
import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChartFragment extends Fragment {
    View parentV;
    PieChart pieChart;
    List<PieEntry> entries;
    List<String> PieEntryLabels;
    PieDataSet pieDataSet;
    PieData pieData;
    View calendarView;
    Button confirm;
    Users user;
    BarChart barChart;
    Calendar previous;
    TextView t;
    SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
    private CalendarPickerView calendar;
    private String[] xValues = {"Data 1", "Data 2", "Data 3"};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        parentV = inflater.inflate(R.layout.fragment_chart, container, false);
        pieChart = (PieChart) parentV.findViewById(R.id.piechart);
        calendarView = parentV.findViewById(R.id.calendar_view);
        confirm = parentV.findViewById(R.id.dateConfirm);
        barChart = parentV.findViewById(R.id.barChart);
        Intent i = getActivity().getIntent();
        t = parentV.findViewById(R.id.errorIndicator);
        t.setText("Please Select Report type");
        user = i.getParcelableExtra("user");
        previous = Calendar.getInstance();
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(3000);
        previous.set(2019, 1, 1);
        GetMinDate gmd = new GetMinDate();
        gmd.execute(user.getId());
        calendar = (CalendarPickerView) parentV.findViewById(R.id.calendar_view);
        setVisible(false);
        final Date today = new Date();
        final RadioGroup rg = parentV.findViewById(R.id.buttonGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.piechartOption) {
                    barChart.setVisibility(View.GONE);
                    calendar.init(previous.getTime(), today).
                            inMode(CalendarPickerView.SelectionMode.SINGLE);
                    setVisible(true);
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Date d = calendar.getSelectedDate();
                            if (d == null)
                                t.setText("Empty input");
                            else {
                                t.setText("");
                                Log.e("date", d.toString());
                                setVisible(false);
                                GetOneRequest gor = new GetOneRequest();
                                gor.execute(d);
                            }
                        }
                    });
                }
                else {
                    pieChart.setVisibility(View.GONE);
                    calendar.init(previous.getTime(), today).
                            inMode(CalendarPickerView.SelectionMode.RANGE);
                    setVisible(true);
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<Date> dates = calendar.getSelectedDates();
                            Log.e("dates", String.valueOf(dates.size()));
                            if (dates.size() <= 1)
                                t.setText("Invalid input");
                            else {
                                t.setText("");
                                Date[] input = {dates.get(0), dates.get(dates.size() - 1)};
                                GetReports gr = new GetReports();
                                gr.execute(input);
                                setVisible(false);
                            }
                        }
                    });

                }
            }

        });
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterText("Report");


        return parentV;
    }


    public void setVisible(boolean t) {
        if (t) {
            calendar.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
        } else {
            calendarView.setVisibility(View.GONE);
            calendar.setVisibility(View.GONE);
            confirm.setVisibility(View.GONE);
        }
    }

    private class GetReports extends AsyncTask<Date, Void, String> {
        @Override
        protected String doInBackground(Date... dates) {
            String start = sm.format(dates[0]);
            String end = sm.format(dates[1]);

            String input = user.getId() + "/" + start + "/" + end;
            String result = RestClient.getRest("reports/dateRange/", input);
            return result;
        }


        @Override
        protected void onPostExecute(String s) {
            HashMap<String, List<Double>> hs = new HashMap<>();
            Log.e("reports", s);
            JSONArray jarray = null;
            try {
                jarray = new JSONArray(s);
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject temp = (JSONObject) jarray.get(i);
                    String d = temp.getJSONObject("reportsPK").getString("reportDate").split("T")[0];
                    hs.put(d, new ArrayList<Double>());
                    hs.get(d).add((double) temp.getDouble("calorieConsumed"));
                    hs.get(d).add((temp.getDouble("calorieBurned")));
                }
                Log.e("key", hs.keySet().toString());
                drawChart(hs);
                barChart.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetOneRequest extends AsyncTask<Date, Void, String> {
        @Override
        protected String doInBackground(Date... dates) {
            String temp = sm.format(dates[0]);
            Description d = new Description();
            d.setText("Reports in " + temp);
            d.setTextSize(20f);
            pieChart.setDescription(d);
            String result = RestClient.getRest("reports/Report/", user.getId() + "/" + temp);
            Log.e("Result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            entries.clear();
            Log.e("xxzz", s);
            if (s.length() == 2) {
                t.setText("no record that day");
            } else {
                JSONArray j = new JSONArray();
                try {
                    j = new JSONArray(s);
                    JSONObject temp = (JSONObject) j.get(0);
                    double consumed = (double) temp.get("CalorieConsumed");
                    entries.add(new PieEntry((float) consumed, "Consumed Calorie"));
                    double burned = (double) temp.get("CalorieBurned");
                    entries.add(new PieEntry((float) burned, "Burned Calorie"));
                    double remaining = ((double) temp.get("remaining_Calorie"));
                    String label = remaining >= 0 ? "Calorie surplus" : "Calorie Deficit";
                    entries.add(new PieEntry((float) Math.abs(remaining), label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("size", String.valueOf(entries.size()));
                pieDataSet = new PieDataSet(entries, "");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.animateY(3000);
                pieChart.setVisibility(View.VISIBLE);
            }
        }
    }

    private void drawChart(HashMap<String, List<Double>> hash) {
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        barChart.getAxisRight().setEnabled(false);

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f;
        float barWidth = 0.26f;
        barWidth = 0.3f;
        barSpace = 0f;
        groupSpace = 0.4f;

        final List<String> xValue = new ArrayList<>();
        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<BarEntry> yVals2 = new ArrayList<BarEntry>();
        int i = 1;
        for (String d : hash.keySet()) {
            xValue.add(d);
            yVals1.add(new BarEntry(i, (float) hash.get(d).get(0).doubleValue()));
            yVals2.add(new BarEntry(i, (float) hash.get(d).get(1).doubleValue()));
            i++;
        }
        Log.e("test", xValue.get(0));
        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Consumed");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(yVals2, "Burned");
            set2.setColor(Color.rgb(164, 228, 251));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0);
        xAxis.setTextSize(12);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(hash.keySet().size());
        xAxis.setLabelRotationAngle(90f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValue));
        barChart.getBarData().setBarWidth(barWidth);
        barChart.setFitBars(true);
        barChart.animateXY(1000, 2000);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate();
    }

    private class GetMinDate extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            String url = "reports/minD/" + String.valueOf(user.getId());
            String date = "";
            try {
                date = RestClient.scrape(url);
            } catch (Exception e) {
            }

            Log.e("date", date);
            if (date.trim().length() != 0) {
                String[] in = date.trim().split("-");
                int year = Integer.parseInt(in[0]);
                int month = Integer.parseInt(in[1]) - 1;
                int day = Integer.parseInt(in[2]);
                previous.set(year, month, day);

            }
            return date;
        }
    }
}
