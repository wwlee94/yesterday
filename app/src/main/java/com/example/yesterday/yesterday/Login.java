package com.example.yesterday.yesterday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {
    EditText ed_id, ed_pw;
    Button btn_login;
    String sId, sPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_id = (EditText) findViewById(R.id.IDText);
        ed_pw = (EditText) findViewById(R.id.PassText);
        btn_login = (Button) findViewById(R.id.loginBtn);

        sId = ed_id.getText().toString();
        sPw = ed_pw.getText().toString();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_id.append("윤상이 바보");

            }
        });
    }
}
