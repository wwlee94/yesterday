package com.example.yesterday.yesterday.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    //recyclerview 가 처음 보이면 작동(여러번)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //final을 써줘야 동작.. ??
        final RecyclerViewHolder viewholder = holder;
        //Log.d("TAG",""+itemPosition);

        holder.name.setText(items.get(position).getName());
        //추가 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(viewholder.getAdapterPosition());
            }
        });
        //삭제 이벤트
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context,(viewholder.getAdapterPosition()+1)+" 번째 : "+items.get(viewholder.getAdapterPosition()).getName(),Toast.LENGTH_SHORT).show();
                //삭제 명령
                return true;
            }
        });
    }

    //데이터 셋의 크기를 리턴
    @Override
    public int getItemCount() {
        return items.size();
    }

    //아이템 추가
    public void addItem(String name){
        //items ArrayList<RecyclerItem> 에 데이터 넣고
        items.add(new RecyclerItem(name));
        //아이템이 추가 되었다고 통지함 -> holder에다가 ?
        notifyItemInserted(getItemCount());
    }
    //아이템 삭제
    public void deleteItem(int position){
        try {
            items.remove(position);
            notifyItemRemoved(position);
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
}
