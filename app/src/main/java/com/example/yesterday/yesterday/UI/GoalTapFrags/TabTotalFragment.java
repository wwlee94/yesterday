package com.example.yesterday.yesterday.UI.GoalTapFrags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.RecyclerView.ItemTouchHelperCallback;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.RecyclerView.RecyclerViewAdapter;
import com.example.yesterday.yesterday.server.SelectGoalServer;


import org.json.JSONObject;

import java.util.ArrayList;


public class TabTotalFragment extends Fragment {

    private ViewGroup rootView;

    //RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    private ArrayList<RecyclerItem> items;
    private String[] texts = {"Charile", "Andrew", "Liz", "Thomas", "Sky", "Andy"};
    private String[] endDates = {"2017-12-30", "2017-7-28", "2017-5-30", "2017-10-22", "2017-8-20", "2017-10-28"};

    ItemTouchHelperCallback callback;
    ItemTouchHelper itemTouchHelper;

    //결과 -> key="TEXT"
    private String userID;
    private String food;
    private String count;
    private String startDate;
    private String endDate;
    private String favorite;

    //삭제 예정
    private String text;
    private String type;

    public TabTotalFragment() {
        //ArrayList 생성해서 RectclerItem으로 데이터 넣어둠
        items = new ArrayList<RecyclerItem>();
        for (int i = 0; i < texts.length; i++) {
            items.add(new RecyclerItem("admin", "김치찌개", "10", "2017-5-28", endDates[i], "0", texts[i]));
        }
    }

    // GoalAddActivity에서 목표 설정을 완료한 후 finish() 했을 때
    // tabTotalFragment onResume 실행됌 onCreateView 실행 안됌
    // 해당 액티비티에서 입력받은 문자를 받기 위함
    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume : TapTotalFragment");

        //GoalFragment로부터 name 데이터 받음!!
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = bundle.getString("USERID");  //나중에 삭제 예정 전역변수 이용하면 됌.
            food = bundle.getString("FOOD");
            count = bundle.getString("COUNT");
            startDate = bundle.getString("STARTDATE");
            endDate = bundle.getString("ENDDATE");
            favorite = bundle.getString("FAVORITE");
            //삭제 예정
            text = bundle.getString("TEXT");
            type = bundle.getString("TYPE");

            if (text != null && endDate != null && type != null) {
                Log.d("FINAL VALUE", text);
                Log.d("FINAL VALUE", endDate);
                Log.d("FINAL VALUE", type);
                adapter.onItemAdd(userID, food, count, startDate, endDate, favorite, text);
                //bundle.clear() 해도 bundle을 null로 만들어 버리진 않음;
                bundle.clear();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate : TapTotalFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TAG", "onCreateView : TapTotalFragment");

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab_total, container, false);

        //RecyclerView 초기화
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        //layoutManager 생성
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //RecylerView에 layout 적용
        recyclerView.setLayoutManager(layoutManager);
        //Decoration 추가 -> 구분선 Vertical: 수직으로 구분한다!
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.recycler_line));
        recyclerView.addItemDecoration(decoration);
        //animator 설정
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Adapter 생성 , RecyclerView에 적용
        adapter = new RecyclerViewAdapter(items);
        recyclerView.setAdapter(adapter);

        //드래그 or 스와이프 이벤트를 사용 하기 위한 ItemTouchHelper
        callback = new ItemTouchHelperCallback(adapter, getActivity());
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Inflate the layout for this fragment
        return rootView;
    }
}