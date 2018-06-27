package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.yesterday.yesterday.R;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class HomeActivity extends AppCompatActivity {

    //MaterialDrawer
    private PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("홈");
    private SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("첫번째");
    private  Drawer result;
    //BottomBar
    private CallLogFragment callLogFragment;
    private ContactsFragment contactsFragment;
    private SetMacroFragment setMacroFragment;
    //
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //MaterialDrawer
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);

        //Intent로 로그인 이름 가져옴
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("woowonLee").withEmail("wwlee94@gmail.com").withIcon(getResources().getDrawable(R.drawable.ic_account_box_black_50dp))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        return true;
                    }
                })
                .build();
        //BottomBar
        callLogFragment = new CallLogFragment();
        contactsFragment = new ContactsFragment();
        setMacroFragment = new SetMacroFragment();

        //bottomBar를 tab했을 때 id를 구분해 해당 내부코드를 실행하여 Fragment의 전환이 이루어짐

        BottomBar bottomBar=(BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                //transaction 객체를 가져옴
                //if 가져온 tabId가 tab_call_log일때 callLogFragment화면으로 전환
                if(tabId==R.id.tab_call_log){
                    replaceFragment(callLogFragment);
                }
                //if 가져온 tabId가 tab_contacts일때 해당 화면으로 전환
                else if(tabId==R.id.tab_contacts){
                    replaceFragment(contactsFragment);
                }
                //if 가져온 tabId가 tab_macro_setting일때 해당 화면으로 전환
                else if(tabId==R.id.tab_macro_setting){
                    replaceFragment(setMacroFragment);
                }
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentContainer, fragment);
        fragmentTransaction.commit();
    }
}
