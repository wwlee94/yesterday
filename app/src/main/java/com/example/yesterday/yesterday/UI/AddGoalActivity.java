package com.example.yesterday.yesterday.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.AddGoalServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddGoalActivity extends AppCompatActivity {

    //이거 EditText 인데 TextView로 가져와도 잘되네..
    TextView foodView;
    TextView countView;
    TextView actionBarHeader;

    DatePicker datePicker;

    String food;
    int count;
    String endDate;
    String startDate;
    int favorite = 0;
    String type = "default";


    SharedPreferences loginSetting;

    Button button;

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        loginSetting = getSharedPreferences("loginSetting", 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_header);

        foodView = (TextView) findViewById(R.id.goal_edit_food);
        countView = (TextView) findViewById(R.id.goal_edit_count);


        datePicker = (DatePicker) findViewById(R.id.goal_datepicker);
        Calendar calendar = Calendar.getInstance();
        //오늘 날짜로 default 설정
        datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
        //최소
        datePicker.setMinDate(System.currentTimeMillis());
        /*
        //최대 (오늘날짜 + 12개월후)
        calendar.add(Calendar.MONTH,12);
        datePicker.setMaxDate(calendar.getTimeInMillis());
        */


        //액션바
        actionBarHeader = (TextView) findViewById(R.id.actionbar_text);
        actionBarHeader.setText("목표 추가");

        //저장 버튼
        button = (Button) findViewById(R.id.goal_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                food = foodView.getText().toString();
                count = Integer.parseInt(countView.getText().toString());
                endDate = String.format("%d-%d-%d",datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());
                startDate = getDate();

                // AsyncTask 객체 생성 -> 목표 정보 DB에 INSERT
                try {
                    result = new AddGoalServer(loginSetting.getString("ID",""), food, count, startDate, endDate, favorite, type).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (result.equals("success")) {
                        //DB 연동 전 intent로 데이터 전송한 것
                        Intent intent = new Intent();
                        intent.putExtra("USERID", loginSetting.getString("ID",""));//나중에 삭제 예정 전역변수 이용하면 됌.
                        intent.putExtra("FOOD", food);
                        intent.putExtra("COUNT", count);
                        intent.putExtra("STARTDATE", startDate);
                        intent.putExtra("ENDDATE", endDate);
                        intent.putExtra("FAVORITE", favorite);
                        intent.putExtra("TYPE",type);

                        //삭제 예정
                        //intent.putExtra("TYPE", goalType);

                        setResult(RESULT_OK, intent);


                        //DB값 다시 가져옴
                        HomeActivity homeActivity = ((HomeActivity)HomeActivity.mContext);
                        //TODO: DB 갱신
                        homeActivity.reNewClientGoal();

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
