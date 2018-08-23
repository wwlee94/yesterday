package com.example.yesterday.yesterday.server;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//로그인 서버와 연결하는 클래스
public class haveBreakfast extends AsyncTask<Void,Void,String> {
    String parent_id;
    String startDate;
    String endDate;
    String answer;
    JSONArray JArray;

    private int startYear;
    private int startMonth;
    //private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;

    private static final String  WEBIP = "192.168.0.4";

    public haveBreakfast(String parent_id ) {
        final Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH) -1;
        //startDay = c.get(Calendar.DATE);
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DATE);
        stringToDateFormat(startYear,startMonth,1,true);
        stringToDateFormat(endYear,endMonth,endDay,false);
        this.parent_id = parent_id;
    }

    public haveBreakfast(String parent_id, String startDate, String endDate ) { //로그인 id 받기
        this.startDate = startDate;
        this.endDate = endDate;
        this.parent_id = parent_id;
    }

    @Override
    protected String doInBackground(Void... params) {
        //request 를 보내줄 클라이언트 생성   (okhttp 라이브러리 사용)
        OkHttpClient client = new OkHttpClient();
        Response response;
        RequestBody requestBody = null;

        //보낼 데이터를 파라미터 형식으로 body에 넣음
        requestBody = new FormBody.Builder().add("parent_id",parent_id).add("startDate",startDate).add("endDate",endDate).build();

        // post형식으로 url로 만든 body를 보냄
        Request request = new Request.Builder()
                .url("http://"+ WEBIP + ":80/skuniv/haveBreakfast")
                .post(requestBody)
                .build();
        try {
            response = client.newCall(request).execute();
            /////////////////////////////////// newcall 하고 응답받기를 기다리는중

            //제이슨 값 받기
            answer = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    //int 형 년월일 을 날짜 데이터 포멧으로 변경한다.
    private void stringToDateFormat(int Year,int Month,int Day ,boolean flag){
        String strDate = Year+ "-"+(Month+1) +"-"+Day+" 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //startday
        if(flag){
            startDate  = strDate;
        }
        else{
            endDate = strDate;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
