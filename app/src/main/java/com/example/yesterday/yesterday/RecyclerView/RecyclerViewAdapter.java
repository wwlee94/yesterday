package com.example.yesterday.yesterday.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;

import java.util.ArrayList;

//getItemCount -> getItemViewType -> onCreateViewHolder -> onBindViewHolder
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    //Recycler에 담을 데이터를 클래스로 만든 RecyclerItem을 ArrayList로 생성
    private ArrayList<RecyclerItem> items;
    private Context context;

    public RecyclerViewAdapter(ArrayList items){
        this.items = items;
    }

    // View 생성 (한줄짜리 이미지랑 텍스트 들어있는 view) , ViewHolder 호출
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_per_item,parent,false);
        RecyclerViewHolder holder=new RecyclerViewHolder(view);
        return holder;
    }

    //View의 내용을 해당 포지션의 데이터로 set
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //final을 써줘야 동작.. ??
        final int itemPosition = position;
        //Log.d("TAG",""+itemPosition);

        holder.name.setText(items.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,(itemPosition+1)+" 번째 : "+items.get(itemPosition).getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //데이터 셋의 크기를 리턴
    @Override
    public int getItemCount() {
        return items.size();
    }
}
