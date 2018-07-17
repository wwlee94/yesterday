package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yesterday.yesterday.R;

public class GoalAddActivity extends AppCompatActivity {

    TextView textView;  //목표 문자열
    TextView dateView;  //마감일

    String goalType; //목표 설정 타입
    String date;
    String text;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_add);

        textView=(TextView)findViewById(R.id.goal_edit_text);
        dateView=(TextView)findViewById(R.id.goal_edit_date);

        //라디오 버튼
        RadioGroup goalGroup=(RadioGroup)findViewById(R.id.goal_radioGroup);
        int GroupID=goalGroup.getCheckedRadioButtonId();
        goalType = ((RadioButton) findViewById(GroupID)).getText().toString();

        //라디오 버튼 바꾸었을 때
        goalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton=(RadioButton)findViewById(checkedId);
                goalType=radioButton.getText().toString();
            }
        });

        //저장 버튼
        button=(Button)findViewById(R.id.goal_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text = textView.getText().toString();
                date = dateView.getText().toString();
                //goalType

                Intent intent = new Intent();
                intent.putExtra("NAME",text);
                intent.putExtra("DATE",date);
                intent.putExtra("TYPE",goalType);

                setResult(RESULT_OK,intent);

                finish();
            }
        });
    }
}
