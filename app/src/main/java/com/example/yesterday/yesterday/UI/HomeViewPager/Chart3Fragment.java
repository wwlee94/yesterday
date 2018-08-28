package com.example.yesterday.yesterday.UI.HomeViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.UI.CalendarActivity;
import com.example.yesterday.yesterday.UI.HomeActivity;
import com.example.yesterday.yesterday.decorators.EventDecorator;
import com.example.yesterday.yesterday.decorators.OneDayDecorator;
import com.example.yesterday.yesterday.decorators.SaturdayDecorator;
import com.example.yesterday.yesterday.decorators.SundayDecorator;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.example.yesterday.yesterday.server.haveBreakfast;
import com.github.mikephil.charting.data.BarEntry;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Chart3Fragment extends Fragment {

    String result;

    private ViewGroup rootView;
    ArrayList<CalendarDay> dates = new ArrayList<>();

    MaterialCalendarView materialCalendarView;

    public Chart3Fragment() {
        // Required empty public constructor
        serverConn();
        stringToJSON(result);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_chart3,container,false);

        materialCalendarView = (MaterialCalendarView)rootView.findViewById(R.id.calendarView);

        DrawCalendar();

        return rootView;
    }

    private String serverConn(){
        try {
            result = new haveBreakfast("kim").execute().get();
        } catch (Exception e){
            e.getMessage();
        }

        return result;
    }

    private void stringToJSON(String result){
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
    }

    private void DrawCalendar() {
        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.from(CalendarDay.today().getYear(), CalendarDay.today().getMonth()-1, 1))
                .setMaximumDate(CalendarDay.today())
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, dates,getActivity()));

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());
    }
}
