
package com.example.yesterday.yesterday;

        import android.app.Activity;
        import android.app.Application;

        import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;
    //로그인 했을 때의 사용자 정보
    private ClientLoginInfo client;

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }
    //client 정보 전역변수 값 설정
    public void setClientInfo(ClientLoginInfo client){
        this.client = client;
    }
    //client 정보 전역변수 값 반환
    public ClientLoginInfo getClientInfo(){
        return this.client;
    }
}