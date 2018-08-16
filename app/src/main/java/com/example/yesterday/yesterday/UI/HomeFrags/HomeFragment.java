package com.example.yesterday.yesterday.UI.HomeFrags;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.example.yesterday.yesterday.R;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.UI.HomeActivity;
import com.example.yesterday.yesterday.UI.HomeViewPager.Chart1Fragment;
import com.example.yesterday.yesterday.UI.HomeViewPager.Chart2Fragment;
import com.example.yesterday.yesterday.UI.HomeViewPager.Chart3Fragment;
import com.example.yesterday.yesterday.UI.TodayMenuActivity;
import com.matthewtamlin.sliding_intro_screen_library.background.BackgroundManager;
import com.matthewtamlin.sliding_intro_screen_library.background.ColorBlender;

import java.util.ArrayList;

//home 화면 Fragment
public class HomeFragment extends Fragment {

    private static final int[] BACKGROUND_COLORS = {0xFF304FFE, 0xFFcc0066, 0xFF9900ff};

    private BackgroundManager backgroundManager = null;

    private ViewGroup rootView;

    private LinearLayout linearLayout;
    //동적 생성하려는 TextView
    private TextView[] textViews;
    private TextView[] textCount;
    //DB에서 값 가져온 recyclerView의 items
    private ArrayList<RecyclerItem> items;
    //즐겨찾기 설정된 items 개수
    private int favoriteCount;
    //즐겨찾기 설정된 아이템의 index (설정된 게 items의 2번째인지 4번째인지)
    private int[] favoriteIndex;

    private ViewPager viewPager = null;
    private ImageView[] imageViews;

    private Button menuButton;

    private Fragment[] arrFragment;

    private Handler handler;
    private setAutoChangeViewPager setAutoChangeViewPager;
    private Thread thread;

    //현재 HomeFragment가 화면에 보이면 isrun :true 안보이면 false
    private boolean isRun;
    //터치 or 드래그 중이면 true 아니면 false
    private boolean isTouched;

    public HomeFragment() {
        // Required empty public constructor

        //chartFragment
        arrFragment = new Fragment[3];
        arrFragment[0] = new Chart1Fragment();
        arrFragment[1] = new Chart2Fragment();
        arrFragment[2] = new Chart3Fragment();

        //indicator
        imageViews = new ImageView[arrFragment.length];
        //
        //textView와 favoriteString는 동적 layout설정 할 때
        items = new ArrayList<RecyclerItem>();
        favoriteCount = 0;

        //핸들러 생성 -> UI 변경하려면 무조건 핸들러
        handler = new Handler();
        //핸들러의 runnable (스레드) 작동 (setAutoChangeViewPager)
        setAutoChangeViewPager = new setAutoChangeViewPager();

        isRun = true;
        isTouched = false;
        //배경화면 colormanager
        configureBackground();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate : Home Fragment");
    }

    //생성자와 onCreateView만 있어도 ok
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //인플레이트(inflate) 한다는 것은 동작 가능한 view의 객체로 생성한다는 의미
        //rootView가 플래그먼트 화면으로 보이게 된다.

        Log.d("TAG", "onCreateView: Home Fragment");

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        //ViewPager
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return arrFragment[position];
            }

            @Override
            public int getCount() {
                return arrFragment.length;
            }
        });
        //페이지가 바뀔 때 이벤트 처리
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (backgroundManager != null) {
                    backgroundManager.updateBackground(viewPager, position, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < arrFragment.length; i++) {
                    if (i == position) {
                        imageViews[i].setBackgroundResource(R.drawable.ic_radio_button_checked_black_24dp);
                    } else {
                        imageViews[i].setBackgroundResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                    }
                }
            }

            //드래그 중이면 자동 페이지 전환 안되도록 떼면 다시 작동
            @Override
            public void onPageScrollStateChanged(int state) {
                //state==0 종료
                if (state == 0) {
                    isTouched = false;
                }
                //state==1 드래그 중
                else if (state == 1) {
                    isTouched = true;
                }
            }
        });
        //* ViewPager Indicator *
        LinearLayout linearLayout = rootView.findViewById(R.id.linear_root);
        //layout_gravity -> center * layout_gravity의 경우 View 자체의 위치를 지정한 위치로 정렬 시키는 것 *
        //생성한 가져온 Gravity -> center * gravity는 View 안의 내용물을 지정한 위치로 정렬 시키는 것 *
        linearLayout.setGravity(Gravity.CENTER);
        //각각의 이미지의 layout 설정할 linearParams 생성
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //이미지들 사이의 간격 좌,우 10씩
        linearParams.setMargins(10, 0, 10, 0);

        //viewPager indicator 초기화 설정
        for (int i = 0; i < arrFragment.length; i++) {
            //해당되는 Activity에서 ImageView 생성
            imageViews[i] = new ImageView(getActivity());
            //imageView의 레이아웃 설정 , 배경화면 설정
            imageViews[i].setLayoutParams(linearParams);
            if (i == 0) {
                //초기 indicator 지정 처음 보이는 ViewPager는 checked 되어 있도록
                imageViews[i].setBackgroundResource(R.drawable.ic_radio_button_checked_black_24dp);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
            linearLayout.addView(imageViews[i]);
        }


        //ViewPager의 mScroller를 이용, 권한 허용하여 원하는 myPager에 duration 설정
        /*
        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, new setScrollerDuration(viewPager.getContext(),sInterpolator,1000));
        }catch(Exception e){ e.printStackTrace(); }
        */

        menuButton = (Button) rootView.findViewById(R.id.menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TodayMenuActivity.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    //자동 스크롤 해주는 스레드 - 현재 위치 기준
    // UI 변경되는 스레드임!!
    public class setAutoChangeViewPager implements Runnable {

        int posit;        //페이지 번호

        public void run() {
            posit = viewPager.getCurrentItem();
            if (posit == 2) {
                posit = 0;
                viewPager.setCurrentItem(posit, true);
            } else {
                posit++;
                viewPager.setCurrentItem(posit, true);
            }
            Log.d("TAG", "**** Thread : setAutoChangeViewPager ****");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //autoChangeviewPager 스레드 재생성
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("TAG", "Thread 시작");
                    while (isRun) {

                        Thread.sleep(5000);
                        //현재 상태가 드래그가 중이면 화면 전환 X
                        if (!isTouched) {
                            //UI 스레드
                            handler.post(setAutoChangeViewPager);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.d("TAG", "Thread 종료");
                    isRun = true;
                }
            }
        });
        thread.start();

        //items 가져옴
        items = ((HomeActivity) getActivity()).getItems();

        favoriteCount = 0;
        //TabTotalFragment의 items 조회
        for (int k = 0; k < 2; k++) {
            //k==0일때 한 번만 실행
            //즐겨찾기로 설정된 목표의 개수를 구하는 코드
            if (k == 0) {
                for (int i = 0; i < items.size(); i++) {
                    Log.d("HomeFragment's itmes", "음식: " + items.get(i).getFood());
                    if (items.get(i).getFavorite() == 1) {
                        favoriteCount++;
                    }
                }
            }
            //k==1일때 한 번만 실행
            //즐겨찾기로 설정된 item의 인덱스를 구하는 코드
            else if (k == 1) {
                Log.d("favoriteCount",""+favoriteCount);
                favoriteIndex = new int[favoriteCount];
                int x = 0;
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getFavorite() == 1) {
                        favoriteIndex[x] = i;
                        x++;
                    }
                }
            }
        }

        //목표 설정 공지란 layout 설정 -> favorite=1 로 즐겨찾기된 목표들 홈 화면에 표기
        linearLayout = rootView.findViewById(R.id.linear_favorite_goal);
        //각각의 이미지의 layout 설정할 linearParams 생성
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //이미지들 사이의 간격
        linearParams.setMargins(0, 4, 0, 0);
        if(favoriteCount==0){
            textViews = new TextView[1];
            textViews[0] = new TextView(getActivity());
            textViews[0].setLayoutParams(linearParams);
            textViews[0].setText("즐겨찾기 된 목표가 없습니다.");
            linearLayout.addView(textViews[0]);
        }
        else {
            //favoriteGoal
            //음식
            textViews = new TextView[favoriteCount];
            //기간+개수
            textCount = new TextView[favoriteCount];

            for (int i = 0; i < favoriteCount; i++) {
                textViews[i] = new TextView(getActivity());
                textCount[i] = new TextView(getActivity());
                textViews[i].setLayoutParams(linearParams);
                textCount[i].setLayoutParams(linearParams);

                textViews[i].setTextColor(Color.parseColor("#FFFFFF"));
                textCount[i].setTextColor(Color.parseColor("#FFFFFF"));
                textViews[i].setText("음식 : " + items.get(favoriteIndex[i]).getFood());

                int current = items.get(favoriteIndex[i]).getCurrentCount();
                int limit = items.get(favoriteIndex[i]).getCount();
                if (((float) current / (float) limit) * 100 >= 70) {
                    textCount[i].setTextColor(Color.parseColor("#FD5523"));
                }
                textCount[i].setText("마감일 : " + items.get(favoriteIndex[i]).getEndDate() + "  "
                        + " 횟수: " + items.get(favoriteIndex[i]).getCurrentCount() + " / " + items.get(favoriteIndex[i]).getCount());

                linearLayout.addView(textViews[i]);
                linearLayout.addView(textCount[i]);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("TAG", "onStop: HomeFragment 실행 중지");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", "onPause: HomeFragment Thread 일시 중지");
        //thread 중지
        isRun = false;
        //view를 제거해줌
        linearLayout.removeAllViewsInLayout();
        //removeAllViews() 를 사용하면 onPause되었을 때 view가 잠깐 사라지는게 보임
    }

    //전역 변수로 backgroundManager 초기화 메소드
    private void configureBackground() {
        this.backgroundManager = new ColorBlender(BACKGROUND_COLORS);
    }

    /*
    public class setScrollerDuration extends Scroller {
        private int mDuration;
        public setScrollerDuration(Context context, Interpolator interpolator, int duration) {
            super(context, interpolator);
            mDuration = duration;
        }
        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
    */

}