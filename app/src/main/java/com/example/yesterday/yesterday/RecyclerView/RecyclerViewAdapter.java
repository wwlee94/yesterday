package com.example.yesterday.yesterday.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.server.DeleteGoalServer;
import com.example.yesterday.yesterday.server.UpdateFavoriteServer;

import java.util.ArrayList;

//getItemCount -> getItemViewType -> onCreateViewHolder -> onBindViewHolder
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Recycler에 담을 데이터를 클래스로 만든 RecyclerItem을 ArrayList로 생성
    private ArrayList<RecyclerItem> items;
    private Context context;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    //결과 값
    String result;

    int favoriteCount;

    //isSwiped = true 되었을 때 새로운 view로 전환 되는 데 또 스와이프 안되게하려고
    //useSwipe true:swipe 가능 false:swipe 불가
    boolean useSwipe;


    public RecyclerViewAdapter(ArrayList<RecyclerItem> items) {
        this.items = items;
        favoriteCount = 0;
        useSwipe = true;
    }

    // View 생성 (한줄짜리 이미지랑 텍스트 들어있는 view) , ViewHolder 호출
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        /*
        if (viewType == TYPE_HEADER) {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_header, parent,false);
            return new HeaderViewHolder(view);
            */

        // } else if (viewType == TYPE_ITEM) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_per_item, parent, false);
        return new RecyclerViewHolder(view);
        // }
        // throw new RuntimeException("NO match for " + viewType);
    }

    //View의 내용을 해당 포지션의 데이터로 set
    //recyclerview가 처음 보이면 작동(여러번)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        /*
        if(holder instanceof HeaderViewHolder){
            final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.headerTitle.setText("Header");
        }
        */
        //final을 써줘야 동작.. ??
        if (holder instanceof RecyclerViewHolder) {
            final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;

            //swipedlayout show
            if (items.get(viewHolder.getAdapterPosition()).isShowSwiped == true) {

                //기본 레이아웃 지우고 swipedlayout 보여주기
                viewHolder.regularlayout.setVisibility(View.GONE);
                viewHolder.swipedlayout.setVisibility(View.VISIBLE);

                //스와이프 레이아웃에선 선 지우기
                viewHolder.endlayout.setVisibility(View.GONE);

                //undo 버튼 누르면 아이템 삭제
                viewHolder.undo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //아이템 삭제
                        useSwipe = true;
                        onItemDelete(items.get(viewHolder.getAdapterPosition()).getUserID(), items.get(viewHolder.getAdapterPosition()).getFood()
                                , items.get(viewHolder.getAdapterPosition()).getType(), viewHolder.getAdapterPosition());
                    }
                });
                // Swipedlayout 클릭하면 취소
                // * 그냥 list 클릭하면 ( isShowSwiped = false && useSwipe = true )로 만들어 swipedlayout 풀리고 regularlayout 나오도록 한 것 *
                viewHolder.swipedlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        useSwipe = true;
                        items.get(viewHolder.getAdapterPosition()).isShowSwiped = false;
                        notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
            }

            //regularlayout show
            else if (items.get(viewHolder.getAdapterPosition()).isShowSwiped == false) {

                viewHolder.regularlayout.setVisibility(View.VISIBLE);
                viewHolder.swipedlayout.setVisibility(View.GONE);

                //타입이 default 이면 endlayout 지우기
                if(items.get(viewHolder.getAdapterPosition()).getType().equals("default")){
                    viewHolder.endlayout.setVisibility(View.GONE);
                }
                else{
                    if(items.get(viewHolder.getAdapterPosition()).getType().equals("success")){
                        viewHolder.endlayout.setBackgroundColor(Color.parseColor("#8000FF00"));
                    }
                    viewHolder.endlayout.setVisibility(View.VISIBLE);
                }

                //* 정적인 부분 *
                viewHolder.goal.setText("음식 : " + items.get(position).getFood());
                viewHolder.endDate.setText(items.get(position).getEndDate());

                int current = items.get(position).getCurrentCount();
                int limit = items.get(position).getCount();
                // 70% 이상 빨간색
                if (((float) current / (float) limit) * 100 >= 70) {
                    //pink:FF0266
                    //주황:FD5523
                    viewHolder.currentCount.setTextColor(Color.parseColor("#FF0266"));
                    viewHolder.count.setTextColor(Color.parseColor("#FF0266"));
                }
                else{
                    viewHolder.currentCount.setTextColor(Color.parseColor("#F9AA33"));
                    viewHolder.count.setTextColor(Color.parseColor("#F9AA33"));
                }
                viewHolder.currentCount.setText("횟수 : " + current);
                viewHolder.count.setText(" / " + limit);

                //items 타입이 default 일때만 클릭 이벤트 작동
                //즐겨찾기 설정 부분
                if (items.get(position).getType().equals("default")) {
                    //favorite 초기화 작업
                    //favorite == 0 이면 선택 X
                    if (items.get(position).getFavorite() == 0) {
                        viewHolder.isClicked = false;
                        viewHolder.favoriteView.setSelected(false);
                    }
                    //favorite == 1 이면 선택된 것
                    else if (items.get(position).getFavorite() == 1) {
                        viewHolder.isClicked = true;
                        viewHolder.favoriteView.setSelected(true);
                    }

                    viewHolder.favoriteView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //viewHolder.isClicked==false이면 즐겨찾기 설정 Dialog
                            //true 이면 즐겨찾기 해제 Dialog 를 띄워준다.
                            showFavoritesDialog(v, viewHolder);
                        }
                    });
                }
            }//isShowSwiped=false 일 때 regularlayout 이벤트 들

            //추가 이벤트
            //동적인 부분이라 holder의 getAdapterPosition 써야함
            // * 그냥 list 클릭하면 ( isShowSwiped = false && useSwipe = true )로 만들어 swipedlayout 풀리고 regularlayout 나오도록 한 것 *
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, (viewHolder.getAdapterPosition() + 1) + " 번째 : "
                                    + " / ID : " + items.get(viewHolder.getAdapterPosition()).getUserID()
                                    + " / Food : " + items.get(viewHolder.getAdapterPosition()).getFood()
                                    + " / CurrentCount : "+items.get(viewHolder.getAdapterPosition()).getCurrentCount()
                                    + " / Count : " + items.get(viewHolder.getAdapterPosition()).getCount()
                                    + " / Type : " + items.get(viewHolder.getAdapterPosition()).getType()
                            , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //데이터 셋의 크기를 리턴
    @Override
    public int getItemCount() {
        return items.size();
    }

    /*
        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
    */
    //아이템 추가
    public void onItemAdd(String userID, String food, int count, String startDate, String endDate, int favorite, String type) {
        //items ArrayList<RecyclerItem> 에 데이터 넣고
        items.add(new RecyclerItem(userID, food, count, startDate, endDate, favorite, type));
        //아이템이 추가 되었다고 통지함 -> holder에다가 ?
        //추가는 getItemCount 함으로서 제일 마지막 List 뒤에 삽입됨
        notifyItemInserted(getItemCount());
    }

    //아이템 삭제
    public void onItemDelete(String userID, String food, String type, int position) {
        try {
            //toast보여주고 deleteItem 해야지 !!

            // AsyncTask 객체 생성 -> 목표 정보 userID 와 food 에 맞는 정보 DELETE
            try {
                result = new DeleteGoalServer(userID, food, type).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (result.equals("success")) {

                    //toast보여주고 deleteItem 해야지 !!
                    Log.d("VALUE", (position + 1) + " 번째 : " + items.get(position).getFood());
                    items.remove(position);
                    notifyItemRemoved(position);

                    Toast.makeText(context, "데이터 삭제 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "데이터 삭제 실패", Toast.LENGTH_SHORT).show();
                }
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
            int count = 0;
            //현재 items에 들어있는 favorite 개수 파악
            for (int i = 0; i < getItemCount(); i++) {
                if (items.get(i).getFavorite() == 1) {
                    count++;
                }
            }
            Log.d("favorite 개수", "" + count);
            //현재 favorite 개수가 2개 이하 일때만 설정 가능
            if (count < 3) {
                builder.setMessage("해당 목표를 즐겨찾기로 설정하시겠습니까?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //변수 초기화
                                String userID = items.get(viewHolder.getAdapterPosition()).getUserID();
                                String food = items.get(viewHolder.getAdapterPosition()).getFood();
                                String type = items.get(viewHolder.getAdapterPosition()).getType();
                                int favorite = items.get(viewHolder.getAdapterPosition()).getFavorite();
                                String result = null;

                                //ICON 선택 된 상태로 설정
                                view.setSelected(true);
                                viewHolder.isClicked = true;
                                items.get(viewHolder.getAdapterPosition()).setFavorite(1);

                                Log.d("클릭 후 favorite값", items.get(viewHolder.getAdapterPosition()).getFood() + items.get(viewHolder.getAdapterPosition()).getFavorite());
                                //UpdateFavoriteServer가 실행 되었다는 뜻은 0 -> 1, 1 -> 0 으로 업데이트 하겠다는 뜻

                                // 웹 서버에 DB 연동 요청 (updateFavorite)
                                try {
                                    result = new UpdateFavoriteServer(userID, food, type, favorite).execute().get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (result.equals("success")) {
                                        Toast.makeText(context, "데이터 변경 성공", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "데이터 변경 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
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
            else if (count >= 3) {
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

                            //변수 초기화
                            String userID = items.get(viewHolder.getAdapterPosition()).getUserID();
                            String food = items.get(viewHolder.getAdapterPosition()).getFood();
                            String type = items.get(viewHolder.getAdapterPosition()).getType();
                            int favorite = items.get(viewHolder.getAdapterPosition()).getFavorite();
                            String result = null;

                            //ICON 선택 해제 상태로 설정
                            view.setSelected(false);
                            viewHolder.isClicked = false;
                            //items 객체에 있는 값도 변경 시켜줘야 현재 전체 viewHolder에 저장된 favorite 개수 알 수 있음
                            items.get(viewHolder.getAdapterPosition()).setFavorite(0);

                            // 웹 서버에 DB 연동 요청 (updateFavorite)
                            try {
                                result = new UpdateFavoriteServer(userID, food, type, favorite).execute().get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (result.equals("success")) {
                                    Toast.makeText(context, "데이터 변경 성공", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "데이터 변경 실패", Toast.LENGTH_SHORT).show();
                                }
                            }

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
