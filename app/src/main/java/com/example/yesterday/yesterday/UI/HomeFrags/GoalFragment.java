package com.example.yesterday.yesterday.UI.HomeFrags;


import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import com.example.yesterday.yesterday.R;

import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.UI.AddGoalActivity;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabGoalFragment;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabSuccessFragment;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabTotalFragment;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabFailFragment;
import com.example.yesterday.yesterday.UI.HomeActivity;

import com.roughike.bottombar.BottomBar;


import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

//목표 화면 Fragment
public class GoalFragment extends Fragment {

    //주황 배경 무슨 오류..!?
    private ViewGroup rootView;

    //TabLayout , ViewPager
    //private ViewPager viewPager;
    private NoSwipeViewPager mViewPager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;

    private TabTotalFragment tabTotalFragment;
    private TabGoalFragment tabGoalFragment;
    private TabFailFragment tabFailFragment;
    private TabSuccessFragment tabSuccessFragment;

    //GoalTabFrags 에서 쓰일 item리스트
    //private ArrayList<RecyclerItem> items;

    //FloatingActionButton
    private FloatingActionButton floatingActionButton;
    private Handler handler;

    //onActivityResult -> 다음 액티비티에게 ACT주고 다시 받아와 같은 값인지(성공했는지) 검사하기 위함
    public int REQUEST_ACT = 1234;

    public GoalFragment() {

        //<!-- TODO: GoalFragment에서 처음에 DB 읽어서 items 만들어주고 나머지 fragment로 items를 파라미터로 전해주면? 앱 실행 될때 DB 한번만 읽어오쥬
        //     TODO: 최종 admin -> 전역변수 이용하여 id가져와 대입할 것

        tabTotalFragment = new TabTotalFragment();
        tabGoalFragment = new TabGoalFragment();
        tabFailFragment = new TabFailFragment();
        tabSuccessFragment = new TabSuccessFragment();

        //UI 스레드 쓰기위한 handler
        handler = new Handler();

        /*
        //HomeActivity에 변수두고 공유하는 방법을 쓰면 bundle로 데이터 주고 받고 필요없음....
        //프래그먼트에 데이터를 전달하기위한 Bundle
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("ITEMS", items);
        //해당 프래그먼트에 데이터 전달
        tabTotalFragment.setArguments(bundle);
        */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //생성자와 onCreateView만 있어도 ok
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        //전역변수 Client 이름 출력
        GlobalApplication application =(GlobalApplication)getActivity().getApplication();
        ClientLoginInfo client = application.getClientInfo();
        Toast.makeText(getActivity(),client.getName(),Toast.LENGTH_LONG).show();
        */

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_goal, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mViewPager = (NoSwipeViewPager) rootView.findViewById(R.id.tab_viewpager);

        //tabLayouy 초기화
        tabLayout.addTab(tabLayout.newTab().setText("전체"));
        tabLayout.addTab(tabLayout.newTab().setText("목표"));
        tabLayout.addTab(tabLayout.newTab().setText("실패"));
        tabLayout.addTab(tabLayout.newTab().setText("완료"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TAG", "Press TabLayout");
                //tab 변경 시 bottombar 없어져 있어도 show()해줌
                BottomBar bottomBar = getActivity().findViewById(R.id.bottomBar);
                bottomBar.getShySettings().showBar();
                //tab 변경 시 hide 후 -> 다시 show 이벤트 처리
                floatingActionButton.hide();
                Thread hideAndShow = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(250);
                            //UI스레드
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //변경할 UI 작성
                                    floatingActionButton.show();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                hideAndShow.start();
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //ViewPager 초기화
        //class로 설정한 viewpager 어댑터 정의 후 적용
        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //페이지 스와이프 기능 해제
        mViewPager.setPagingEnabled(false);

        //floatingActionButton (+)버튼 // 초기화 밑 이벤트 처리
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent((HomeActivity) getActivity(), AddGoalActivity.class);
                //fragment의 startActivityForResult !!
                startActivityForResult(intent, REQUEST_ACT);

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    //FragmentPagerAdapter -> 정적인 뷰페이저에 어울림 미리 페이지를 load 해두기 때문
    //FragmentStatePagerAdapter -> 동적인 뷰페이저에 어울림 페이지 focus가 사라지면 destroy하기 때문
    //즉 FragmentStatePagerAdapter는 getItem 할 때 객체를 새로 생성하고 return해주어야함 생성자에 생성해서 하면 오류남
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return tabTotalFragment;
                case 1:
                    return tabGoalFragment;
                case 2:
                    return tabFailFragment;
                case 3:
                    return tabSuccessFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    //GoalFragment에서 GoalAddActivity로 넘어간 이후 데이터 다시 받아오기 위함
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "GoalFragment onActivityResult");
        // Check which request we're responding to
        //내가 지정한 RESULT_ACT
        if (requestCode == REQUEST_ACT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                //GoalAddActivity로 부터 데이터 받음!!
                String userID = data.getStringExtra("USERID");
                String food = data.getStringExtra("FOOD");
                int count = data.getIntExtra("COUNT",-1);
                String startDate = data.getStringExtra("STARTDATE");
                String endDate = data.getStringExtra("ENDDATE");
                int favorite = data.getIntExtra("FAVORITE",-1);
                String type = data.getStringExtra("TYPE");

                //items 가져옴
                //TODO: 얘네의 기능은????????????????
                //ArrayList<RecyclerItem> items = ((HomeActivity) getActivity()).getItems();
                //items.add(new RecyclerItem(userID,food,count,startDate,endDate,favorite,type));

                //TabFragment로 데이터 전달
                Bundle bundle[] = new Bundle[2];
                for(int i=0;i<2;i++) {
                    bundle[i] = new Bundle();
                    bundle[i].putString("USERID", userID);//나중에 삭제 예정 전역변수 이용하면 됌.
                    bundle[i].putString("FOOD", food);
                    bundle[i].putInt("COUNT", count);
                    bundle[i].putString("STARTDATE", startDate);
                    bundle[i].putString("ENDDATE", endDate);
                    bundle[i].putInt("FAVORITE", favorite);
                    bundle[i].putString("TYPE", type);
                }
                tabTotalFragment.setArguments(bundle[0]);
                tabGoalFragment.setArguments(bundle[1]);

                // Do something with the contact here (bigger example below)
            }
        }
    }

}
