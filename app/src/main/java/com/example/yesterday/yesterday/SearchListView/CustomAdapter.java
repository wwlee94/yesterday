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

public class CustomAdapter extends BaseAdapter{

    // 문자열을 보관 할 ArrayList
    private Context context;
    private List<String> list;
    private LayoutInflater inflate;
    private Boolean bool;

    // 생성자
    public CustomAdapter(List<String> list, Context context,Boolean bool){
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
        this.bool = bool;
    }

    // 현재 아이템의 수를 리턴
    @Override
    public int getCount() {
        return list.size();
    }

    // 현재 아이템의 오브젝트를 리턴, Object를 상황에 맞게 변경하거나 리턴받은 오브젝트를 캐스팅해서 사용
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // 아이템 position의 ID 값 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        TextView        text    = null;
        ImageView       btn     = null;
        CustomHolder    holder  = null;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if ( convertView == null ) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            if(bool) {
                convertView = inflate.inflate(R.layout.addfood, null);
            }else{
                convertView = inflate.inflate(R.layout.deletefood, null);
            }

            text    = (TextView) convertView.findViewById(R.id.text);
            btn     = (ImageView) convertView.findViewById(R.id.btn_test);

            // 홀더 생성 및 Tag로 등록
            holder = new CustomHolder();
            holder.m_TextView   = text;
            holder.m_Btn        = btn;
            convertView.setTag(holder);
        }
        else {
            holder  = (CustomHolder) convertView.getTag();
            text    = holder.m_TextView;
            btn     = holder.m_Btn;
        }

        // Text 등록
        text.setText(list.get(position));

        // 버튼 이벤트 등록
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 터치 시 해당 아이템 이름 출력
                Toast.makeText(context, list.get(pos), Toast.LENGTH_SHORT).show();
            }
        });

        // 리스트 아이템을 터치 했을 때 이벤트 발생
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 터치 시 해당 아이템 이름 출력
                Toast.makeText(context, "리스트 클릭 : "+list.get(pos), Toast.LENGTH_SHORT).show();
            }
        });

        // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
        convertView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // 터치 시 해당 아이템 이름 출력
                Toast.makeText(context, "리스트 롱 클릭 : "+list.get(pos), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return convertView;
    }

    private class CustomHolder {
        TextView    m_TextView;
        ImageView      m_Btn;
    }

    // 외부에서 아이템 추가 요청 시 사용
    public void add(String _msg) {
        list.add(_msg);
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int _position) {
        list.remove(_position);
    }
}
