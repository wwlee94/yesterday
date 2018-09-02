package com.example.yesterday.yesterday.UI;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.yesterday.yesterday.R;

public class Update_dialog extends Dialog {

    private View.OnClickListener dialog_cancle_listener;
    private View.OnClickListener dialog_ok_listener;
    EditText update_text;
    Button cancle_btn,ok_btn;
    String kind;


    public Update_dialog(Context context){
        super(context);
    }

    public Update_dialog(Context context,
                         View.OnClickListener dialog_cancle_listener,
                         View.OnClickListener dialog_ok_listener,String kind){
        super(context);
        this.dialog_cancle_listener = dialog_cancle_listener;
        this.dialog_ok_listener = dialog_ok_listener;
        this.kind = kind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_update_dialog);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_update_dialog);
        update_text = (EditText)findViewById(R.id.update_text);
        cancle_btn = (Button)findViewById(R.id.cancle_btn);
        ok_btn = (Button)findViewById(R.id.ok_btn);

        /*cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().
            }
        });*/

        // 클릭 이벤트 셋팅
        if (dialog_cancle_listener != null && dialog_ok_listener != null) {
            cancle_btn.setOnClickListener(dialog_cancle_listener);
            ok_btn.setOnClickListener(dialog_ok_listener);
        }

    }
}
