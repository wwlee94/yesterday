package com.example.yesterday.yesterday.SearchListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    // 문자열을 보관 할 ArrayList
    private Context context;
    private List<String> list;
    private LayoutInflater inflate;
    private Boolean bool;

    // 생성자
    public CustomAdapter(List<String> list, Context context, Boolean bool) {
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
        this.bool = bool;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        TextView text = null;
        ImageView btn = null;
        CustomHolder holder = null;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if (convertView == null) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            if (bool) {
                convertView = inflate.inflate(R.layout.addfood, null);
            } else {
                convertView = inflate.inflate(R.layout.deletefood, null);
            }

            text = (TextView) convertView.findViewById(R.id.text);
            btn = (ImageView) convertView.findViewById(R.id.btn_test);

            // 홀더 생성 및 Tag로 등록
            holder = new CustomHolder();
            holder.m_TextView = text;
            holder.m_Btn = btn;
            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
            text = holder.m_TextView;
            btn = holder.m_Btn;
        }

        // Text 등록
        text.setText(list.get(position));



        return convertView;
    }

    private class CustomHolder {
        TextView m_TextView;
        ImageView m_Btn;
    }

}
