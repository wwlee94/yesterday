package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        Toast.makeText(getApplicationContext(), name +"님 환영합니다.", Toast.LENGTH_LONG).show();
        Log.i("error", name);
    }
}
