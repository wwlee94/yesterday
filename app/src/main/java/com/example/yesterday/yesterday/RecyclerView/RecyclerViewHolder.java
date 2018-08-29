package com.example.yesterday.yesterday.RecyclerView;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yesterday.yesterday.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    //recycler 각각의 개별 item을 보여주는 view
    public View itemView;

    public RelativeLayout regularlayout;
    //success,fail시 가운데 줄
    public LinearLayout endlayout;
    public TextView goal;
    public TextView count;
    public TextView currentCount;
    public TextView endDate;
    public ImageView favoriteView;

    public RelativeLayout swipedlayout;
    public TextView undo;

    //각각의 imageView에서 다르게 적용되는 변수가 필요해 holder에서 선언한 변수
    //isClicked  false : 클릭 안된 상태 true : 클릭 된 상태
    boolean isClicked;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        isClicked = false;

        //기본 item 뷰
        regularlayout = itemView.findViewById(R.id.recycler_regularlayout);
        //success,fail시 가운데 줄
        endlayout = itemView.findViewById(R.id.recycler_end_layout);
        //이미지 가져와 이벤트 처리
        favoriteView = itemView.findViewById(R.id.recycler_image_view);
        //이미지 제외한 나머지 레이아웃
        goal = itemView.findViewById(R.id.recycler_goal);
        count = itemView.findViewById(R.id.recycler_count);
        currentCount = itemView.findViewById(R.id.recycler_currentcount);
        endDate = itemView.findViewById(R.id.recycler_end_date);

        //스와이프 했을 때 item 뷰
        swipedlayout = itemView.findViewById(R.id.recycler_swipedlayout);
        undo = itemView.findViewById(R.id.recycler_undo);
    }
}
