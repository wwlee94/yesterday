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
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;

import com.example.yesterday.yesterday.UI.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

//AlarmProgress의 리시버
public class AlarmProgressReceiver extends BroadcastReceiver {

    public static boolean isPush = true;

    ArrayList<RecyclerItem> items = new ArrayList<RecyclerItem>();

    int favoriteCount = 0;

    String favorite[];

    //Notification 클래스는 푸시 알림을 만들거나 받아온 알림의 객체를 가지는 클래스이고
    //NotificationManager 클래스는 Notification 객체를 가지고 푸시 알림을 보내주는 클래스

    @Override
    public void onReceive(Context context, Intent intent) {
        //알람 시간이 되었을때 onReceive를 호출함
        Log.d("AlarmReceiver", "Progress 호출 받음 -> 푸시 알림 완료");

        final Context mContext = context;

        isPush = false;

        //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //알림을 클릭 했을 때 전환되는 화면
        Intent Activity = new Intent(context, LoginActivity.class);

        //Notification 객체에 파라미터로 담기 위한 PendingIntent 객체 생성
        PendingIntent progressSender = PendingIntent.getActivity(context, AlarmUtils.PROGRESSCODE, Activity, PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification 객체 생성 및 푸시 알림에 대한 각종 설정
        Notification.Builder progressBuilder = new Notification.Builder(context);
        progressBuilder.setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                .setTicker("Yesterday")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("목표 진행 상황")
                .setContentText("진행상황을 보려면 더 보기를 클릭하세요.")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(progressSender)
                .setAutoCancel(true);

        //SharedPreferences로 데이터를 객체로 가져오기
        items = getSharedPreferencesItems(context);
        Log.d("items 개수", "" + items.size());
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getFavorite() == 1) {
                favoriteCount++;
            }
        }
        favorite = new String[favoriteCount];
        int x = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getFavorite() == 1) {
                //favorite[x] = "음식: " + items.get(i).getFood() + " 기간: " + items.get(i).getEndDate() + " 횟수: " + items.get(i).getCurrentCount();
                favorite[x] = items.get(i).getFood()+"을(를) "+items.get(i).getCount()+"번 중 "+items.get(i).getCurrentCount()+"번 먹었습니다. 마감: "
                        +items.get(i).getEndDate();
                x++;
            }
        }

        //드래그 후 알림창에 최종 보여질 텍스트
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle(progressBuilder);
        inboxStyle.setSummaryText("더 보기");
        //favorite의 개수에 따라 텍스트 개수 다르게
        if(favoriteCount==0){
            inboxStyle.addLine("즐겨찾기 된 목표가 없습니다.");
        }
        else {
            for (int i = 0; i < favoriteCount; i++) {
                inboxStyle.addLine(favorite[i]);
            }
        }
        progressBuilder.setStyle(inboxStyle);

        //NotificationManager를 이용하여 푸시 알림 보내기
        notificationmanager.notify(AlarmUtils.PROGRESSCODE, progressBuilder.build());

        //알람 재등록
        new AlarmUtils(mContext).AlarmProgress(10000);
    }

    //SharedPreferences에 저장된 데이터 가져오는 메서드
    public ArrayList<RecyclerItem> getSharedPreferencesItems(Context context) {

        ArrayList<RecyclerItem> item;

        //SharedPreferences로 데이터를 객체로 가져오기
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("ITEM", "");
        Type myType = new TypeToken<ArrayList<RecyclerItem>>() {
        }.getType();
        item = gson.fromJson(json, myType);

        return item;
    }
}