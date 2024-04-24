package com.pnu.b_gosu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.pnu.b_gosu.Database.SubmitDataSource;
import com.pnu.b_gosu.Map.Maps;
import com.pnu.b_gosu.R;
import com.unity3d.player.UnityPlayer;

public class MainPage extends AppCompatActivity{

    private UnityPlayer unityPlayer;
    public Animation fade_in;
    public static double cur_lon;
    public static double cur_lat;

    private static int sCharacterId;
    private static int sMissionIsTrue;
    private static String sMissionName;



    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        TextView wall = findViewById(R.id.mainpage_wall);
        SubmitDataSource dataSource = new SubmitDataSource(MainPage.this);

        dataSource.open();
        int characterId = 0;
        Cursor characterCursor = dataSource.getAllCharacters();

        if (characterCursor != null && characterCursor.moveToFirst()) {
            do {
                // character_id 값을 읽어옵니다.
                characterId = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));
            } while (characterCursor.moveToNext());

            // 사용이 끝나면 Cursor를 닫습니다.
        }
        dataSource.close();

        Log.d("엥",characterId+"");
        int i = getIntent().getIntExtra("key", 0);
        sCharacterId  = getIntent().getIntExtra("characterid",characterId);
        sMissionIsTrue  = getIntent().getIntExtra("missionIsTrue",0);
        sMissionName  = getIntent().getStringExtra("missionName");

        Log.d("승환아",sCharacterId+"//");


        if (i == 1) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.playsound);
            mediaPlayer.start();
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc_current;


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 위치 접근 권한이 이미 허가된 경우
        } else {
            // 위치 접근 권한이 허가되지 않은 경우, 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if they are not granted yet
            return;
        }

        loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc_current == null) {
            Log.d("엄마", "이것 좀 보세요");
            loc_current = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc_current != null)
        {
            cur_lat = loc_current.getLatitude();
            cur_lon = loc_current.getLongitude();
            Log.d("씻",cur_lat + "//" + cur_lon);
        }
        else {
            cur_lat = 35.5383773;
            cur_lon = 129.3113596;
            Toast.makeText(getApplicationContext(), "현재 위치 정보를 받아올 수가 없어요ㅠㅠ", Toast.LENGTH_SHORT).show();
        }

        ConstraintLayout unity_mainframe = findViewById(R.id.constraintlayout_uni);
        ConstraintLayout unityframe = findViewById(R.id.frame_unity);
        unityPlayer = new UnityPlayer(MainPage.this);
        unityframe.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        unityframe.setMinimumHeight(ViewGroup.LayoutParams.MATCH_PARENT);




        Display display = getWindowManager().getDefaultDisplay();
        int deviceWidth = display.getWidth();
        int deviceHeight = display.getHeight();

        // 가로와 높이 조정
        int newWidth1 = deviceWidth - 72;
        int newHeight1 = deviceHeight - 352;

        // mainframe 뷰의 LayoutParams 설정

        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(newWidth1, newHeight1);
        params1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 상단에 맞춤
        params1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // 시작 부분에 맞춤
        params1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // 끝 부분에 맞춤
        params1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params1.verticalBias = 0.5f;
        params1.horizontalBias = 0.5f;
        View mview = unityPlayer.getRootView();

        unityPlayer.requestFocus();
        unityPlayer.windowFocusChanged(true);
        unityframe.addView(mview);
        mview.setLayoutParams(params1);
        statusbarVisibility(true);
        int newWidth = deviceWidth - 20;
        int newHeight = deviceHeight - 300;

        // mainframe 뷰의 LayoutParams 설정
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(newWidth, newHeight);
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 상단에 맞춤
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // 시작 부분에 맞춤
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // 끝 부분에 맞춤
        params.topMargin = (int) dptopx(this, 10);
        params.leftMargin = (int) dptopx(this, 10);
        params.rightMargin = (int) dptopx(this, 10);

        // mainframe 뷰에 LayoutParams 적용
        unity_mainframe.setLayoutParams(params);

        fade_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onAnimationEnd(Animation animation) {
                wall.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });






        TextView btn_map = findViewById(R.id.mainpage_btn_maps);
        TextView btn_credit = findViewById(R.id.mainpage_btn_credit);
        btn_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                    Intent intent = new Intent(MainPage.this, Maps.class);
                    startActivity(intent);
                return false;
            }
        });
        btn_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainPage.this, DeskActivity.class);
                unityframe.removeAllViews();
                startActivity(i);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (unityPlayer != null) {
            unityPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (unityPlayer != null) {
            unityPlayer.quit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unityPlayer != null) {
            unityPlayer.quit();
        }
    }
    public float dptopx(Context context , float dp)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,dm);
    }


    public void statusbarVisibility(boolean setVisibility){
        if(setVisibility){
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            else {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }else{
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            else {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 위치 접근 권한을 승인함.
                } else {
                    // 사용자가 위치 접근 권한 거부.
                }
                return;
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();

        // 여기에 뒤로가기 버튼 이벤트 처리 코드를 작성합니다.
        // 원하는 동작을 구현하십시오.
    }

    public static int GetCharacterId() {
        return sCharacterId;
    }

    public static int GetMissionIsTrue() {
        return sMissionIsTrue;
    }

    public static String GetMissionName() {
        return sMissionName;
    }
}