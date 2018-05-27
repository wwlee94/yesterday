package com.example.yesterday.yesterday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONObject;

public class Login extends AppCompatActivity {
    EditText ed_id, ed_pw;
    Button btn_login;
    String sId, sPw;
    ClientLoginInfo client;
//    JSONObject jsonObject;
//    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_id = (EditText) findViewById(R.id.IDText);
        ed_pw = (EditText) findViewById(R.id.PassText);
        btn_login = (Button) findViewById(R.id.loginBtn);

        sId = ed_id.getText().toString();   // id
        sPw = ed_pw.getText().toString();   // password

        btn_login.setOnClickListener(new View.OnClickListener() {  // 로그인 버튼 리스너
            @Override
            public void onClick(View v) {
                client = new ClientLoginInfo(sId,sPw); // 로그인 클라이언트 객체 생성
                String json = new Gson().toJson(client);
                Log.i("info","login");
            }
        });
    }
}
