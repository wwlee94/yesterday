package com.example.yesterday.yesterday.UI.HomeFrags;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.EditText.BackPressEditText;
import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.SearchListView.CustomAdapter;
import com.example.yesterday.yesterday.SearchListView.SearchAdapter;
import com.example.yesterday.yesterday.server.BarchartServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


//추가 화면 Fragment
public class AddFragment extends Fragment {

    private ViewGroup rootView;

    private BackPressEditText editSearch;        // 검색어를 입력할 Input 창

    private List<String> searchlist;          // 데이터를 넣은 리스트변수
    private ArrayList<String> searchArrayList;
    private ListView searchListview;                   // 검색을 보여줄 리스트변수    
    private SearchAdapter searchAdapter;               // 리스트뷰에 연결할 아답터


    private List<String> frequentlyFoodList;
    private ArrayList<String> frequentlyArrayList;
    private ListView frequentlyListView;
    private CustomAdapter frequentAdapter;

    private List<String> selectFoodList;
    private ArrayList<String> selectlyArrayList;
    private ListView selectListView;
    private CustomAdapter selectAdapter;

    private RelativeLayout listviewDefault;
    private RelativeLayout listviewSearch;

    InputMethodManager imm;

    public SharedPreferences loginPre;
    //음성인식
    SpeechRecognizer mRecognizer;
    Button voiceInputBtn;
    Intent intent;

    public AddFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //생성자와 onCreateView만 있어도 ok
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_add, container, false);

        editSearch = (BackPressEditText) rootView.findViewById(R.id.editSearch);
        //로그인 정보 가져오기
        loginPre = getActivity().getSharedPreferences("loginSetting",MODE_PRIVATE);
        voiceInputBtn = (Button) rootView.findViewById(R.id.edit_voice_btn);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // 리스트를 생성한다.
        searchlist = new ArrayList<String>();
        frequentlyFoodList = new ArrayList<String>();
        selectFoodList = new ArrayList<String>();

        searchListview = (ListView) rootView.findViewById(R.id.listView);
        frequentlyListView = (ListView) rootView.findViewById(R.id.frequentlyListView);
        selectListView = (ListView) rootView.findViewById(R.id.selectListView);

        searchListview.setVisibility(View.INVISIBLE);

        listviewDefault = (RelativeLayout) rootView.findViewById(R.id.listview_default);
        listviewSearch = (RelativeLayout) rootView.findViewById(R.id.listview_search);

        listviewDefault.setVisibility(View.VISIBLE);
        listviewSearch.setVisibility(View.INVISIBLE);

        // 검색에 사용할 데이터을 미리 저장한다.
        settingsearchArrayList();
        settingfrequentlyArrayList(serverConn());

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        searchArrayList = new ArrayList<String>();
        frequentlyArrayList = new ArrayList<String>();
        selectlyArrayList = new ArrayList<String>();
        searchArrayList.addAll(searchlist);
        frequentlyArrayList.addAll(frequentlyFoodList);
        selectlyArrayList.addAll(selectFoodList);

        // 리스트에 연동될 아답터를 생성한다.
        searchAdapter = new SearchAdapter(searchlist, getActivity());
        frequentAdapter = new CustomAdapter(frequentlyFoodList, getActivity(), true);
        selectAdapter = new CustomAdapter(selectFoodList, getActivity(), false);

        // 리스트뷰에 아답터를 연결한다.
        searchListview.setAdapter(searchAdapter);
        frequentlyListView.setAdapter(frequentAdapter);
        selectListView.setAdapter(selectAdapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchListview.setVisibility(View.VISIBLE);
                    listviewDefault.setVisibility(View.INVISIBLE);
                    listviewSearch.setVisibility(View.VISIBLE);
                } else {
                    searchListview.setVisibility(View.INVISIBLE);
                    listviewSearch.setVisibility(View.INVISIBLE);
                    listviewDefault.setVisibility(View.VISIBLE);
                }
            }
        });

        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    addSelectFoodList(editSearch.getText().toString());
                    editSearch.setText("");
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        editSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editSearch.setFocusable(true);
                editSearch.setFocusableInTouchMode(true);
                return false;
            }
        });

        searchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSelectFoodList(searchlist.get(position));
                editSearch.setText("");
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                editSearch.setFocusable(false);
            }
        });

        frequentlyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSelectFoodList(frequentlyArrayList.get(position));
            }
        });

        selectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFoodList.remove(position);
                selectAdapter.notifyDataSetChanged();
            }
        });

        //음성인식
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                Log.i("voice","voice=========");
                String key = "";
                key = SpeechRecognizer.RESULTS_RECOGNITION;
                ArrayList<String> mResult = results.getStringArrayList(key);
                String[] rs = new String[mResult.size()];
                addSelectFoodList(rs[0]);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
        mRecognizer.setRecognitionListener(listener);


        voiceInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "음성인식", Toast.LENGTH_LONG).show();
                mRecognizer.startListening(intent);
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }



    public void addSelectFoodList(String foodstr) {
        //selectFoodList.clear();
        selectFoodList.add(foodstr);
        selectlyArrayList.addAll(selectFoodList);
        selectAdapter.notifyDataSetChanged();
    }


    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        searchlist.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            searchlist.addAll(searchArrayList);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < searchArrayList.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (searchArrayList.get(i).toLowerCase().contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    searchlist.add(searchArrayList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        searchAdapter.notifyDataSetChanged();
    }

    // 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingsearchArrayList() {
        searchlist.add("abc");
        searchlist.add("bcd");
        searchlist.add("cde");
        searchlist.add("def");
        searchlist.add("하성운");
        searchlist.add("크리스탈");
        searchlist.add("강승윤");
        searchlist.add("손나은");
        searchlist.add("남주혁");
        searchlist.add("루이");
        searchlist.add("진영");
        searchlist.add("슬기");
        searchlist.add("이해인");
        searchlist.add("고원희");
        searchlist.add("설리");
        searchlist.add("공명");
        searchlist.add("김예림");
        searchlist.add("혜리");
        searchlist.add("박혜수");
        searchlist.add("카이");
        searchlist.add("진세연");
        searchlist.add("동호");
        searchlist.add("박세완");
        searchlist.add("도희");
        searchlist.add("창모");
        searchlist.add("허영지");
    }

    public void settingfrequentlyArrayList(String result) {
        String food_name = null;

        try {
            JSONArray jarray = new JSONObject(result).getJSONArray("data");
            for (int i = jarray.length() - 1; i >= 0; i--) {
                JSONObject jObject = jarray.getJSONObject(i);
                food_name = jObject.optString("food");
                frequentlyFoodList.add(food_name);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void settingselectListView() {

    }

    private String serverConn() {
        String result = null;
        try {
            result = new BarchartServer(loginPre.getString("ID","")).execute().get();
        } catch (Exception e) {
            e.getMessage();
        }

        return result;
    }

}
