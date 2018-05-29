package com.example.yesterday.yesterday;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import org.apache.http.*;


import com.example.yesterday.yesterday.DBConnector.Connector;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Login extends AppCompatActivity {
    EditText ed_id, ed_pw;
    Button btn_login;
    TextView jsonText;
    String sId, sPw;
    ClientLoginInfo client;
    private ProgressDialog pDialog;
    String LoadData;
    private static final String URL_ADDRESS = "http://192.168.0.72:8080/adConnector/Connector";
//    JSONObject jsonObject;
//    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_id = (EditText) findViewById(R.id.IDText);
        ed_pw = (EditText) findViewById(R.id.PassText);
        btn_login = (Button) findViewById(R.id.loginBtn);
        jsonText = (TextView) findViewById(R.id.jsontext);

        sId = ed_id.getText().toString();   // id
        sPw = ed_pw.getText().toString();   // password


        btn_login.setOnClickListener(new View.OnClickListener() {  // 로그인 버튼 리스너
            @Override
            public void onClick(View v) {
                client = new ClientLoginInfo(sId,sPw); // 로그인 클라이언트 객체 생성
                String json = new Gson().toJson(client);
                //new Connector().execute();
                getList a = new getList(json);
                a.execute();
                Log.i("info","login");
            }
        });
    }

    class getList extends AsyncTask<Void,String,Void>{
        String json;
        public  getList(String json){
            this.json = json;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                URL Url = new URL(URL_ADDRESS); //url화
                HttpURLConnection conn = (HttpURLConnection) Url.openConnection(); //url을 연결한 객체 생성
                conn.setRequestMethod("POST"); // post방식 설정
                conn.setDoOutput(true);  //쓰기모드
                conn.setDoInput(true);  // 읽기모드
                conn.setUseCaches(false);  //캐싱데이터 수신 여부
                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

                InputStream is = conn.getInputStream();  // input stream 개방
                OutputStream os = conn.getOutputStream(); // outputstream 개방

                //os.write(json);
               // os.flush();

                StringBuffer builder = new StringBuffer(); //문자열을 담기 위한 객체
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));  // 문자열 셋팅
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line+"\n");
                    Log.i("LoadData","ddddddd");
                }

                //builder.append(json);  // 빌더에 json 추가

                LoadData = builder.toString();
                jsonText.setText(LoadData);
                Log.i("LoadData",LoadData);

            }catch(MalformedURLException | ProtocolException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //jsonObject = this.jsonObject;
//            pDialog = new ProgressDialog(Login.this);
//            pDialog.setMessage("검색중입니다...");
//            pDialog.setCancelable(true);
//            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //pDialog.dismiss();
            super.onPostExecute(aVoid);

        }
    }
}
