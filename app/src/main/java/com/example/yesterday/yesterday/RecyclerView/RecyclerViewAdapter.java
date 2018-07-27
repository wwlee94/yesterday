package com.example.yesterday.yesterday.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.DeleteGoalServer;

import java.util.ArrayList;

//getItemCount -> getItemViewType -> onCreateViewHolder -> onBindViewHolder
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    //Recycler에 담을 데이터를 클래스로 만든 RecyclerItem을 ArrayList로 생성
    private ArrayList<RecyclerItem> items;
    private Context context;

    //결과 값
    String result;

    int favoriteCount;

    public RecyclerViewAdapter(ArrayList<RecyclerItem> items) {
        this.items = items;
        favoriteCount = 0;
    }

    // View 생성 (한줄짜리 이미지랑 텍스트 들어있는 view) , ViewHolder 호출
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_per_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    //View의 내용을 해당 포지션의 데이터로 set
    //recyclerview가 처음 보이면 작동(여러번)
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        //final을 써줘야 동작.. ??
        final RecyclerViewHolder viewHolder = holder;

        //* 정적인 부분 *
        //TODO: goal text 안에 있는 부분 recylcler_per_item 에서 새로 textview 만들어 줘야할 듯
        holder.goal.setText("음식 : " + items.get(position).getFood());
        holder.count.setText(items.get(position).getCount());
        holder.endDate.setText(items.get(position).getEndDate());

        //favorite 초기화 작업

        //favorite == 0 이면 선택 X
        if (Integer.parseInt(items.get(position).getFavorite()) == 0) {
            holder.isClicked = false;
            holder.imageView.setSelected(false);
        }
        //favorite == 1 이면 선택된 것
        else if (Integer.parseInt(items.get(position).getFavorite()) == 1) {
            holder.isClicked = true;
            holder.imageView.setSelected(true);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewHolder.isClicked==false이면 즐겨찾기 설정 Dialog
                //true 이면 즐겨찾기 해제 Dialog 를 띄워준다.
                showFavoritesDialog(v,viewHolder);
            }
        });

        //추가 이벤트
        //동적인 부분이라 holder의 getAdapterPosition 써야해
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toast보여주고 deleteItem 해야지 !!
                Toast.makeText(context, (viewHolder.getAdapterPosition() + 1) + " 번째 : "
                                + " / ID : " + items.get(viewHolder.getAdapterPosition()).getUserID()
                                + " / Food : " + items.get(viewHolder.getAdapterPosition()).getFood()
                                + " / Count : " + items.get(viewHolder.getAdapterPosition()).getCount()
                        , Toast.LENGTH_SHORT).show();
                //onItemDelete(viewHolder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //onItemDelete(viewHolder.getAdapterPosition());
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
    public void onItemAdd(String userID, String food, String count, String startDate, String endDate, String favorite) {
        //items ArrayList<RecyclerItem> 에 데이터 넣고
        items.add(new RecyclerItem(userID, food, count, startDate, endDate, favorite));
        //아이템이 추가 되었다고 통지함 -> holder에다가 ?
        //추가는 getItemCount 함으로서 제일 마지막 List 뒤에 삽입됨
        notifyItemInserted(getItemCount());
    }

    //아이템 삭제
    public void onItemDelete(String userID, String food, int position) {
        try {
            //toast보여주고 deleteItem 해야지 !!

            // AsyncTask 객체 생성 -> 목표 정보 userID 와 food 에 맞는 정보 DELETE
            try {
                result = new DeleteGoalServer(userID, food).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.equals("success")) {

                //toast보여주고 deleteItem 해야지 !!
                Log.d("VALUE", (position + 1) + " 번째 : " + items.get(position).getFood());
                items.remove(position);
                notifyItemRemoved(position);

                Toast.makeText(context, "데이터 삭제 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "데이터 삭제 실패", Toast.LENGTH_SHORT).show();
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RecyclerItem> getItems() {
        return items;
    }

    public void showFavoritesDialog(View v, RecyclerViewHolder holder) {

        final View view = v;
        final RecyclerViewHolder viewHolder = holder;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("즐겨찾기 설정");
        //favorite select 해제 -> 설정 할때
        if (viewHolder.isClicked == false) {
            int count=0;
            for(int i=0;i<getItemCount();i++){
                if(Integer.parseInt(items.get(i).getFavorite())==1){
                    count++;
                }
            }
            Log.d("favorite 개수",""+count);
            //현재 favorite 개수가 2개 이하 일때만 설정 가능
            if(count<3) {
                builder.setMessage("해당 목표를 즐겨찾기로 설정하시겠습니까?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                view.setSelected(true);
                                viewHolder.isClicked = true;
                                items.get(viewHolder.getAdapterPosition()).setFavorite("1");
                                Log.d("클릭 후 favorite값",items.get(viewHolder.getAdapterPosition()).getFood()+items.get(viewHolder.getAdapterPosition()).getFavorite());
                                //favorite을 UPDATE 하는 과정을 작성하면 끗 but 그러면 favorite을 취소할 때에도 dialog띄워서 하는게 나을 듯

                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
            //현재 favorite 개수가 3개 이상이면 설정은 X 해제는 O
            else if(count>=3){
                builder.setMessage("즐겨찾기는 3개 이상 설정이 불가합니다.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        }
        //favorite cancel 설정 -> 해제 할때
        else if (viewHolder.isClicked == true) {
            builder.setMessage("해당 목표를 즐겨찾기에서 해제하시겠습니까?");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            view.setSelected(false);
                            viewHolder.isClicked = false;
                            //items 객체에 있는 값도 변경 시켜줘야 현재 전체 viewHolder에 저장된 favorite 개수 알 수 있음
                            items.get(viewHolder.getAdapterPosition()).setFavorite("0");
                            //favorite을 UPDATE 하는 과정을 작성하면 끗 but 그러면 favorite을 취소할 때에도 dialog띄워서 하는게 나을 듯

                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }

        //* 최종 *
        //type에 따라서 builder 다르게 설정하고 show();
        builder.show();
    }
}
