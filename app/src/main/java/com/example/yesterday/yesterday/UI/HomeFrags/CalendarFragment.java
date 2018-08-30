package com.example.yesterday.yesterday.UI.HomeFrags;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.UI.CalendarActivity;
import com.example.yesterday.yesterday.UI.HomeActivity;
import com.example.yesterday.yesterday.decorators.EventDecorator;
import com.example.yesterday.yesterday.decorators.OneDayDecorator;
import com.example.yesterday.yesterday.decorators.SaturdayDecorator;
import com.example.yesterday.yesterday.decorators.SundayDecorator;
import com.example.yesterday.yesterday.server.DateServer;
import com.example.yesterday.yesterday.server.FoodListServer;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class CalendarFragment extends Fragment {

    ViewGroup root;
    private FragmentManager fragmentManager;

    private AddFragment addFragment;

    private Button button;

    TextView breakfastText ;
    TextView lunchText;
    TextView dinnerText ;

    MaterialCalendarView materialCalendarView;

    String result;
    String dateResult;

    String foodname;

    SharedPreferences loginPre;

    ArrayList<CalendarDay> dates = new ArrayList<>();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPre = getActivity().getSharedPreferences("loginSetting",MODE_PRIVATE);
        Log.i("loginPre",loginPre.getString("ID",""));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        materialCalendarView = (MaterialCalendarView )root.findViewById(R.id.calendar_calendarView);

        /*
        Intent intent = getIntent();
        foodname = intent.getStringExtra("foodname");
        */

        Bundle bundle = getArguments();

        if(bundle!=null) {
            foodname = bundle.getString("foodname");
            //bundle.remove("foodname");
            bundle.clear();
        }

        if(foodname != null) {
            dateServerConn();
            dateToResults(dateResult);
        }

        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2018, 9, 20))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        breakfastText = (TextView)root.findViewById(R.id.calendar_breakfastText);
        lunchText = (TextView)root.findViewById(R.id.calendar_lunchText);
        dinnerText = (TextView)root.findViewById(R.id.calendar_dinnerText);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                serverConn(stringToDateFormat(date.getYear(),date.getMonth(),date.getDay()));
                stringToJSON(result);
            }
        });

        fragmentManager = this.getFragmentManager();
        addFragment = ((HomeActivity)getActivity()).getAddFragment();

        //수정 버튼
        button = root.findViewById(R.id.calendar_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"버튼 클릭",Toast.LENGTH_SHORT).show();
                ((HomeActivity)getActivity()).bottomBar.selectTabAtPosition(1);
            }
        });
        return root;
    }
    private String dateServerConn(){
        try {
            dateResult = new DateServer(loginPre.getString("ID",""),foodname).execute().get();
        } catch (Exception e){
            e.getMessage();
        }

        return dateResult;
    }

    private void dateToResults(String result){
        String date = null;
        Calendar calendar = Calendar.getInstance();

        try {
            JSONArray jarray = new JSONObject(result).getJSONArray("data");
            for (int i = 0 ; i <= jarray.length()-1 ; i++) {
                JSONObject jObject = jarray.getJSONObject(i);
                date = jObject.optString("date");

                CalendarDay day;
                String[] time = date.split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2].split(" ")[0]);

                calendar.set(year,month-1,dayy);
                day = CalendarDay.from(calendar);
                dates.add(day);

                Log.d("TAG","JSON test :"+year+";"+month+";"+dayy);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, dates,getActivity()));
    }

    //int 형 년월일 을 날짜 데이터 포멧으로 변경한다.
    private String stringToDateFormat(int Year,int Month,int Day){
        String date = Year+ "-"+(Month+1) +"-"+Day+" 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return date;
    }

    private String serverConn(String Date){
        try {
            result = new FoodListServer("kim",Date).execute().get();
        } catch (Exception e){
            e.getMessage();
        }
        return result;
    }

    //JSON 형식 String 데이터를 파라미터로 받는다.
    //JSON형식 데이터를 받은 후 파싱 해준 후
    //각각의 데이터를 막댈그래프에 보여주기 위해 값을 입력 해준다.
    private void stringToJSON(String result){
        String foodName = null;
        String foodTime = null;

        try {
            JSONArray jarray = new JSONObject(result).getJSONArray("data");
            breakfastText.setText("");
            lunchText.setText("");
            dinnerText.setText("");
            for (int i = jarray.length()-1 ; i >= 0; i--) {
                JSONObject jObject = jarray.getJSONObject(i);
                foodName = jObject.optString("food");
                foodTime = jObject.optString("foodTime");

                if(foodTime.equals("B")){
                    breakfastText.setText(breakfastText.getText()+" "+foodName);
                }else if(foodTime.equals("L")){
                    lunchText.setText(lunchText.getText()+" "+foodName);
                }else if(foodTime.equals("D")){
                    dinnerText.setText(dinnerText.getText()+" "+foodName);
                }else if(foodTime.equals("B")){

                }
                Log.d("TAG","Statics Fragment : JSON test :"+foodName +" : "+ foodTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
