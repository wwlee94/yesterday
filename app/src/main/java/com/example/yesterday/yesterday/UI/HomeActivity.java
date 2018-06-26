package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class HomeActivity extends FragmentActivity {

    private CallLogFragment callLogFragment;
    private ContactsFragment contactsFragment;
    private SetMacroFragment setMacroFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        callLogFragment = new CallLogFragment();
        contactsFragment = new ContactsFragment();
        setMacroFragment = new SetMacroFragment();

         initFragment();     //초기 플래그먼트(화면) 설정

        //bottomBar를 tab했을 때 id를 구분해 해당 내부코드를 실행하여 Fragment의 전환이 이루어짐

        BottomBar bottomBar=(BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                //transaction 객체를 가져옴
                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
                //if 가져온 tabId가 tab_call_log일때 callLogFragment화면으로 전환
                if(tabId==R.id.tab_call_log){
                    transaction.replace(R.id.contentContainer,callLogFragment).commit();
                }
                //if 가져온 tabId가 tab_contacts일때 해당 화면으로 전환
                else if(tabId==R.id.tab_contacts){
                    transaction.replace(R.id.contentContainer,contactsFragment).commit();
                }
                //if 가져온 tabId가 tab_macro_setting일때 해당 화면으로 전환
                else if(tabId==R.id.tab_macro_setting){
                    transaction.replace(R.id.contentContainer,setMacroFragment).commit();
                }
            }
        });
    }
    //App 실행시 보여지는 초기 Fragment 설정
    public void initFragment(){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentContainer,callLogFragment);     //contentContainer->framelayout의id
        transaction.addToBackStack(null);
        transaction.commit();       //실행 시킴
    }
}
