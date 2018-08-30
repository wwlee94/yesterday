package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.ClientLoginInfo;
import com.example.yesterday.yesterday.server.LoginServer;
import com.example.yesterday.yesterday.R;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    EditText id_text, pw_text;
    Button login_btn, join_btn;
    TextView actionBarHeader;
    SessionCallback callback;
    CheckBox auto_check;
    String sId, sPw;
    ClientLoginInfo client, logoutClient;
    String result = "";

    //자동 로그인 SharedPreferences 객체 생성
    SharedPreferences loginSetting;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //카카오 해시키 가져오기
        getHashKey();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_header);

        actionBarHeader = (TextView) findViewById(R.id.actionbar_text);
        actionBarHeader.setText("로그인");


        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        id_text = (EditText) findViewById(R.id.id_text);
        pw_text = (EditText) findViewById(R.id.pw_text);
        login_btn = (Button) findViewById(R.id.login_btn);

        join_btn = (Button) findViewById(R.id.join_btn);
        auto_check = (CheckBox) findViewById(R.id.auto_check);

        client = new ClientLoginInfo();
        client.setLog(false);
        logoutClient = new ClientLoginInfo();
        //login SharedPreferences
        loginSetting = getSharedPreferences("loginSetting", 0);
        editor = loginSetting.edit();

        //auto_check true면 id, pw setting
        if (loginSetting.getBoolean("Auto_Login_enabled", false)) {
            //id_text.setText(loginSetting.getString("ID",""));
            //pw_text.setText(loginSetting.getString("PW",""));
            auto_check.setChecked(true);
            sId = loginSetting.getString("ID", "");
            sPw = loginSetting.getString("PW", "");
            Log.d("login","outoLogin");
            MyLogin(sId, sPw);
        }

        //auto_check click listener
        auto_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("ID", id_text.getText().toString());
                    editor.putString("PW", pw_text.getText().toString());
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                } else {
                    editor.clear();
                    editor.commit();
                }
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {  // 로그인 버튼 리스너
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 id와 pw값을 받아옴 ..... 리스너 안에서 가져와야함 ㅠ
                sId = id_text.getText().toString();   // id
                sPw = pw_text.getText().toString();   // password
                MyLogin(sId, sPw);
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    public void MyLogin(String id, String pw) {
        // AsyncTask 객체 생성, 호출
        try {
            result = new LoginServer(id, pw).execute().get();
        } catch (Exception e) {
            e.getMessage();
        }

        Log.d("result", result);

        if (result.equals("fail")) {
            Toast.makeText(getApplicationContext(), "로그인 실패 입니다.", Toast.LENGTH_LONG).show();
        } else {
            client.setType("회원");
            client.setName(result);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("client", client);
            startActivity(intent);
        }
    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.yesterday.yesterday", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HASH_KEY", "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
                        Log.e("kakao error", message);
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    Log.e("UserProfile", userProfile.getId() + "");

                    long number = userProfile.getId();
                    client.setName(userProfile.getNickname());
                    client.setType("카카오");

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("client", client);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

        }
    }
}