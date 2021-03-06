package com.example.yesterday.yesterday.server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//로그인 서버와 연결하는 클래스
public class ClientUpdateServer extends AsyncTask<Void,Void,String> {
    private String parent_id;
    private String updateText;
    private String answer;
    private String kind;
    //owl wifi 로컬 -> 192.168.0.75
    //조교서버 -> 117.17.142.207
    private static final String  WEBIP = "117.17.142.207";

    public ClientUpdateServer(String parent_id,String updateText,String kind) { //로그인 id, pw 받기
        this.parent_id = parent_id;
        this.updateText = updateText;
        this.kind = kind;
        Log.i("kind","=========="+kind + "====="+parent_id);
    }

    @Override
    protected String doInBackground(Void... params) {
        //request 를 보내줄 클라이언트 생성   (okhttp 라이브러리 사용)
        OkHttpClient client = new OkHttpClient();
        Response response;
        RequestBody requestBody = null;

        //보낼 데이터를 파라미터 형식으로 body에 넣음
        requestBody = new FormBody.Builder().add("parent_id",parent_id).add("updateText",updateText).add("kind",kind).build();

        // post형식으로 url로 만든 body를 보냄
        Request request = new Request.Builder()
                .url("http://"+ WEBIP + ":80/skuniv/infoUpdate")
                .post(requestBody)
                .build();
        try {
            response = client.newCall(request).execute();
            /////////////////////////////////// newcall 하고 응답받기를 기다리는중
            answer = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
