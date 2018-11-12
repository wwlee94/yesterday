package com.example.yesterday.yesterday.UI.HomeFrags;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yesterday.yesterday.EditText.BackPressEditText;
import com.example.yesterday.yesterday.PushAlarm.AlarmUtils;
import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.SearchListView.ChangeAdapter;
import com.example.yesterday.yesterday.SearchListView.SearchAdapter;
import com.example.yesterday.yesterday.UI.HomeActivity;
import com.example.yesterday.yesterday.server.AddFoodServer;
import com.example.yesterday.yesterday.server.BarchartServer;
import com.example.yesterday.yesterday.server.CheckTypeServer;
import com.example.yesterday.yesterday.server.SetFoodServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


//추가 화면 Fragment
public class AddFragment extends Fragment {

    private ViewGroup rootView;

    // 검색어를 입력할 Input 창
    private BackPressEditText editSearch;

    //검색 메뉴 리스트 뷰를 보여주고 갱신 하기 위한
    private List<String> searchlist;
    private ArrayList<String> searchArrayList;
    private ListView searchListview;
    private SearchAdapter searchAdapter;

    //자주 찾는 메뉴 리스트 뷰를 보여주고 갱신 하기 위한
    private List<String> frequentlyFoodList;
    private ArrayList<String> frequentlyArrayList;
    private ListView frequentlyListView;
    private ChangeAdapter frequentAdapter;

    //고른 메뉴 리스트 뷰를 보여주고 갱신 하기 위한
    private List<String> selectFoodList;
    private ArrayList<String> selectlyArrayList;
    private ListView selectListView;
    private ChangeAdapter selectAdapter;

    //검색창 선택시 다른 리스트 뷰를 보여주기 위해 레이아웃을 2가지로 나눔
    private RelativeLayout listviewDefault;
    private RelativeLayout listviewSearch;

    //소프트 키보드
    InputMethodManager imm;

    //데이트 피커 버튼 , 고른 날짜를 보여줄 텍스트
    private Button dateButton;
    private TextView dateTextView;

    //날짜 저장 년, 월 , 일
    private int year;
    private int month;
    private int day;

    //날짜 고르기 위한 데이트 피커
    DatePickerDialog startDatePickerDialog;

    //현재 시간 불러오기 -> 아침,점심,저녁 을 고르는 라디오 값에 디폴트 값을 주기 위해
    String time;

    //아침 점심 저녁 간식 라디오 버튼 4개
    RadioButton breakfastRadio;
    RadioButton lunchRadio;
    RadioButton dinnerRadio;
    RadioButton snackRadio;
    RadioButton radioButton;
    RadioGroup radioGroup;

    //음식 추가 버튼
    Button addButton;

    SharedPreferences loginPre;
    ArrayList<RecyclerItem> items;
    String value;


    //생성자
    public AddFragment() {
        items = new ArrayList<RecyclerItem>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_add, container, false);

        loginPre = getActivity().getSharedPreferences("loginSetting", MODE_PRIVATE);

        editSearch = (BackPressEditText) rootView.findViewById(R.id.editSearch);
        dateButton = (Button) rootView.findViewById(R.id.datebutton);
        dateTextView = (TextView) rootView.findViewById(R.id.datetextView);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        listviewDefault = (RelativeLayout) rootView.findViewById(R.id.listview_default);
        listviewSearch = (RelativeLayout) rootView.findViewById(R.id.listview_search);
        breakfastRadio = (RadioButton) rootView.findViewById(R.id.breakfastRadio);
        lunchRadio = (RadioButton) rootView.findViewById(R.id.lunchRadio);
        dinnerRadio = (RadioButton) rootView.findViewById(R.id.dinnerRadio);
        snackRadio = (RadioButton) rootView.findViewById(R.id.snackRadio);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        addButton = (Button) rootView.findViewById(R.id.addbutton);

        //디폴트 리스트 뷰를 먼저 보여주고 검색 리스트 뷰는 숨겨 놓는다.
        listviewDefault.setVisibility(View.VISIBLE);
        listviewSearch.setVisibility(View.INVISIBLE);

        //현재 시간을 받아와 라디오 버튼에 눌러 둔다.
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
        time = sdfNow.format(date);

        setRadio(time);


        //현재 날짜를 받아와서 년월일 저장.
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DATE);
        //년월일 텍스트에 업데이트
        updateDate(dateTextView, year, month, day);


        // 리스트를 생성한다.
        searchlist = new ArrayList<String>();
        frequentlyFoodList = new ArrayList<String>();
        selectFoodList = new ArrayList<String>();
        searchListview = (ListView) rootView.findViewById(R.id.listView);
        frequentlyListView = (ListView) rootView.findViewById(R.id.frequentlyListView);
        selectListView = (ListView) rootView.findViewById(R.id.selectListView);
        searchListview.setVisibility(View.INVISIBLE);
        // 검색에 사용할 데이터, 자주찾는 메뉴를  저장한다.
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
        frequentAdapter = new ChangeAdapter(frequentlyFoodList, getActivity(), true);
        selectAdapter = new ChangeAdapter(selectFoodList, getActivity(), false);
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

            // input창에 문자를 입력할때마다 호출된다.
            @Override
            public void afterTextChanged(Editable editable) {
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });


        //검색창에 포커스가 변할때 콜백 리스너
        //1. 검색창에 포커스가 들어오면 검색 리스트 뷰를 보여주고, 기본 디폴트 리스트 뷰는 숨켜준다.
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

        //검색창 키입력 이벤트
        //1. 엔터키 입력시 검색창을 비워 준다
        //2. 입력 받은 텍스트를  선택 리스트 뷰에 보여준다
        //3. 소트프키보드를 내려준다.
        editSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
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

        //검색리스트뷰 에서의 아이템 클릭 리스너
        //1. 검색 리스트 뷰에서 아이템 선택시, 선택된 리스트 뷰로 아이템이 옮겨진다
        searchListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSelectFoodList(searchlist.get(position));
                editSearch.setText("");
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                editSearch.setFocusable(false);
            }
        });

        //자주 찾는메뉴 리스트뷰 에서의 아이템 클릭 리스너
        //1. 자주 찾는 메뉴에서 아이템 선택시 선택 된 리스트 뷰로 아이템이 옮겨진다.
        frequentlyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSelectFoodList(frequentlyArrayList.get(position));
            }
        });

        //선택된 메뉴 리스트뷰 에서의 아이템 클릭 리스너
        //1. 선택된 메뉴에서는 아이템을 선택시 삭제 한다.
        //2. 마지막으로 리스트가 삭제 되면 폭커스가 검색창으로 옮겨짐을 막기 위해 포커스 제거
        //3. 선택 된 음식 제거후, 어댑터 상태 새로고침
        selectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editSearch.setFocusable(false);
                selectFoodList.remove(position);
                selectAdapter.notifyDataSetChanged();
            }
        });

        //데이트 피커 다이어로그 확인 버튼 시 이벤트
        //1. 텍스트 뷰 업데이트
        startDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                updateDate(dateTextView, year, month, dayOfMonth);

            }
        }, year, month, day);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        //음식 추가 버튼 클릭 리스너
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = addServer();
                if (selectFoodList.isEmpty()) {
                    Toast.makeText(getActivity(), "선택된 음식이 없습니다.", Toast.LENGTH_SHORT).show();
                } else if (result.equals("success")) {

                    Toast.makeText(getActivity(), "음식 추가 성공", Toast.LENGTH_SHORT).show();
                    ((HomeActivity) getActivity()).bottomBar.selectTabAtPosition(0);

                    //갱신 한 번 해주고!
                    items = ((HomeActivity) getActivity()).reNewClientGoal();
                    //목표치 80% 넘으면 알림, 실패면 알림
                    for (int x = 0; x < selectFoodList.size(); x++) {
                        for (int i = 0; i < items.size(); i++) {
                            //추가한 음식의 이름과 목표 아이템의 이름이 같다면
                            if (selectFoodList.get(x).equals(items.get(i).getFood())) {
                                if (items.get(i).getType().equals("default")) {
                                    int current = items.get(i).getCurrentCount();
                                    int limit = items.get(i).getCount();
                                    float cautionLimit = limit * ((float) 8 / (float) 10);
                                    //80% 넘겼을 때 한 번
                                    if (Math.ceil(cautionLimit) == current) {
                                        new AlarmUtils(getActivity()).AlarmOverRegister(5000, "주의", items.get(i).getFood());
                                    }
                                    //100% 될 때 한 번
                                    else if (limit == current) {
                                        new AlarmUtils(getActivity()).AlarmOverRegister(5000, "실패", items.get(i).getFood());
                                        try {
                                            value = new CheckTypeServer(loginPre.getString("ID",""),items.get(i).getFood(),items.get(i).getFavorite(),items.get(i).getType(),"fail").execute().get();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }finally {
                                            if(value.equals("success")){
                                                Log.d("CheckTypeServer","음식 추가 후 목표설정치 초과한 음식 타입 변경 완료");
                                                //디비 갱신보다 items 바꿔줌 -> 진행중 목표에 실패한 목표가 뜸 -> 사용자에게 내가 실패한 목록이 뭔지 보여줌
                                                items.get(i).setType("fail");
                                            }
                                            else{
                                                Log.d("CheckTypeServer","음식 추가 후 목표설정치 초과한 음식 타입 변경 실패");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
        String foodstring = null;
        String foodname = null;
        try {
            foodstring = new SetFoodServer().execute().get();
        } catch (Exception e) {
            e.getMessage();
        }

        try {
            JSONArray jarray = new JSONObject(foodstring).getJSONArray("food");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);
                foodname = jObject.optString("food");
                searchlist.add(foodname);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void settingfrequentlyArrayList(String result) {
        String food_name = null;

        try {
            JSONArray jarray = new JSONObject(result).getJSONArray("data");
            for (int i = 0; i < jarray.length(); i++) {
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
            result = new BarchartServer(loginPre.getString("ID", "")).execute().get();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    private String addServer() {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        Date date1 = new Date();
        String currentDate = formatter.format(date1);

        String result = null;
        String foodString = "";
        String date = year + "-" + (month + 1) + "-" + day + " " + currentDate;
        radioButton = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());


        try {
            for (int i = 0; i < selectFoodList.size(); i++)
                foodString += (selectFoodList.get(i) + "-");

            result = new AddFoodServer(loginPre.getString("ID", ""), foodString, radioButton.getText().toString(), date).execute().get();

        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    private void updateDate(TextView view, int Year, int Month, int Day) {
        this.year = Year;
        this.month = Month;
        this.day = Day;
        String str = Year + "년 " + (Month + 1) + "월 " + Day + "일";
        view.setText(str);
    }

    private void setRadio(String Time) {
        int t = Integer.parseInt(Time);
        if (t >= 5 && t <= 10) {
            breakfastRadio.setChecked(true);
        } else if (t >= 11 && t < 15) {
            lunchRadio.setChecked(true);
        } else if (t >= 16 && t <= 21) {
            dinnerRadio.setChecked(true);
        } else {
            snackRadio.setChecked(true);
        }
    }


}
