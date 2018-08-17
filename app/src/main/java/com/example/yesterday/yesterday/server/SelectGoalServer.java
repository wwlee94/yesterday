package com.example.yesterday.yesterday.server;

import android.os.AsyncTask;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//로그인 서버와 연결하는 클래스
public class SelectGoalServer extends AsyncTask<Void,Void,String> {

    private String userID;

    private String result;
    //owl wifi 로컬 -> 192.168.0.75
    //조교서버 -> 117.17.142.207
    private static final String  WEBIP = "192.168.0.75";

    //addGoalActivity에서 등록한 정보 생성자로 받는다.
    public SelectGoalServer(String userID) {
        this.userID = userID;
    }

    @Override
    protected String doInBackground(Void... params) {
        //request 를 보내줄 클라이언트 생성   (okhttp 라이브러리 사용)
        OkHttpClient client = new OkHttpClient();
        Response response;
        RequestBody requestBody = null;

        //보낼 데이터를 파라미터 형식으로 body에 넣음
        requestBody = new FormBody.Builder().add("USERID",userID).build();

        // post형식으로 url로 만든 body를 보냄
        Request request = new Request.Builder()
                .url("http://"+ WEBIP + ":80/skuniv/selectGoal")
                .post(requestBody)
                .build();
        try {
            response = client.newCall(request).execute();
            /////////////////////////////////// newcall 하고 응답받기를 기다리는중


            //서버로 부터 받은 json타입 문자열을 Object로 파싱 후 반환 !!

            //서버로 부터 온 답을 반환
            result = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}