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

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddGoalActivity extends AppCompatActivity {

    //이거 EditText 인데 TextView로 가져와도 잘되네..
    TextView foodView;
    TextView countView;
    TextView endDateView;  //마감일
    //TextView favoriteView; //즐겨찾기

    String goalType; //목표 설정 타입
    String food;
    int count;
    String endDate;
    String startDate;
    int favorite = 0;
    String type = "default";

    //임시
    String userID = "admin";

    Button button;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        foodView = (TextView) findViewById(R.id.goal_edit_food);
        countView = (TextView) findViewById(R.id.goal_edit_count);
        endDateView = (TextView) findViewById(R.id.goal_edit_date);


        //라디오 버튼 TODO: layout은 존재하지만 사용은 안하는 중
        RadioGroup goalGroup = (RadioGroup) findViewById(R.id.goal_radioGroup);
        int GroupID = goalGroup.getCheckedRadioButtonId();
        goalType = ((RadioButton) findViewById(GroupID)).getText().toString();

        //라디오 버튼 바꾸었을 때
        goalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                goalType = radioButton.getText().toString();
            }
        });

        //저장 버튼
        button = (Button) findViewById(R.id.goal_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                food = foodView.getText().toString();
                count = Integer.parseInt(countView.getText().toString());
                endDate = endDateView.getText().toString();
                startDate = getDate();

                // AsyncTask 객체 생성 -> 목표 정보 DB에 INSERT
                try {
                    result = new AddGoalServer(userID, food, count, startDate, endDate, favorite, type).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (result.equals("success")) {
                        //DB 연동 전 intent로 데이터 전송한 것
                        Intent intent = new Intent();
                        intent.putExtra("USERID", userID);//나중에 삭제 예정 전역변수 이용하면 됌.
                        intent.putExtra("FOOD", food);
                        intent.putExtra("COUNT", count);
                        intent.putExtra("STARTDATE", startDate);
                        intent.putExtra("ENDDATE", endDate);
                        intent.putExtra("FAVORITE", favorite);
                        intent.putExtra("TYPE",type);

                        //삭제 예정
                        //intent.putExtra("TYPE", goalType);

                        setResult(RESULT_OK, intent);

                        finish();

                    } else {
                        showOverlap();
                        Log.d("AddGoalServer", "데이터 저장 실패 (DB 연동 오류 or 기본 키 포함되있어서)");
                    }
                }
            }
        });
    }

    public void showOverlap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목표 설정");
        builder.setMessage("중복되는 음식이 들어갑니다.\n음식 종류를 다시 입력해주세요.");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    //TODO: 할 수 있으면 데이터 중복일 때랑 서버 오류 일때 구분해서
    public void showDBError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목표 설정");
        builder.setMessage("현재 서버에 오류가 있습니다.\n잠시 후에 다시 시도해주세요.");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public String getDate() {
        long now = System.currentTimeMillis();
        // Data 객체에 시간을 저장한다.
        Date date = new Date(now);
        // 각자 사용할 포맷을 정하고 문자열로 만든다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdfNow.format(date);

        return nowDate;
    }
}
