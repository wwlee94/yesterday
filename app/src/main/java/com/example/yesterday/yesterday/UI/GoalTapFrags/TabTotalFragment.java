package com.example.yesterday.yesterday.UI.GoalTapFrags;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.RecyclerView.ItemTouchHelperCallback;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.RecyclerView.RecyclerViewAdapter;
import com.example.yesterday.yesterday.UI.HomeActivity;



import java.util.ArrayList;

public class TabTotalFragment extends Fragment {

    private ViewGroup rootView;

    //RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    //목표를 담기위한 RecyclerItem의 배열
    private ArrayList<RecyclerItem> items;

    private FloatingActionButton fab;

    ItemTouchHelperCallback callback;
    ItemTouchHelper itemTouchHelper;

    //결과 -> key="TEXT"
    private String userID;
    private String food;
    private int count;
    private String startDate;
    private String endDate;
    private int favorite;
    private String type;

    private Boolean isrun;

    public TabTotalFragment() {

        items = new ArrayList<RecyclerItem>();

        isrun = true;

    }

    // GoalAddActivity에서 목표 설정을 완료한 후 finish() 했을 때
    // tabTotalFragment onResume 실행됌 onCreateView 실행 안됌
    // 해당 액티비티에서 입력받은 문자를 받기 위함
    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume : TapTotalFragment");

        //TODO:데이터 추가,삭제,변경될 때 매번 DB에서 select해서 가져오려면 여기서 해결

        //GoalFragment로부터 name 데이터 받음!! -> 목표추가 했을 때 이렇게 데이터 추가 물론 DB에도 저장됨
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = bundle.getString("USERID");  //나중에 삭제 예정 전역변수 이용하면 됌.
            food = bundle.getString("FOOD");
            count = bundle.getInt("COUNT");
            startDate = bundle.getString("STARTDATE");
            endDate = bundle.getString("ENDDATE");
            favorite = bundle.getInt("FAVORITE");
            type = bundle.getString("TYPE");

            //값들이 null이 아니면 adapter에 item 추가
            if (food != null && count != -1 && endDate != null && favorite != -1) {
                adapter.onItemAdd(userID, food, count, startDate, endDate, favorite ,type);
                //bundle.clear() 해도 bundle을 null로 만들어 버리진 않음;
                bundle.clear();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate : TapTotalFragment");

        /*
        //HomeActivity에 변수두고 공유하는 방법을 쓰면 bundle로 데이터 주고 받고 필요없음....
        if (isrun) {
            Bundle bundle = getArguments();
            items = bundle.getParcelableArrayList("ITEMS");

            isrun = false;
        }
        */

        // * 앱 실행 이후 DB로 값 가져오고 생성 될 때만 한 번 RecyclerView에 뿌려주고
        // 이후 추가되는 항목은 onResume에서 별로로 추가 항상 DB에서 가져오면 느려질 것이기 때문 *
        if(isrun) {
            // 목표DB를 저장할 items
            items = ((HomeActivity) getActivity()).getItems();
            //items 한 번 불러오고 난 이후에 false로 전환
            isrun = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TAG", "onCreateView : TapTotalFragment");

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab_total, container, false);

        //다른 Fragment or Activity에 있는 view 가져와 적용 시키는 것
        fab = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);

        //RecyclerView 초기화
        recyclerView = (RecyclerView) rootView.findViewById(R.id.total_recyclerview);
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

        //recyclerView를 스크롤 했을 때의 이벤트 처리
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if ((dy > 0) && fab.isShown()) {
                    fab.hide();
                } else if (dy < 0) {
                    fab.show();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                /*
                //스크롤을 멈췄을 때 이벤트 TODO: FloatActionButton 이벤트 추후 변경
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                {
                }
                super.onScrollStateChanged(recyclerView, newState);
                */
            }
        });

        //드래그 or 스와이프 이벤트를 사용 하기 위한 ItemTouchHelper
        callback = new ItemTouchHelperCallback(adapter, getActivity());
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Inflate the layout for this fragment
        return rootView;
    }
}