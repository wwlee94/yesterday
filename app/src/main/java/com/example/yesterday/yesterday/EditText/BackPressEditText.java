package com.example.yesterday.yesterday.EditText;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.UI.HomeActivity;

public class BackPressEditText extends EditText {
    private BackPressEditText editSearch;

    private OnBackPressListener _listener;

    public BackPressEditText(Context context)
    {
        super(context);
    }


    public BackPressEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public BackPressEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if(((HomeActivity)getContext()).onKeyPreIme(keyCode,event)){

            //이벤트
            editSearch = (BackPressEditText)((HomeActivity) getContext()).findViewById(R.id.editSearch);
//            editSearch.setFocusable(false);
        }
        return super.onKeyPreIme(keyCode, event);
    }


    public void setOnBackPressListener(OnBackPressListener $listener)
    {
        this.clearFocus();
        _listener = $listener;
    }

    public interface OnBackPressListener
    {
        public void onBackPress();
    }

}
