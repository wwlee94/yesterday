package com.example.yesterday.yesterday.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.AddGoalServer;

import org.json.JSONObject;

public class AddGoalActivity extends AppCompatActivity {

    TextView textView;  //목표 문자열
    TextView foodView;
    TextView countView;
    TextView endDateView;  //마감일
    //TextView favoriteView; //즐겨찾기

    String goalType; //목표 설정 타입
    String text;
    String food;
    String count;
    String endDate;

    //임시
    String userID="admin";
    String startDate="2017-10-20";
    String favorite ="0";
    //
    Button button;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        textView=(TextView)findViewById(R.id.goal_edit_text);
        foodView=(TextView)findViewById(R.id.goal_edit_food);
        countView=(TextView)findViewById(R.id.goal_edit_count);
        endDateView=(TextView)findViewById(R.id.goal_edit_date);

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
                food = foodView.getText().toString();
                count= countView.getText().toString();
                endDate = endDateView.getText().toString();
                //id
                //startDate
                //favorite

                // AsyncTask 객체 생성 -> 목표 정보 DB에 INSERT
                try{
                    result = new AddGoalServer(userID,food,count,startDate,endDate,favorite).execute().get();
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(result.equals("success")) {
                    //DB 연동 전 intent로 데이터 전송한 것
                    Intent intent = new Intent();
                    intent.putExtra("USERID",userID);//나중에 삭제 예정 전역변수 이용하면 됌.
                    intent.putExtra("TEXT", text);
                    intent.putExtra("FOOD", food);
                    intent.putExtra("COUNT", count);
                    intent.putExtra("STARTDATE", startDate);
                    intent.putExtra("ENDDATE", endDate);
                    intent.putExtra("FAVORITE", favorite);

                    //삭제 예정
                    intent.putExtra("TYPE", goalType);

                    setResult(RESULT_OK, intent);

                    finish();
                }
                else{
                    show();
                    Log.d("AddGoalServer","데이터 저장 실패 (DB 연동 오류 or 기본 키 포함되있어서)");
                }
            }
        });
    }
    void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목표 설정 오류");
        builder.setMessage("중복되는 음식이 들어갑니다.");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });
        builder.show();
    }
}
