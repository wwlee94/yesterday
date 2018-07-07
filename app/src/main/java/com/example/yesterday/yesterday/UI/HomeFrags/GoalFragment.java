package com.example.yesterday.yesterday.UI.HomeFrags;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.RecyclerView.RecyclerViewAdapter;
import com.example.yesterday.yesterday.UI.GoalAddActivity;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabGoalFragment;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabSuccessFragment;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabTotalFragment;
import com.example.yesterday.yesterday.UI.GoalTapFrags.TabUserGoalFragment;
import com.example.yesterday.yesterday.UI.HomeActivity;

import java.util.ArrayList;

//목표 화면 Fragment
public class GoalFragment extends Fragment {

    //주황 배경 무슨 오류..!?
    private ViewGroup rootView;

    //TabLayout , ViewPager
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;

    //FloatingActionButton
    private FloatingActionButton floatingActionButton;

    public GoalFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    //생성자와 onCreateView만 있어도 ok
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_goal,container,false);

        tabLayout = (TabLayout)rootView.findViewById(R.id.tab_layout);
        viewPager = (ViewPager)rootView.findViewById(R.id.tab_viewpager);

        //tabLayouy 초기화
        tabLayout.addTab(tabLayout.newTab().setText("전체"));
        tabLayout.addTab(tabLayout.newTab().setText("목표"));
        tabLayout.addTab(tabLayout.newTab().setText("다짐"));
        tabLayout.addTab(tabLayout.newTab().setText("완료"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //ViewPager 초기화
        //class로 설정한 viewpager 어댑터 정의 후 적용
        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TAG","Press TabLayout");
                viewPager.setCurrentItem(tab.getPosition(),false);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        //floatingActionButton 초기화 밑 이벤트 처리
        floatingActionButton = (FloatingActionButton)rootView.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((HomeActivity)getActivity(),GoalAddActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
    //FragmentPagerAdapter -> 정적인 뷰페이저에 어울림 미리 페이지를 load 해두기 때문
    //FragmentStatePagerAdapter -> 동적인 뷰페이저에 어울림 페이지 focus가 사라지면 destroy하기 때문
    //즉 FragmentStatePagerAdapter는 getItem 할 때 객체를 새로 생성하고 return해주어야함 생성자에 생성해서 하면 오류남
    public class PagerAdapter extends FragmentPagerAdapter {

        private int tabCount;

        public PagerAdapter(FragmentManager fm){
            super(fm);
    }
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new TabTotalFragment();
                case 1:
                    return  new TabGoalFragment();
                case 2:
                    return new TabUserGoalFragment();
                case 3:
                    return new TabSuccessFragment();
                    default:
                        return null;
            }
        }
        @Override
        public int getCount() {
            return 4;
        }
    }
}
