package com.example.yesterday.yesterday.UI;

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
import com.example.yesterday.yesterday.server.FoodListServer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        materialCalendarView = (MaterialCalendarView )findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2018, 9, 20))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        String[] results = {"2018,03,18","2018,04,18","2018,05,18","2018,06,18"};

        new ApiSimulator(results).executeOnExecutor(Executors.newSingleThreadExecutor());

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

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }



            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,CalendarActivity.this));
        }
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
