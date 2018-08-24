package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.decorators.EventDecorator;
import com.example.yesterday.yesterday.decorators.OneDayDecorator;
import com.example.yesterday.yesterday.decorators.SaturdayDecorator;
import com.example.yesterday.yesterday.decorators.SundayDecorator;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.example.yesterday.yesterday.server.DateServer;
import com.example.yesterday.yesterday.server.FoodListServer;
import com.example.yesterday.yesterday.server.haveBreakfast;
import com.github.mikephil.charting.data.BarEntry;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarActivity extends AppCompatActivity {

    TextView breakfastText ;
    TextView lunchText;
    TextView dinnerText ;

    MaterialCalendarView materialCalendarView;

    String result;
    String dateResult;

    String foodname;

    ArrayList<CalendarDay> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        materialCalendarView = (MaterialCalendarView )findViewById(R.id.calendarView);

        Intent intent = getIntent();
        foodname = intent.getStringExtra("foodname");

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

        breakfastText = (TextView)findViewById(R.id.breakfastText);
        lunchText = (TextView)findViewById(R.id.lunchText);
        dinnerText = (TextView)findViewById(R.id.dinnerText);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                serverConn(stringToDateFormat(date.getYear(),date.getMonth(),date.getDay()));
                stringToJSON(result);
            }
        });
    }

    private String dateServerConn(){
        try {
            dateResult = new DateServer("kim",foodname).execute().get();
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
        materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, dates,CalendarActivity.this));
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
