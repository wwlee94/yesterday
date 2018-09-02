package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.ClientLoginInfo;
import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.ClientUpdateServer;

public class UpdateinfoActivity extends AppCompatActivity {

    TextView id_text,name_text;
    Button id_update_btn, name_update_btn, pw_update_btn, ok_btn;
    ClientLoginInfo client;
    Update_dialog update_dialog;
    String updateText = "";
    String result = "";
    String kind = "";

    public SharedPreferences loginSetting;
    public SharedPreferences.Editor editor;

    public UpdateinfoActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

        id_text = (TextView)findViewById(R.id.client_id_text);
        name_text = (TextView)findViewById(R.id.client_name_text);
        id_update_btn = (Button)findViewById(R.id.id_update_btn);
        name_update_btn = (Button)findViewById(R.id.name_update_btn);
        pw_update_btn = (Button)findViewById(R.id.pw_update_btn);
        ok_btn = (Button)findViewById(R.id.ok_btn);

        loginSetting = getSharedPreferences("loginSetting", MODE_PRIVATE );
        editor = loginSetting.edit();

        //인텐트로 회원 정보 받기 ㅎㅎㅎ
        Intent intent  = getIntent();
        client = (ClientLoginInfo) intent.getSerializableExtra("client");
        Log.i("ID",client.getId());
        Toast.makeText(getApplicationContext(), "회원정보입니당", Toast.LENGTH_LONG).show();

        id_text.setText(client.getId());
        name_text.setText(client.getName());

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("ID", client.getId());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("client", client);
                startActivity(intent);
            }
        });
    }

    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.id_update_btn:
                kind = "id";
                update_dialog = new Update_dialog(this,
                        dialogCancleListener, // dialog cancle button event
                        dialogOkListener,kind); // dialog ok button event
                update_dialog.show();
                Toast.makeText(getApplicationContext(), kind + " update dialog",Toast.LENGTH_SHORT).show();
                break;

            case R.id.name_update_btn:
                kind = "name";
                update_dialog = new Update_dialog(this,
                        dialogCancleListener, // dialog cancle button event
                        dialogOkListener,kind); // dialog ok button event
                update_dialog.show();
                Toast.makeText(getApplicationContext(), kind +"update dialog",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private View.OnClickListener dialogCancleListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "dialog cancle",Toast.LENGTH_SHORT).show();
            update_dialog.dismiss();
        }
    };

    private View.OnClickListener dialogOkListener = new View.OnClickListener() {
        public void onClick(View v) {
            updateText = update_dialog.update_text.getText().toString();
            // id update
            if(update_dialog.kind.equals("id")){
                //ClientUpdateServer clientUpdateServer = new ClientUpdateServer(client.getId(),updateText,"id");
            }
            // name update
            else if(update_dialog.kind.equals("name")){
                //ClientUpdateServer clientUpdateServer = new ClientUpdateServer(client.getId(),updateText,"name");
            }

            try {
                result = new ClientUpdateServer(client.getId(), updateText, update_dialog.kind).execute().get();
            }catch (Exception e){
                e.getMessage();
            }

            // id overlap -> server connect
            if(result.equals("overlap")){
                Toast.makeText(getApplicationContext(), kind+" overlap",Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("success")) {
                update_dialog.dismiss();
                Toast.makeText(getApplicationContext(), kind+" update", Toast.LENGTH_SHORT).show();
                if(kind.equals("id")){
                    id_text.setText(updateText);
                    client.setId(updateText);
                }
                else if(kind.equals("name")){
                    name_text.setText(updateText);
                    client.setName(updateText);
                }
            }else {
                Toast.makeText(getApplicationContext(), " failed",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener pwListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "pw change dialog",Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener updateListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "update",Toast.LENGTH_SHORT).show();
        }
    };

}
