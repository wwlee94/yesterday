package com.example.yesterday.yesterday.server;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckTypeServer extends AsyncTask<Void, Void, String> {

    private String userID;
    private String food;
    private int favorite;
    private String type;
    private String changeType;

    private String result;

    public CheckTypeServer(String userID, String food, int favorite, String type,String changeType) {
        this.userID = userID;
        this.food = food;
        this.favorite = favorite;
        this.type = type;
        this.changeType = changeType;
    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Response response;
        RequestBody requestBody = null;

        requestBody = new FormBody.Builder().add("USERID",userID).add("FOOD",food)
                .add("FAVORITE",""+favorite).add("TYPE",type).add("CHANGETYPE",changeType)
                .build();

        Request request = new Request.Builder()
                .url("http://"+Server.WEBIP()+":80/skuniv/changeType")
                .post(requestBody)
                .build();

        try{
            response = client.newCall(request).execute();

            result = response.body().string();

        }catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
