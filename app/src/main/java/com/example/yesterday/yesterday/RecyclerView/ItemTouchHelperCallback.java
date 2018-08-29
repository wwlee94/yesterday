package com.example.yesterday.yesterday.RecyclerView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;


import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;


import com.example.yesterday.yesterday.R;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final RecyclerViewAdapter mAdapter;

    RectF rectF;
    Context context;

    public static final float ALPHA_FULL = 1.0f;

    public ItemTouchHelperCallback(RecyclerViewAdapter adapter, Context context) {
        mAdapter = adapter;
        this.context = context;

    }

    //RecyclerView에서 드래그 된지 알기 위해서 오버라이드
    @Override
    public boolean isLongPressDragEnabled() {
        return mAdapter.useSwipe;
    }

    //RecyclerView에서 스와이프 된지 알기 위해 오버라이드
    @Override
    public boolean isItemViewSwipeEnabled() { return mAdapter.useSwipe; }

    //현재 어떤 동작을 취했는 지 알려주는 메소드
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        //드래그 = 위,아래
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //스와이프 = 좌,우
        int swipeFlags = ItemTouchHelper.LEFT;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    //Item을 Long Touch하여 다른 위치로 움직였을 때
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        Log.d("TAG", "ItemTouchHelper : onMove");
        return false;
    }

    //스와이프 되었을 때
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        //mAdapter.onItemDelete(mAdapter.getItems().get(position).getUserID(),mAdapter.getItems().get(position).getFood(),position);

        //아이템의 현재 상태를 스와이프 된 상태로 바꿔 ??:도중에 취소해도? ->  해답: 취소했을 때 상태 가져와 적용, 완료했을때 상태 가져와 스와이프 금지
        //TODO: adapter에서 items 가져오는 게아니라 HomeActivity의 items 가져오게
        mAdapter.getItems().get(position).isShowSwiped = true;
        mAdapter.useSwipe=false;
        //adapter에 변경 사항 알려준 뒤 갱신
        mAdapter.notifyItemChanged(position);
    }

    //스와이프하면 background 그리기
    //뒷 배경에 그릴 메소드
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;

        Paint p = new Paint();
        p.setColor(Color.parseColor("#bffa315b"));//#7FD21928

        //왼쪽으로 스와이프 하면
        if (dX < 0) {
            //Draw red background
            //if -> 배경이 itemView 의 왼쪽 틀 가장자리를 넘지 않도록
            if (itemView.getRight() + dX > itemView.getLeft()) {
                rectF = new RectF(itemView.getRight() + (int) dX, itemView.getTop() + 4, itemView.getRight(), itemView.getBottom() - 4);
            } else {
                rectF = new RectF(itemView.getLeft(), itemView.getTop() + 4, itemView.getRight(), itemView.getBottom() - 4);
            }
            c.drawRoundRect(rectF, 10, 10, p);

            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_forever_black_24dp);

            //Draw icon
            int itemHeight = itemView.getBottom() - itemView.getTop();
            //icon의 크기?
            int intrinsicWidth = icon.getIntrinsicWidth();
            int intrinsicHeight = icon.getIntrinsicHeight();

            int iconLeft = itemView.getRight() - intrinsicWidth - 30;
            int iconRight = itemView.getRight() - 30;
            //아이콘 중앙에 위치하려고
            int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int iconBottom = iconTop + intrinsicHeight;
            //itemView.getRight+dX 가 iconRight랑 겹칠 때부터 icon을 그림
            if (itemView.getRight() + (int) dX <= iconRight - intrinsicWidth / 2) {
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            }
            icon.draw(c);
        }
        //itemView Fadeout
        final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
        viewHolder.itemView.setAlpha(alpha);
        //뷰의 x축 변환
        // viewHolder.itemView.setTranslationX(dX);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}