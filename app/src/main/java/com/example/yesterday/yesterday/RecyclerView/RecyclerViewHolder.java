package com.example.yesterday.yesterday.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.yesterday.yesterday.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    //recycler 각각의 개별 item을 보여주는 view
    public View itemView;

    public TextView text;
    public TextView endDate;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        //이미지 제외한 나머지 레이아웃
        //이미지 가져와 이벤트 처리
        text = itemView.findViewById(R.id.recycler_text);
        endDate = itemView.findViewById(R.id.recycler_end_date);
    }
}
