package com.example.yesterday.yesterday.PushAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//매일 10시에 진행상황 알람
public class AlarmUtils {

    static final int PROGRESSCODE = 0;
    static final int ISRESISTERCODE = 1;
    static final int OVERREGISTERCODE = 2;

    Context context;

    AlarmManager am;

    public AlarmUtils(Context context) {
        this.context = context;

        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    //알림 10시에 진행상황
    public void AlarmProgress(int delay) {
        Log.d("AlarmProgress", "진행 상황 알림 설정 시작");

        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd / HH시 mm분 ss초");

        Intent intent = new Intent(context, AlarmProgressReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, PROGRESSCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        // # ProgressCalendar 시간 설정
        //현재 시각이 오전 10시를 지나지 않았다면
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 10, 1, 0);
            Log.d("시간", "" + calendar.get(Calendar.HOUR_OF_DAY) + " < 10 [ 10시 이전 ]");
            Log.d("날짜", "" + simple.format(calendar.getTime()));
        }
        //현재 시각이 오전10시 이후라면 다음날 10시로
        else if (calendar.get(Calendar.HOUR_OF_DAY) >= 10) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) +1, 10, 1, 0);
            Log.d("시간", "" + calendar.get(Calendar.HOUR_OF_DAY) + " >= 10 [ 10시 이후 ]");
            Log.d("날짜", "" + simple.format(calendar.getTime()));
        }

        //알람 예약 Doze모드에서도 알림 작동하도록
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + delay, sender);
        Log.d("AlarmProgress", "Progress 알림 설정 완료");

    }

    //알람을 추가했는지 21시에 알림
    public void AlarmIsRegister(int delay) {
        Log.d("AlarmResister", "진행 상황 알림 설정 시작");

        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd / HH시 mm분 ss초");

        Intent intent = new Intent(context, AlarmIsRegisterReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, ISRESISTERCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        // # IsRegisterCalendar 시간 설정
        //현재 시각이 오전 10시를 지나지 않았다면
        if (calendar.get(Calendar.HOUR_OF_DAY) < 21) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 21, 1, 0);
            Log.d("시간", "" + calendar.get(Calendar.HOUR_OF_DAY) + " < 21 [ 21시 이전 ]");
            Log.d("날짜", "" + simple.format(calendar.getTime()));
        }
        //현재 시각이 오전10시 이후라면 다음날 10시로
        else if (calendar.get(Calendar.HOUR_OF_DAY) >= 21) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, 21, 1, 0);
            Log.d("시간", "" + calendar.get(Calendar.HOUR_OF_DAY) + " >= 21 [ 21시 이후 ]");
            Log.d("날짜", "" + simple.format(calendar.getTime()));
        }

        //알람 예약 Doze모드에서도 알림 작동하도록
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + delay, sender);
        Log.d("AlarmResister", "OverRegister 알림 설정 완료");
    }

    //음식 추가시 80% 넘겼으면 알림
    public void AlarmOverRegister(int delay,String flag,String food){

        Log.d("AlarmOverRegister", "진행 상황 알림 설정 시작");

        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd / HH시 mm분 ss초");

        Intent intent = new Intent(context, AlarmOverRegisterReceiver.class);
        intent.putExtra("FLAG",flag);
        intent.putExtra("FOOD",food);

        PendingIntent sender = PendingIntent.getBroadcast(context, OVERREGISTERCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //알람 예약 Doze모드에서도 알림 작동하도록
        //추가 버튼 클릭 시 over됬으면 현재시각에 알림 발생
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, sender);
        Log.d("AlarmOverRegister", "OverResister 알림 설정 완료");

    }
}