package com.example.yesterday.yesterday.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.example.yesterday.yesterday.server.FoodListServer;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class CalendarActivity extends AppCompatActivity {

    TextView breakfastText ;
    TextView lunchText;
    TextView dinnerText ;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);

        breakfastText = (TextView)findViewById(R.id.breakfastText);
        lunchText = (TextView)findViewById(R.id.lunchText);
        dinnerText = (TextView)findViewById(R.id.dinnerText);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                serverConn(stringToDateFormat(year,month,dayOfMonth));
                stringToJSON(result);
            }
        });
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
