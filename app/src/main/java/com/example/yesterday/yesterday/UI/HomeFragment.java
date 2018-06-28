package com.example.yesterday.yesterday.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;

import static android.content.ContentValues.TAG;

//home 화면 Fragment
public class HomeFragment extends Fragment {

    private ViewGroup rootView;
    private Intent intent;
    private Button btn_go;
    private TextView view;

    public HomeFragment() {
        // Required empty public constructor
    }

    //생성자와 onCreateView만 있어도 ok
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //인플레이트(inflate) 한다는 것은 동작 가능한 view의 객체로 생성한다는 의미
        //rootView가 플래그먼트 화면으로 보이게 된다.
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_home,container,false);

        btn_go = (Button)rootView.findViewById(R.id.button);
        view=(TextView)rootView.findViewById(R.id.textview);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setText("hello");
                Log.d(TAG, "onClick ");
                Toast.makeText((HomeActivity)getActivity(), "버튼 클릭 됌", Toast.LENGTH_LONG).show();
                intent = new Intent((HomeActivity)getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }
}
