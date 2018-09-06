package com.example.yesterday.yesterday.PushAlarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.UI.LoginActivity;
import com.example.yesterday.yesterday.server.FoodListServer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class AlarmIsRegisterReceiver extends BroadcastReceiver{

    String result;

    //SharedPreferences
    public SharedPreferences loginSetting;
    public SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {

        //아래에 쓰일 알림 메소드 파라미터로 넘겨주기 위함
        final Context mContext = context;

        int count = getFoodLength(context);
        Log.d("COUNT",""+getFoodLength(context));

        if(count==0) {

            //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //알림을 클릭 했을 때 전환되는 화면
            Intent Activity = new Intent(context, LoginActivity.class);

            // # requestSender
            PendingIntent requestSender = PendingIntent.getActivity(context, AlarmUtils.ISRESISTERCODE, Activity, PendingIntent.FLAG_UPDATE_CURRENT);

            //Notification 객체 생성 및 푸시 알림에 대한 각종 설정
            Notification.Builder requestBuilder = new Notification.Builder(context);
            requestBuilder.setSmallIcon(R.drawable.ic_help_outline_black_24dp)
                    .setTicker("Yesterday")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("음식 추가 알림")
                    .setContentText("오늘 음식을 추가하셨나요 ? *^^*")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(requestSender)
                    .setAutoCancel(true);

            notificationmanager.notify(AlarmUtils.ISRESISTERCODE, requestBuilder.build());
        }
        else {
            Log.d("AlarmIsRegisterReceiver","Count: "+count+" 이므로 IsRegister 알림 사용 X");
        }

        //알람 재등록
        new AlarmUtils(mContext).AlarmIsRegister(10000);
    }
    public int getFoodLength(Context context){

        int count=-1;

        try{
            //ID 가져오기
            loginSetting = context.getSharedPreferences("loginSetting", MODE_PRIVATE);
            editor = loginSetting.edit();
            //DATE 가져오기
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
            result = new FoodListServer(loginSetting.getString("ID",""),date).execute().get();
            JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
            count = jsonArray.length();

        }catch (Exception e){
            e.printStackTrace();
        }

        return count;
    }
}
