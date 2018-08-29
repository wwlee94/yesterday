package com.example.yesterday.yesterday.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yesterday.yesterday.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder  {

    //recycler 각각의 개별 item을 보여주는 view
    public View itemView;

    public TextView headerTitle;

    public HeaderViewHolder(View itemView){
        super(itemView);
        this.itemView = itemView;

        //헤더
        headerTitle = itemView.findViewById(R.id.recycler_header_title);
    }
}
