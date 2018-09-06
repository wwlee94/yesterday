package com.example.yesterday.yesterday.UI.HomeViewPager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Chart2Fragment extends Fragment {

    private ViewGroup rootView;

    PieChart pieChart;
    String result;

    private int foodcount = 0;
    private int maxfoodvalue = 0;

    ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

    SharedPreferences loginPre;
    Boolean flag=true;

    public Chart2Fragment() {
        // Required empty public constructor
       // stringToJSON(serverConn());
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPre = getActivity().getSharedPreferences("loginSetting",MODE_PRIVATE);

        if(flag) {
            stringToJSON(serverConn());
            flag = false;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_chart2,container,false);

        pieChart = (PieChart)rootView.findViewById(R.id.piechart);

        // id 값 가져오기
        loginPre = getActivity().getSharedPreferences("loginSetting", 0);

        DrawChart(pieChart);

        // Inflate the layout for this fragment
        return rootView;
    }

    private String serverConn(){
        try {
            result = new BarchartServer(loginPre.getString("ID","")).execute().get();
        } catch (Exception e){
            e.getMessage();
        }

        return result;
    }

    //JSON 형식 String 데이터를 파라미터로 받는다.
    //JSON형식 데이터를 받은 후 파싱 해준 후
    //각각의 데이터를 막댈그래프에 보여주기 위해 값을 입력 해준다.
    private void stringToJSON(String result){
        String food_name = null;
        String food_count = null;

        int count = 0;
        try {
            JSONArray jarray = new JSONObject(result).getJSONArray("data");
            for (int i = jarray.length()-1 ; i >= 0; i--) {
                JSONObject jObject = jarray.getJSONObject(i);
                food_name = jObject.optString("food");
                food_count = jObject.optString("count");

                if(Integer.parseInt(food_count) > maxfoodvalue){
                    maxfoodvalue = Integer.parseInt(food_count);
                }

                if(i < 5) {
                    yValues.add(new PieEntry(Integer.parseInt(food_count), food_name));
                }

                foodcount++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void DrawChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        //사방 여분 오프셋
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(2f);

        //가운데 원형 색상, 크기
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        //pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(30f);

        // enable rotation of the chart by touch
        //pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(false);

        //라벨
        /*
        Description description = new Description();
        description.setText("");
        description.setTextSize(20);
        pieChart.setDescription(description);
        */

        //pieChart.animateY(2000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setSliceSpace(5f);//파이 사이 거리
        dataSet.setSelectionShift(3f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);//숫자(%) 크기
        data.setValueTextColor(Color.BLACK);//숫자(%) 색상
        pieChart.setEntryLabelColor(Color.parseColor("#000000")); //라벨 색상

        pieChart.setData(data);
    }

}
