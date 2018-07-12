package com.example.yesterday.yesterday.UI.HomeFrags;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.yesterday.yesterday.R;

import java.util.Calendar;

//통계 화면 Fragment
public class StatisticsFragment extends Fragment {

    private ViewGroup rootView;

    Button startBtn;
    Button endBtn;
    TextView startDateView;
    TextView endDateView;

    private int startYear;
    private int startMonth;
    private int startDay;

    private int endYear;
    private int endMonth;
    private int endDay;

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

        startBtn = (Button) rootView.findViewById(R.id.button);
        endBtn = (Button) rootView.findViewById(R.id.button2);

        startDateView = (TextView)rootView.findViewById(R.id.textView);
        endDateView = (TextView)rootView.findViewById(R.id.textView2);

        final Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH) -1;
        startDay = c.get(Calendar.DATE);
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DATE);

        final DatePickerDialog startDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startYear = year;
                startMonth = month;
                startDay = dayOfMonth;
                updateDate(startDateView,startYear,startMonth,startDay);
            }
        }, startYear, startMonth, startDay);

        final DatePickerDialog endDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endYear = year;
                endMonth = month;
                endDay = dayOfMonth;
                updateDate(endDateView,endYear,endMonth,endDay);
            }
        }, endYear, endMonth , endDay);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        //통계 플래그 먼트 생성시 초기값 입력 : 시작텍스트에는 한달 전 날짜 값, 종료 텍스트에는 오늘 날짜 값 디폴트 지정
        updateDate(startDateView,startYear,startMonth,startDay);
        updateDate(endDateView,endYear,endMonth,endDay);


        return rootView;
    }

    private void updateDate(TextView view,int Year,int Month,int Day){
        String str = Year +"년"+(Month +1)+"월"+ Day +"일";
        view.setText(str);
    }

}
