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

public class AlarmOverRegisterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //아래에 쓰일 알림 메소드 파라미터로 넘겨주기 위함
        final Context mContext = context;

        String flag = intent.getStringExtra("FLAG");
        String food = intent.getStringExtra("FOOD");
        Log.d("StringFLAG",flag);

        //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //알림을 클릭 했을 때 전환되는 화면
        Intent Activity = new Intent(context, LoginActivity.class);

        // # requestSender
        PendingIntent requestSender = PendingIntent.getActivity(context, AlarmUtils.OVERREGISTERCODE, Activity, PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification 객체 생성 및 푸시 알림에 대한 각종 설정
        Notification.Builder requestBuilder = new Notification.Builder(context);

        if(flag.equals("주의")) {
            requestBuilder.setSmallIcon(R.drawable.ic_help_outline_black_24dp)
                    .setTicker("Yesterday")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("제한 개수 임박")
                    .setContentText(food+"이(가) 설정량의 80%를 넘었습니다. 주의하세요! ^오^")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(requestSender)
                    .setAutoCancel(true);
        }
        else if(flag.equals("실패")){
            requestBuilder.setSmallIcon(R.drawable.ic_help_outline_black_24dp)
                    .setTicker("Yesterday")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("제한 개수 초과")
                    .setContentText(food+"목표를 달성 실패.ㅠ_ㅠ 다음번에는 더 노력해봐요!")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(requestSender)
                    .setAutoCancel(true);
        }
        notificationmanager.notify(AlarmUtils.OVERREGISTERCODE, requestBuilder.build());
    }
}

