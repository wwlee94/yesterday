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

public class TabFailFragment extends Fragment {

    private ViewGroup rootView;

    //RecyclerView
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewAdapter adapter;

    private ArrayList<RecyclerItem> tempItems;
    //목표를 담기위한 RecyclerItem의 배열
    private ArrayList<RecyclerItem> items;

    private FloatingActionButton fab;

    ItemTouchHelperCallback callback;
    ItemTouchHelper itemTouchHelper;

    private Boolean isrun;

    public TabFailFragment() {
        // Required empty public constructor

        items = new ArrayList<RecyclerItem>();
        tempItems = new ArrayList<RecyclerItem>();

        isrun = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "onResume : TapFailFragment");

            items = ((HomeActivity) getActivity()).getItemsFail();

            tempItems.clear();
            tempItems.addAll(items);
            items.clear();
            items.addAll(tempItems);
            adapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate : TapFailFragment");

        // * 앱 실행 이후 DB로 값 가져오고 생성 될 때만 한 번 RecyclerView에 뿌려주고
        // 이후 추가되는 항목은 onResume에서 별로로 추가 항상 DB에서 가져오면 느려질 것이기 때문 *
        // 목표DB를 저장할 items
            items = ((HomeActivity) getActivity()).getItemsFail();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView : TapFailFragment");

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab_fail, container, false);

        //다른 Fragment or Activity에 있는 view 가져와 적용 시키는 것
        fab = (FloatingActionButton) getActivity().findViewById(R.id.floating_action_button);

        //RecyclerView 초기화
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fail_recyclerview);
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

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
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