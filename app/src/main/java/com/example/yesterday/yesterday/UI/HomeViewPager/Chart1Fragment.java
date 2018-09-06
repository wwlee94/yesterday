package com.example.yesterday.yesterday.UI.HomeViewPager;

import android.content.Context;
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
import com.example.yesterday.yesterday.UI.HomeActivity;
import com.example.yesterday.yesterday.UI.LoginActivity;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Chart1Fragment extends Fragment {

    private ViewGroup rootView;

    HorizontalBarChart horizontalBarChart;

    String result;

    private int foodcount = 0;
    private int maxfoodvalue = 0;

    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();

    SharedPreferences loginPre;

    Boolean flag = true;

    public Chart1Fragment() {
        // Required empty public constructor
        //서버와 연동해 데이터를 JSON 형태로 불러온다.
        //stringToJSON(serverConn());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPre = getActivity().getSharedPreferences("loginSetting", MODE_PRIVATE);

        if (flag) {
            stringToJSON(serverConn());
            flag = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chart1, container, false);

        //막대바 차트 생성
        horizontalBarChart = (HorizontalBarChart) rootView.findViewById(R.id.fixedchart);

        //차트 그리기
        DrawChart(horizontalBarChart);


        // Inflate the layout for this fragment
        return rootView;
    }

    //DB연동을 위해 서버와 연결 접속을 시도한다.
    //파라미터로 로그인한 사용자 아이디, 시작 날짜 , 종료 날짜를 입력 받는다.
    //서버에서 db 쿼리를 보낸후 받은 값을 JSON String 값으로 받아 return한다.
    //(ex [{count=5, food=커피}, {count=4, food=초밥}, {count=3, food=술}, {count=2, food=김치찌개}] )
    private String serverConn() {
        // id 값 가져오기

        try {
            result = new BarchartServer(loginPre.getString("ID", "")).execute().get();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    //JSON 형식 String 데이터를 파라미터로 받는다.
    //JSON형식 데이터를 받은 후 파싱 해준 후
    //각각의 데이터를 막댈그래프에 보여주기 위해 값을 입력 해준다.
    private void stringToJSON(String result) {
        String food_name = null;
        String food_count = null;
        try {

            JSONArray jarray = new JSONObject(result).getJSONArray("data");

            foodcount = 0;
            labels.clear();

            for (int i = jarray.length() - 1; i >= 0; i--) {
                JSONObject jObject = jarray.getJSONObject(i);

                food_name = jObject.optString("food");
                food_count = jObject.optString("count");

                if (Integer.parseInt(food_count) > maxfoodvalue) {
                    maxfoodvalue = Integer.parseInt(food_count);
                }

                if (i < 5) {
                    labels.add(food_name);
                    entries.add(new BarEntry((4 - i), Integer.parseInt(food_count)));

                    foodcount++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // horizontalBarChart에 레이블 추가 , 디자인, 값 입력 , y축 최대 최소값 등등 .
    private void DrawChart(HorizontalBarChart horizontalBarChart) {

        BarDataSet dataset = new BarDataSet(entries, "# of Colls");
        dataset.setDrawValues(true);

        BarData data = new BarData(dataset);

        //Design 막대 랜덤 색상 지정
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        //각각 막대 값을 가리키는 숫자의 textsize 지정
        data.setValueTextSize(10f);
        //각각 막대 값을 가리키는 숫자의 color 지정
        data.setValueTextColor(Color.DKGRAY);

        //바 width 크기 지정
        data.setBarWidth(0.85f);

        horizontalBarChart.setData(data);

        //라벨 갯수 지정 !!!!!!!!!!!!!!!!!!
        horizontalBarChart.getXAxis().setLabelCount(foodcount);
        horizontalBarChart.getXAxis().setTextColor(Color.BLACK);

        YAxis y = horizontalBarChart.getAxisLeft();

        //y축 최대 최소값 지정 !
        y.setAxisMaxValue(maxfoodvalue + 1);
        y.setAxisMinValue(0);

        //차트 위쪽의 grid lines, 음식 개수 true:visible false:invisible
        horizontalBarChart.getAxisLeft().setEnabled(true);
        //차트 아래쪽의 grid lines true:visible false:invisible
        horizontalBarChart.getAxisRight().setEnabled(true);
        // Hide graph description
        horizontalBarChart.getDescription().setEnabled(false);
        // Hide graph legend(범례)
        horizontalBarChart.getLegend().setEnabled(false);

        //그래프 가로축 가이드라인 false
        horizontalBarChart.getXAxis().setDrawGridLines(false);
        //위에서 지정한 라벨값(음식이름)을 그래프에 지정
        horizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        //라벨을 그려준다. true
        horizontalBarChart.getXAxis().setDrawLabels(true);
        //라벨 포지션 왼쪽으로 이동
        horizontalBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        //라벨이 화면 밖으로 나가지 않게 전체적으로 오른쪽으로 이동
        horizontalBarChart.getXAxis().setXOffset(-10);

        //라벨값이 들어갈 수 있게 도표 위치 살짝 오른쪽으로 이동
        horizontalBarChart.setExtraOffsets(50, 0, 0, 0);

        //깔끔한 design 을 위해 지워줍니다.
        horizontalBarChart.getXAxis().setDrawAxisLine(false);
        //아래 gideline 숫자값 제거 .(위 gideline 과 중복 )
        //horizontalBarChart.getAxisRight().setDrawLabels(false);

        horizontalBarChart.setTouchEnabled(false);
        horizontalBarChart.setDragEnabled(false);// : 차트의 끌기 (이동)를 활성화 / 비활성화합니다.
        horizontalBarChart.setScaleEnabled(false); // : 두 축의 차트에 대한 배율을 설정 / 해제합니다.
        horizontalBarChart.setScaleXEnabled(false); // : x 축에서 크기 조절을 활성화 / 비활성화합니다.
        horizontalBarChart.setScaleYEnabled(false); // : Y 축에서 크기 조절을 활성화 / 비활성화합니다.

        horizontalBarChart.getAxisRight().setDrawGridLines(false);


    }
}
