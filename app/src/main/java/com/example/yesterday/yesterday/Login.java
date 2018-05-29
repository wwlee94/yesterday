package com.example.yesterday.yesterday;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    EditText ed_id, ed_pw;
    Button btn_login;
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
        jsonText = (TextView) findViewById(R.id.jsontext);

        btn_login.setOnClickListener(new View.OnClickListener() {  // 로그인 버튼 리스너
            @Override
            public void onClick(View v) {
                //client = new ClientLoginInfo(sId,sPw); // 로그인 클라이언트 객체 생성
                // 로그인 값 gson 파싱
                //String loginJson = new Gson().toJson(client);
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
                Log.d("loginCheck",s);
            else Log.d("loginCheck","fail");

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        }


    }
//    class getList extends AsyncTask<String,Void,String>{
//        String json;
//        public  getList(String json){
//            this.json = json;
//        }
//        @Override
//        protected String doInBackground(Void... voids) {
//            try {
//
//                URL Url = new URL(URL_ADDRESS); //url화
//                HttpURLConnection conn = (HttpURLConnection) Url.openConnection(); //url을 연결한 객체 생성
//                conn.setRequestMethod("POST"); // post방식 설정
//                conn.setDoOutput(true);  //쓰기모드
//                conn.setDoInput(true);  // 읽기모드
//                conn.setUseCaches(false);  //캐싱데이터 수신 여부
//                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정
//
//                InputStream is = conn.getInputStream();  // input stream 개방
//                OutputStream os = conn.getOutputStream(); // outputstream 개방
//
//                //os.write(json);
//               // os.flush();
//
//                StringBuffer builder = new StringBuffer(); //문자열을 담기 위한 객체
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));  // 문자열 셋팅
//                String line=null;
//
//                line = reader.readLine();
//                jsonText.setText(line);
//                //Log.i("LoadData",LoadData);
//                //JSONArray jsonArray
//
////                while((line = reader.readLine()) != null){
////                    builder.append(line+"\n");
////                    Log.i("LoadData","ddddddd");
////                }
//
//                //builder.append(json);  // 빌더에 json 추가
//
//                //LoadData = builder.toString();
//                //jsonText.setText(LoadData);
//                //Log.i("LoadData",LoadData);
//
//            }catch(MalformedURLException | ProtocolException e){
//                e.printStackTrace();
//            }catch(IOException e){
//                e.printStackTrace();
//            } catch(Exception e){
//                e.printStackTrace();
//            }
//
//            return line;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            //jsonObject = this.jsonObject;
////            pDialog = new ProgressDialog(Login.this);
////            pDialog.setMessage("검색중입니다...");
////            pDialog.setCancelable(true);
////            pDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            //pDialog.dismiss();
//            super.onPostExecute(result);
//            System.out.println(result);
//
//        }
//    }
}
