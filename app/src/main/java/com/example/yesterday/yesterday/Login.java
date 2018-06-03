package com.example.yesterday.yesterday;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    EditText ed_id, ed_pw;
    Button btn_login,kakaoLogoutBtn;
    TextView jsonText;
    String sId, sPw;
    ClientLoginInfo client;
    private ProgressDialog pDialog;
    private static final String  WEBIP = "192.168.0.72";

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            ed_id = (EditText) findViewById(R.id.IDText);
            ed_pw = (EditText) findViewById(R.id.PassText);
            btn_login = (Button) findViewById(R.id.loginBtn);
            kakaoLogoutBtn = (Button) findViewById(R.id.kakaoLogoutBtn);
            //jsonText = (TextView) findViewById(R.id.jsonText);

        btn_login.setOnClickListener(new View.OnClickListener() {  // 로그인 버튼 리스너
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 id와 pw값을 받아옴 ..... 리스너 안에서 가져와야함 ㅠ
                sId = ed_id.getText().toString();   // id
                sPw = ed_pw.getText().toString();   // password
                System.out.println(sId + "  dfdfdfdfdf   " + sPw);

                // AsyncTask 객체 생성, 호출
                LoginServer loginServer=new LoginServer(sId,sPw);
                loginServer.execute();
                Log.i("info","login");
            }
        });

        kakaoLogoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        //카카오톡 로그인
        requestMe();
    }

    public class LoginServer extends AsyncTask<Void,Void,String>{
        String parent_id;
        String parent_pw;
        String answer;


        public LoginServer(String parent_id,String parent_pw) { //로그인 id, pw 받기
            this.parent_id = parent_id;
            this.parent_pw = parent_pw;
        }

        @Override
        protected String doInBackground(Void... params) {
            //request 를 보내줄 클라이언트 생성   (okhttp 라이브러리 사용)
            OkHttpClient client = new OkHttpClient();
            Response response;
            RequestBody requestBody = null;

            //보낼 데이터를 파라미터 형식으로 body에 넣음
            requestBody = new FormBody.Builder().add("parent_id",parent_id).add("parent_pw",parent_pw).build();

            // post형식으로 url로 만든 body를 보냄
            Request request = new Request.Builder()
                    .url("http://"+ WEBIP + ":8080/adConnector/Connector")
                    .post(requestBody)
                    .build();
            try {
                response = client.newCall(request).execute();
                /////////////////////////////////// newcall 하고 응답받기를 기다리는중
                answer = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return answer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //로그인 성공 여부 확인
            if(s.equals("success"))
                Log.i("loginCheck",s);
            else Log.i("loginCheck","fail");

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        }

    }

    // 카카오톡 로그인을 위한 콜백 클래스
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
//                        finish();
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

//                    Log.e("UserProfile", userProfile.toString());
//                    Log.e("UserProfile", userProfile.getId() + "");

                    long number = userProfile.getId();

                    Log.i("userProfile",Long.toString(number));
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ

        }
    }


    public void requestMe() {
        //카카오톡 로그인 유저의 정보를 받아오는 함수

        UserManagement.requestMe(new MeResponseCallback() {
            @Override //실패 하였을 시
            public void onFailure(ErrorResult errorResult) {
                Log.e("Fail", "error message=" + errorResult);
//                super.onFailure(errorResult);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("onSessionClosed", "onSessionClosed1 =" + errorResult);
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                Log.d("NotSignedUp", "onNotSignedUp ");
            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.e("UserProfile", result.toString());
                Log.e("UserProfile", result.getId() + "");
            }
        });
    }
}
