package com.example.yesterday.yesterday.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yesterday.yesterday.ClientLoginInfo;
import com.example.yesterday.yesterday.PushAlarm.AlarmProgressReceiver;
import com.example.yesterday.yesterday.PushAlarm.AlarmUtils;
import com.example.yesterday.yesterday.R;

import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.UI.HomeFrags.AddFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.CalendarFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.GoalFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.HomeFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.StatisticsFragment;
import com.example.yesterday.yesterday.server.CheckTypeServer;
import com.example.yesterday.yesterday.server.DeleteGoalServer;
import com.example.yesterday.yesterday.server.SelectGoalServer;
import com.example.yesterday.yesterday.server.SelectDateServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    public static Context mContext;
    //Activity
    public Activity mActivity;
    //toolbar
    private Toolbar toolbar;
    //FragmentManager
    private FragmentManager fragmentManager;
    //SharedPreferences
    public SharedPreferences loginSetting;
    public SharedPreferences.Editor editor;

    //MaterialDrawer
    private PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("홈").withIcon(R.drawable.ic_home_solid_white).withIconTintingEnabled(true);
    private PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("홈_첫번째").withIcon(R.drawable.ic_wb_sunny_black_24dp).withIconTintingEnabled(true);
    private PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("홈_두번째").withIcon(R.drawable.ic_help_outline_black_24dp).withIconTintingEnabled(true);
    private PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("섹션_첫번째").withIcon(R.drawable.ic_settings_black_24dp).withIconTintingEnabled(true);
    private PrimaryDrawerItem logout = new PrimaryDrawerItem().withIdentifier(5).withName("로그아웃").withIcon(R.drawable.ic_playlist_add_black_24dp).withIconTintingEnabled(true);

    private SecondaryDrawerItem sectionHeader = new SecondaryDrawerItem().withName("section_header");

    Drawer drawerResult;
    AccountHeader headerResult;
    //private Handler handler;

    //BottomBar
    public BottomBar bottomBar;
    //Fragment
    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private GoalFragment goalFragment;
    private StatisticsFragment statisticsFragment;
    private CalendarFragment calendarFragment;
    //Canlendar Icon
    private ImageView calendarView;

    //ClientGoal DB 연동 결과값
    String result;
    //각 Fragment에서 items가 필요할 때 공유하기 위한 변수
    private ArrayList<RecyclerItem> items;
    private ArrayList<RecyclerItem> itemsGoal;
    private ArrayList<RecyclerItem> itemsSuccess;
    private ArrayList<RecyclerItem> itemsFail;

    //
    String name;
    //client
    ClientLoginInfo client;

    boolean isPush;

    public HomeActivity() {

        //Activity
        mActivity = HomeActivity.this;

        //FragmentManager
        fragmentManager = getSupportFragmentManager();
        //handler = new Handler();

        //Fragment
        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        goalFragment = new GoalFragment();
        statisticsFragment = new StatisticsFragment();
        calendarFragment = new CalendarFragment();

        items = new ArrayList<RecyclerItem>();
        itemsGoal = new ArrayList<RecyclerItem>();
        itemsSuccess = new ArrayList<RecyclerItem>();
        itemsFail = new ArrayList<RecyclerItem>();

        client = new ClientLoginInfo();

        isPush = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "HomeActivity onStart / 시작");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "HomeActivity onRestart / 다시 실행");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "HomeActivity onResume / 다시 시작");
        //reNewClientGoal();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "HomeActivity onPause / 일시 정지");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "HomeActivity onStop / 정지");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "HomeActivity onDestroy / 종료");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SharedPreference
        loginSetting = getSharedPreferences("loginSetting", MODE_PRIVATE);
        editor = loginSetting.edit();
        Log.i("ID", loginSetting.getString("ID", ""));

        //TODO: DB 갱신
        reNewClientGoal();


        mContext = this;

        Log.d("TAG", "onCreate / 앱 생성(초기화)");

        //10시 진행 상황 푸시 알림
        //위에서 item 데이터 가져온 것으로 알림
        Log.d("Start_isPush", "" + isPush);
        if (AlarmProgressReceiver.isPush) {
            new AlarmUtils(getApplicationContext()).AlarmProgress(0);
            new AlarmUtils(getApplicationContext()).AlarmIsRegister(0);
            Log.d("End_isPush", "" + isPush);
        }

        //MaterialDrawer 쓰기위해 toolbar의 id를 가져와 객체 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("어제 점심 뭐 먹었지 ?");

        //Intent로 client가져오기  -> client 아직 안쓰임
        Intent intent = getIntent();
        client = (ClientLoginInfo) intent.getSerializableExtra("client");
        Log.i("ID", client.getId());
        Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();

        //TODO: DB 갱신
        reNewClientGoal();

        //logout listener
        //item5.set
        //Calendar
        //Calendar View로 넘어가면 밑에 바텀바 focus 어케 해결!?
        calendarView = (ImageView) findViewById(R.id.toolbar_calendar_button);
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(calendarFragment, "달력");
            }
        });

        // MaterialDrawer
        // Create the AccountHeader -> 사용자 계정(이미지,이름,이메일)헤더 생성
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)        //배경이미지
                .withCompactStyle(true)                         //소형스타일
                .withProfileImagesClickable(false)              //프로필이미지선택X
                //.withSavedInstance(savedInstanceState)
                .addProfiles(
                        new ProfileDrawerItem().withName(client.getName()).withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .build();
        //create the drawer and remember the `Drawer` result object
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)                           //toolbar에 drawer추가
                .withAccountHeader(headerResult)                //drawer에 계정 정보 추가
                .addDrawerItems(                                //drawer에 들어갈 item추가
                        item1, item2, item3,
                        new DividerDrawerItem(),
                        sectionHeader,
                        item4, logout
                )
                //drawer를 클릭 했을 때 이벤트 처리
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        //logout clicked
                        if (drawerItem == logout) {
                            if ((client.getType()).equals("회원")) {
                                editor.clear();
                                editor.commit();
                            } else if ((client.getType()).equals("카카오")) {
                                onClickLogout();
                                editor.clear();
                                editor.commit();
                            }
                            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                })
                //.withFullscreen(true)
                //반투명화 시켜줌.. style에 statusBar랑 같이 쓰면
                .withTranslucentStatusBar(false)
                //색 안 넣으면 투명화 layout
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                //.withSavedInstance(savedInstanceState)
                .build();

        //bottomBar를 tab했을 때 id를 구분해 해당 내부코드를 실행하여 Fragment의 전환이 이루어짐
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {

                //transaction 객체를 가져옴
                //if 가져온 tabId가 tab_home일때 homeFragment화면으로 전환
                if (tabId == R.id.tab_home) {
                    replaceFragment(homeFragment, "HOME");
                    Log.d("HomeFragment", "" + tabId);
                }
                //if 가져온 tabId가 tab_add일때 해당 화면으로 전환
                else if (tabId == R.id.tab_add) {
                    replaceFragment(addFragment, "ADD");
                    //2131296560
                    Log.d("AddFragment", "" + tabId);
                }
                //if 가져온 tabId가 tab_goal일때 해당 화면으로 전환
                else if (tabId == R.id.tab_goal) {
                    replaceFragment(goalFragment, "GOAL");
                    Log.d("GoalFragment", "" + tabId);
                }
                //if 가져온 tabId가 tab_statistics일때 해당 화면으로 전환
                else if (tabId == R.id.tab_statistics) {
                    replaceFragment(statisticsFragment, "STATISTICS");
                    Log.d("StatisticsFragment", "" + tabId);
                }
            }
        });
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                //transaction 객체를 가져옴
                //if 가져온 tabId가 tab_home일때 homeFragment화면으로 전환
                if (tabId == R.id.tab_home) {
                    replaceFragment(homeFragment, "HOME");
                }
                //if 가져온 tabId가 tab_add일때 해당 화면으로 전환
                else if (tabId == R.id.tab_add) {
                    replaceFragment(addFragment, "ADD");
                }
                //if 가져온 tabId가 tab_goal일때 해당 화면으로 전환
                else if (tabId == R.id.tab_goal) {
                    replaceFragment(goalFragment, "GOAL");
                }
                //if 가져온 tabId가 tab_statistics일때 해당 화면으로 전환
                else if (tabId == R.id.tab_statistics) {
                    replaceFragment(statisticsFragment, "STATISTICS");
                }
            }
        });

        //sharedfreference에 items 저장
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<RecyclerItem>>() {
        }.getType();
        String json = gson.toJson(items, listType);
        editor.putString("ITEM", json);
        editor.commit();
    }

    //Fragment 화면 전환
    public void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentContainer, fragment, tag);
        transaction.commit();
    }

    //갱신
    public void reFresh() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.detach(goalFragment).attach(goalFragment).commit();
    }

    public AddFragment getAddFragment() {
        return addFragment;
    }

    public CalendarFragment getCalendarFragment() {
        return calendarFragment;
    }


    public void reNewClientGoal() {
        items.clear();
        itemsGoal.clear();
        itemsSuccess.clear();
        itemsFail.clear();

        //파싱된 데이터를 메소드를 통해 items에 대입
        items = getClientGoal();
        //fooddata를 조회해 해당되는 food의 개수를 가져옴
        //TODO: fooddata 값을 가져오긴 하는데 기간 조회가 어려움
        items = getCurrentCount();

        //fail인 아이템 currentCount 초기화
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType().equals("fail")) {
                items.get(i).setCurrentCount(items.get(i).getCount());
            }
        }
        //오늘 날짜와 목표설정 마감일을 비교하여 type 업데이트!!
        items = compareDate();

        for (int i = 0; i < items.size(); i++) {
            //type이 success인 것만 가져옴
            if (items.get(i).getType().equals("default")) {
                itemsGoal.add(items.get(i));
            } else if (items.get(i).getType().equals("success")) {
                itemsSuccess.add(items.get(i));
            } else if (items.get(i).getType().equals("fail")) {
                itemsFail.add(items.get(i));
            }
        }
    }

    // DB 연동해서 Select
    // 현재 로그인한 id에 해당하는 목표들을 가져오는 메소드
    public ArrayList<RecyclerItem> getClientGoal() {

        result = null;
        //ClientGoal 데이터베이스에 접속해 JSONObject 결과값 받아오는 코드
        try {
            result = new SelectGoalServer(loginSetting.getString("ID", "")).execute().get();
            Log.i("ID", client.getId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //성공 , 실패 여부
            if (result.equals("fail")) {
                Log.d("ClientGoal", "데이터 조회 실패");
            } else {
                Log.d("ClientGoal", "데이터 조회 성공");
                Log.d("ClientGoal", result);
            }
        }
        //result -> json - String 형태
        try {
            //이클립스에서 데이터 담을 때 역순 작업
            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = (JSONArray) jsonObject.get("CLIENTGOAL");
            //jsonArray.length() -> 각각의 {id,food,...} 전체의 갯수
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject list = (JSONObject) jsonArray.get(i);

                String userID = list.getString("USERID");
                String food = list.getString("FOOD");
                int count = list.getInt("COUNT");
                String startDate = list.getString("STARTDATE");
                String endDate = list.getString("ENDDATE");
                int favorite = list.getInt("FAVORITE");
                String type = list.getString("TYPE");
                //item에 파싱한 list 값을 넣어줌
                items.add(new RecyclerItem(userID, food, count, startDate, endDate, favorite, type));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    //목표에 해당하는 음식을 먹은 횟수 조회
    public ArrayList<RecyclerItem> getCurrentCount() {

        result = null;
        //fooddata 데이터베이스에 접속해 JSONObject 결과값 받아오는 코드
        try {
            result = new SelectDateServer(loginSetting.getString("ID", "")).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //성공 , 실패 여부
            if (result.equals("fail")) {
                Log.d("ClientGoal", "음식 개수 조회 실패");
            } else {
                Log.d("ClientGoal", "음식 개수 조회 성공");
                Log.d("ClientGoal", result);
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = (JSONArray) jsonObject.get("FOODCOUNT");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            Date startDate = null;
            Date endDate = null;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject list = (JSONObject) jsonArray.get(i);
                String food = list.getString("FOOD");
                String dateStr = list.getString("DATE");
                try {
                    //String -> Date 형변환
                    date = simpleDateFormat.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }//try
                Log.d("가져온 음식 ", food);
                Log.d("가져온 음식 기간 ", dateStr);
                for (int k = 0; k < items.size(); k++) {
                    //type 은 default 와 success만 비교
                    if (items.get(k).getType().equals("default") || items.get(k).getType().equals("success")) {
                        //items의 food와 DB에서 가져온 food가 같다면
                        if (items.get(k).getFood().equals(food)) {
                            try {
                                //String -> Date 형변환
                                startDate = simpleDateFormat.parse(items.get(k).getStartDate());
                                endDate = simpleDateFormat.parse(items.get(k).getEndDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }//try

                            //startDate <= date <= endDate 범위
                            if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
                                Log.d("음식종류", "clientgoal 음식: " + items.get(k).getFood() + " /타입: " + items.get(k).getType());
                                Log.d("시간범위", "startDate:" + items.get(k).getStartDate() + " <= Date:" + dateStr + " <= endDate:" + items.get(k).getEndDate());
                                items.get(k).setCurrentCount(items.get(k).getCurrentCount() + 1);
                            }

                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    //날짜 비교 후 결과 값 도출
    //목표 설정 마감일 > 오늘 날짜(지났으면) 성공 여부 판별
    public ArrayList<RecyclerItem> compareDate() {

        String result = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //오늘 날짜
        Date currentDate = new Date();
        Log.d("currentDate", format.format(currentDate));

        //검사 날짜
        for (int i = 0; i < items.size(); i++) {
            //index:-1 -> 삭제 안 일어남
            int index = -1;
            if (items.get(i).getType().equals("default")) {
                try {
                    Date checkDate = format.parse(items.get(i).getEndDate());
                    int check = currentDate.compareTo(checkDate);
                    //마감일이 지난 item들 currentDate > checkDate
                    if (check > 0) {
                        Log.d("currentDate > checkDate", format.format(checkDate));

                        //Type 변경 전 해당 음식의 type이 중복되는 지 Check!!
                        for (int k = 0; k < items.size(); k++) {
                            //음식 이름이 같고 그 음식의 이름을 가진 타입 중 success가 있는 지 판별
                            if (items.get(k).getFood().equals(items.get(i).getFood())) {
                                if (items.get(k).getType().equals("success")) {

                                    //success 가 존재 한다면 지워
                                    try {
                                        result = new DeleteGoalServer(loginSetting.getString("ID", ""), items.get(k).getFood(), items.get(k).getType()).execute().get();
                                        Log.d("delete 하는 items 값", items.get(k).getFood() + items.get(k).getType());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (result.equals("success")) {
                                            Log.d("DeleteGoalServer", "데이터 삭제 성공");
                                        } else {
                                            Log.d("DeleteGoalServer", "데이터 삭제 실패");
                                        }

                                        index = k;

                                    }//finally
                                }//success
                            }
                        }

                        //중복 체크 후 DB 연동 Type 변경 코드
                        //
                        try {
                            result = new CheckTypeServer("admin", items.get(i).getFood(), items.get(i).getFavorite(), items.get(i).getType()).execute().get();
                            Log.d("DB Update 하는 items값", items.get(i).getFood() + items.get(i).getFavorite() + items.get(i).getType());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (result.equals("success")) {
                                Log.d("CheckTypeServer", "데이터 변경 성공");
                            } else {
                                Log.d("CheckTypeServer", "데이터 변경 실패");
                            }
                        }//finally


                        //DB에서 변경했으니 items에서도 변경!!
                        items.get(i).setType("success");
                        //즐겨찾기 설정되어있으면 해제
                        if (items.get(i).getFavorite() == 1) {
                            items.get(i).setFavorite(0);
                        }
                        //count: -1 -> 삭제된 items 없음
                        //TODO: FOR문 밖에서 처리해야 할 수도
                        if (index != -1) {
                            //앞서 중복되는 success 타입의 데이터 items에서 제거
                            Log.d("index", "" + index);
                            items.remove(index);
                        }

                    }//check > 0
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }//default
        }

        return items;
    }

    //* 다른 Fragment에서 getItems 해온 뒤에 그 items 변수를 다른 곳에서 변경하던 지우던 다 주소값으로 연결되어 있는 듯
    //  즉, Fragment에서 바꾼 items가 여기 HomeActivity의 items에서도 바뀐다.
    public void setItems(ArrayList<RecyclerItem> items) {
        this.items = items;
    }

    public ArrayList<RecyclerItem> getItems() {
        return items;
    }

    public ArrayList<RecyclerItem> getItemsGoal() {
        return itemsGoal;
    }

    public ArrayList<RecyclerItem> getItemsSuccess() {
        return itemsSuccess;
    }

    public ArrayList<RecyclerItem> getItemsFail() {
        return itemsFail;
    }

    //백 버튼 눌렀을 때
    public boolean onKeyPreIme(int KeyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_SHORT).show();

            return true;
        }
        return false;
    }

    //로그아웃 메소드
    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Toast.makeText(getApplicationContext(), "카카오톡 로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
