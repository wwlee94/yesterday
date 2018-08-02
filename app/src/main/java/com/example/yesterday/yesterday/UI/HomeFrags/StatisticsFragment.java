package com.example.yesterday.yesterday.UI.HomeFrags;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.example.yesterday.yesterday.server.LoginServer;
import com.github.mikephil.charting.charts.Chart;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//통계 화면 Fragment
public class StatisticsFragment extends Fragment {

    private ViewGroup rootView;

    String result;

    Button startBtn;
    Button endBtn;
    TextView startDateView;
    TextView endDateView;

    // 각각 시작 종료  두가지의 날짜(년,월,일)값을 저장 해두고, 통계를 db값에서 불러온다.
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;

    String startDate;
    String endDate;

    private int foodcount = 0;
    private int maxfoodvalue = 0;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    //생성자와 onCreateView만 있어도 ok
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_statistics,container,false);
        // Inflate the layout for this fragment

        //막대바 차트 생성
        final HorizontalBarChart horizontalBarChart = (HorizontalBarChart)rootView.findViewById(R.id.chart);

        //시작 종료 날짜를 설정 할 수 있는 버튼
        startBtn = (Button) rootView.findViewById(R.id.button);
        endBtn = (Button) rootView.findViewById(R.id.button2);

        //시작 종료 날짜 보여주는 텍스트
        startDateView = (TextView)rootView.findViewById(R.id.textView);
        endDateView = (TextView)rootView.findViewById(R.id.textView2);


        // 각각 시작 종료  두가지의 날짜(년,월,일)값을 저장 해두고, 통계를 db값에서 불러온다.
        final Calendar c = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH) -1;
        startDay = c.get(Calendar.DATE);
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DATE);

        //시작날짜를 입력 하기위한 버튼 이벤트
        startDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if(year <= endYear ){
                            startYear = year;
                            startMonth = month;
                            startDay = dayOfMonth;
                            updateDate(startDateView,startYear,startMonth,startDay);
                    stringToDateFormat(startYear,startMonth,startDay,true);
                    renewChart(horizontalBarChart);
                }else{
                    Toast.makeText(getActivity(),"종료날짜 이전의 날짜를 선택해 주세요",Toast.LENGTH_LONG).show();
                }
            }
        }, startYear, startMonth, startDay);

        //종료날짜를 입력 하기위한 버튼 이벤트
        endDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(year >= startYear && month >= startMonth && dayOfMonth >= startDay){
                    endYear = year;
                    endMonth = month;
                    endDay = dayOfMonth;
                    updateDate(endDateView,endYear,endMonth,endDay);
                    stringToDateFormat(endYear,endMonth,endDay,false);
                    renewChart(horizontalBarChart);
                }else{
                    Toast.makeText(getActivity(),"시작 날짜 이후의 날짜를 선택해 주세요",Toast.LENGTH_LONG).show();
                }
            }
        }, endYear, endMonth , endDay);

        //startDatePicker의 날짜 지정 범위 제한.
        // 최소 : 최초 데이터 입력 날짜
        // 최대 : 오늘 날짜 & endDate 보다 작은 날짜
        minDate.set(2018,6-1,10);
        startDatePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());
        startDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime().getTime());
        //startDatePicker의 날짜 지정 범위 제한.
        // 최소 : 최초 데이터 입력 날짜 & startDate 보다 큰 날짜
        // 최대 : 오늘 날짜
        minDate.set(startYear,startMonth,startDay);
        endDatePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());
        endDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime().getTime());

        //시작버튼을 누르면 날짜 설정 다이어로그를 띄어준다.
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });
        //종료버튼을 누르면 날짜 설정 다이어로그를 띄어준다.
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        //통계 플래그 먼트 생성시 초기값 입력 : 시작텍스트에는 한달 전 날짜 값, 종료 텍스트에는 오늘 날짜 값 디폴트 지정
        updateDate(startDateView,startYear,startMonth,startDay);
        updateDate(endDateView,endYear,endMonth,endDay);

        stringToDateFormat(startYear,startMonth,startDay,true);
        stringToDateFormat(endYear,endMonth,endDay,false);

        //서버와 연동해 데이터를 JSON 형태로 불러온다.
        stringToJSON(serverConn(startDate,endDate));

        //차트 그리기
        DrawChart(horizontalBarChart);

        return rootView;
    }

    //시작, 종료 날짜 텍스트 값 update 메소드
    private void updateDate(TextView view,int Year,int Month,int Day){
        String str = Year +"년"+(Month +1)+"월"+ Day +"일";
        view.setText(str);
    }

    //int 형 년월일 을 날짜 데이터 포멧으로 변경한다.
    private void stringToDateFormat(int Year,int Month,int Day ,boolean flag){
        String strDate = Year+ "-"+(Month+1) +"-"+Day+" 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //startday
        if(flag){
            //try {
                startDate  = strDate;
            //}catch(ParseException e) {            }
        }
        //endday
        else{
            //try {
                endDate = strDate;
            //}catch(ParseException e) {            }
        }
    }

    //DB연동을 위해 서버와 연결 접속을 시도한다.
    //파라미터로 로그인한 사용자 아이디, 시작 날짜 , 종료 날짜를 입력 받는다.
    //서버에서 db 쿼리를 보낸후 받은 값을 JSON String 값으로 받아 return한다.
    //(ex [{count=5, food=커피}, {count=4, food=초밥}, {count=3, food=술}, {count=2, food=김치찌개}] )
    private String serverConn(String startDate,String endDate){
        try {
            result = new BarchartServer("kim",startDate,endDate).execute().get();
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
        float spaceforBar = 1f;
        try {
            JSONArray jarray = new JSONObject(result).getJSONArray("data");
            for (int i = jarray.length()-1 ; i >= 0; i--) {
                JSONObject jObject = jarray.getJSONObject(i);
                food_name = jObject.optString("food");
                food_count = jObject.optString("count");

                if(Integer.parseInt(food_count) > maxfoodvalue){
                    maxfoodvalue = Integer.parseInt(food_count);
                }

                labels.add(food_name);
                entries.add(new BarEntry((jarray.length()-1-i)*spaceforBar, Integer.parseInt(food_count)));

               Log.d("TAG","Statics Fragment : JSON test :"+food_name +" : "+ food_count);
               foodcount++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renewChart(HorizontalBarChart horizontalBarChart){
        labels.clear();
        Log.d("TAG","Statics Fragment "+ labels);
        entries.clear();

        foodcount = 0;
        maxfoodvalue = 0;

        stringToJSON(serverConn(startDate,endDate));

        //차트 그리기
        DrawChart(horizontalBarChart);
    }

    // horizontalBarChart에 레이블 추가 , 디자인, 값 입력 , y축 최대 최소값 등등 .
    private void DrawChart(HorizontalBarChart horizontalBarChart){

        BarDataSet dataset = new BarDataSet(entries, "# of Colls");
        dataset.setDrawValues(true);

        BarData data = new BarData(dataset);



        //Design 막대 랜덤 색상 지정
        dataset.setColors(ColorTemplate.VORDIPLOM_COLORS);

        //각각 막대 값을 가리키는 숫자의 textsize 지정
        data.setValueTextSize(10f);
        //각각 막대 값을 가리키는 숫자의 color 지정
        data.setValueTextColor(Color.DKGRAY);

        //바 width 크기 지정
        data.setBarWidth(0.85f);

        horizontalBarChart.setData(data);

        //라벨 갯수 지정 !!!!!!!!!!!!!!!!!!
        horizontalBarChart.getXAxis().setLabelCount(foodcount);
        horizontalBarChart.getXAxis().setTextColor(Color.BLUE);

        YAxis y = horizontalBarChart.getAxisLeft ();

        //y축 최대 최소값 지정 !
        y.setAxisMaxValue(maxfoodvalue);
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
        horizontalBarChart.setExtraOffsets(50,0,0,0);

        //깔끔한 design 을 위해 지워줍니다.
        horizontalBarChart.getXAxis().setDrawAxisLine(false);
        //아래 gideline 숫자값 제거 .(위 gideline 과 중복 )
        horizontalBarChart.getAxisRight().setDrawLabels(false);

        //잘 모르게떰..ㅋㅋ
        //horizontalBarChart.getXAxis().setAxisLineWidth(10);

        //2개인 세로축을 따로따로 지워 줘야함 .
        //horizontalBarChart.getAxisLeft().setDrawGridLines(false);
        horizontalBarChart.getAxisRight().setDrawGridLines(false);

        //막대 애니메이션 속도(숫자 클수록 느리게)
        horizontalBarChart.animateY(2000);
    }

}
