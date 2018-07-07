package com.example.yesterday.yesterday.UI.GoalTapFrags;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.RecyclerView.RecyclerViewAdapter;


import java.util.ArrayList;


public class TabTotalFragment extends Fragment {

    private ViewGroup rootView;

    //RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    private String[] names={"Charile","Andrew","Liz","Thomas","Sky","Andy","Lee","Park","Kim","Jeong"};

    //결과 -> key="NAME"
    private String name;

    public TabTotalFragment() {
        // Required empty public constructor

    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("TAG","onResume : TapTotalFragment");

        //GoalFragment로부터 name 데이터 받음!!
        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString("NAME");
            Log.d("FINAL VALUE",name);
            adapter.addItem(name);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("TAG","onCreate : TapTotalFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TAG","onCreateView : TapTotalFragment");

        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_tab_total,container,false);

        //RecyclerView 초기화
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview);
        //layoutManager 생성
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //ArrayList 생성해서 RectclerItem으로 데이터 넣어둠
        ArrayList<RecyclerItem> items = new ArrayList<RecyclerItem>();
        for(int i=0;i<names.length;i++) {
            items.add(new RecyclerItem(names[i]));
        }
        //RecylerView에 layout 적용
        recyclerView.setLayoutManager(layoutManager);
        //Decoration 추가 -> 구분선 Vertical: 수직으로 구분한다!
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_line));
        recyclerView.addItemDecoration(decoration);
        //animator 설정
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Adapter 생성
        adapter = new RecyclerViewAdapter(items);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return rootView;
    }
}