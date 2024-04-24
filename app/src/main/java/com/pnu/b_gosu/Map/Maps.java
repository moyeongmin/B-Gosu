package com.pnu.b_gosu.Map;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContentProviderCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.pnu.b_gosu.Database.SubmitDataSource;
import com.pnu.b_gosu.MainPage;
import com.pnu.b_gosu.ProgressDialog;
import com.pnu.b_gosu.R;
import com.pnu.b_gosu.Retrofit.QuizModel;
import com.pnu.b_gosu.Retrofit.RetrofitInstance;
import com.pnu.b_gosu.Retrofit.TipModel;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener, LocationListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    public static int tmp;
    public static double cur_lat = 129.05562775;
    public static double cur_lon = 35.1379222;
    public static MapPOIItem[] resmarker;
    public static MapPOIItem[] shopmarker;
    public static MapPOIItem[] tradmarker;
    public static MapPOIItem[] playmarker;
    public static MapPOIItem questmarker;
    public static int is_res_marked = 1;
    public static int is_shop_marked = 1;
    public static int is_trad_marked = 1;
    public static int is_play_marked = 1;
    public static int res_called = 0;
    public static int shop_called = 0;
    public static int trad_called = 0;
    public static int play_called = 0;

    public static List<ReceiveModel> result_res = new ArrayList<>();
    public static List<ReceiveModel> result_shop = new ArrayList<>();
    public static List<ReceiveModel> result_tour = new ArrayList<>();
    public static List<ReceiveModel> result_play = new ArrayList<>();
    public static int toastcount = 0;


    @SuppressLint("Range")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapView = new MapView(Maps.this);
        mapView.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mapView.setMinimumHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mapViewContainer = findViewById(R.id.map_view);
        SubmitDataSource dataSource = new SubmitDataSource(this);
        mapView.setMapViewEventListener(this);
        TextView homebtn = findViewById(R.id.maps_btn_home);
        TextView creditbtn = findViewById(R.id.maps_btn_credit);
        ConstraintLayout mainframe = findViewById(R.id.maps_inside_mainframe);
        TextView questbtn = findViewById(R.id.questbtn);
        Display display = getWindowManager().getDefaultDisplay();
        int deviceWidth = display.getWidth();
        int deviceHeight = display.getHeight();

        questmarker = new MapPOIItem();
        questmarker.setItemName("없음");

        // 가로와 높이 조정
        int newWidth1 = deviceWidth - 72;
        int newHeight1 = deviceHeight - 352;



        mapViewContainer.addView(mapView);
        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(newWidth1, newHeight1);
        params1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 상단에 맞춤
        params1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // 시작 부분에 맞춤
        params1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // 끝 부분에 맞춤
        params1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params1.verticalBias = 0.5f;
        params1.horizontalBias = 0.5f;
        mapView.setLayoutParams(params1);
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
        // LocationManager 초기화
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc_current;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc_current != null) {
            cur_lat = loc_current.getLatitude();
            cur_lon = loc_current.getLongitude();
            // 이제 latitude 변수를 사용할 수 있습니다.
        }


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000); //딜레이 타임 조절






        creditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Maps.this, MainPage.class);
                i.putExtra("missionIsTrue", 0);
                i.putExtra("missionName", "default");
                startActivity(i);
            }
        });
        dataSource.open();
        Cursor cursor = dataSource.getAllSubmitData();
        int[] survey = new int[9];

        if (cursor.moveToFirst()) {
            do {
                survey[0] = cursor.getInt(cursor.getColumnIndex("field1"));//ct_tag1
                survey[1] = cursor.getInt(cursor.getColumnIndex("field2"));//ct_tag2
                survey[2] = cursor.getInt(cursor.getColumnIndex("field3"));//ct_tag3
                survey[3] = cursor.getInt(cursor.getColumnIndex("field4"));//ct_tag4
                survey[4] = cursor.getInt(cursor.getColumnIndex("field5"));//r_tag3
                survey[5] = cursor.getInt(cursor.getColumnIndex("field6"));//r_tag4
                survey[6] = cursor.getInt(cursor.getColumnIndex("field7"));//r_tag5
                survey[7] = cursor.getInt(cursor.getColumnIndex("field8"));//r_tag3_1
                survey[8] = cursor.getInt(cursor.getColumnIndex("field9"));//s_tag
                // 필요한 작업 수행
            } while (cursor.moveToNext());
        }

        cursor.close();
        dataSource.close();


        // 가로와 높이 조정
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
        mainframe.setLayoutParams(params);


        questbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questdialog();
            }
        });
        TextView resicon = findViewById(R.id.resicon);
        TextView shopicon = findViewById(R.id.shopicon);
        TextView tradicon = findViewById(R.id.tradicon);
        TextView playicon = findViewById(R.id.playicon);


        tradicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trad_called == 0)
                {

                    ProgressDialog progressDialog = new ProgressDialog(Maps.this);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Call<List<ReceiveModel>> tourcall = RetrofitInstance.getApiService().calltour(cur_lon, cur_lat);
                    tourcall.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            if (response.body() != null) {
                                result_tour = response.body();

                                tradmarker = new MapPOIItem[result_tour.size()];
                                for (int i = 0; i < tradmarker.length; i++) {
                                    tradmarker[i] = new MapPOIItem();
                                    MapPoint tmppoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(result_tour.get(i).getMapy()), Double.parseDouble(result_tour.get(i).getMapx()));
                                    tradmarker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    tradmarker[i].setCustomImageResourceId(R.drawable.mark_tradition_right);
                                    tradmarker[i].setCustomImageAnchor(0.5f, 0.5f);
                                    tradmarker[i].setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0, 0));
                                    tradmarker[i].setMapPoint(tmppoint);
                                    tradmarker[i].setItemName("tradmarker_" + i);
                                    tradmarker[i].setItemName(result_tour.get(i).getTitle() + "#" + result_tour.get(i).getAddr1() + "#" + result_tour.get(i).getFirstimage());
                                    tradmarker[i].setAlpha(1.0f);
                                    tradmarker[i].setCustomCalloutBalloon(getCalloutBalloon(tradmarker[i]));
                                    mapView.addPOIItem(tradmarker[i]);
                                    if(i == (tradmarker.length -1))
                                    {
                                        Handler handler1 = new Handler();
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();

                                            }
                                        },5000);
                                    }


                                }
                                is_trad_marked = 2;
                                tradicon.setBackgroundResource(R.drawable.tradition_btnimg_filled);
                            }


                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });
                    trad_called = 1;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 2000); //딜레이 타임 조절

                }
                if (tradmarker != null) {
                    if (is_trad_marked == 1) {
                        is_trad_marked = 2;
                        tradicon.setBackgroundResource(R.drawable.tradition_btnimg_filled);
                        for (int i = 0; i < tradmarker.length; i++) {
                            if (!tradmarker[i].getItemName().equals(questmarker.getItemName())) {
                                tradmarker[i].setAlpha(1.0f);
                            }
                        }

                    } else if (is_trad_marked == 2) {
                        is_trad_marked = 1;
                        tradicon.setBackgroundResource(R.drawable.tradition_btnimg);
                        for (int i = 0; i < tradmarker.length; i++) {
                            tradmarker[i].setAlpha(0);
                        }


                    }
                }

            }
        });
        playicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(play_called == 0)
                {
                    ProgressDialog progressDialog = new ProgressDialog(Maps.this);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Call<List<ReceiveModel>> playcall = RetrofitInstance.getApiService().callplay(cur_lon, cur_lat);
                    playcall.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            if (response.body() != null) {

                                result_play = response.body();


                                playmarker = new MapPOIItem[result_play.size()];
                                for (int i = 0; i < playmarker.length; i++) {
                                    playmarker[i] = new MapPOIItem();
                                    MapPoint tmppoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(result_play.get(i).getMapy()), Double.parseDouble(result_play.get(i).getMapx()));
                                    playmarker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    playmarker[i].setCustomImageResourceId(R.drawable.mark_play_right);
                                    playmarker[i].setCustomImageAnchor(0.5f, 0.5f);
                                    playmarker[i].setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0, 0));
                                    playmarker[i].setMapPoint(tmppoint);
                                    playmarker[i].setItemName("playmarker" + i);
                                    playmarker[i].setAlpha(1.0f);
                                    playmarker[i].setItemName(result_play.get(i).getTitle() + "#" + result_play.get(i).getAddr1() + "#" + result_play.get(i).getFirstimage());
                                    playmarker[i].setCustomCalloutBalloon(getCalloutBalloon(playmarker[i]));
                                    mapView.addPOIItem(playmarker[i]);
                                    if(i == (playmarker.length -1))
                                    {
                                        Handler handler1 = new Handler();
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();

                                            }
                                        },(resmarker.length)*450);
                                    }


                                }
                                is_play_marked = 2;
                                playicon.setBackgroundResource(R.drawable.play_btnimg_filled);
                            }


                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });



                    play_called = 1;
                }
                if (playmarker != null) {

                    if (is_play_marked == 1) {
                        //여기 클릭하면 이미지가 바뀌고 서버와 통신해서 음식점을 받아와서 마커로 찍어준다
                        is_play_marked = 2;
                        playicon.setBackgroundResource(R.drawable.play_btnimg_filled);


                        for (int i = 0; i < playmarker.length; i++) {
                            if (!playmarker[i].getItemName().equals(questmarker.getItemName())) {
                                playmarker[i].setAlpha(1.0f);
                            }
                        }
                    } else if (is_play_marked == 2) {
                        is_play_marked = 1;
                        playicon.setBackgroundResource(R.drawable.play_btnimg);
                        for (int i = 0; i < playmarker.length; i++) {
                            playmarker[i].setAlpha(0);
                        }


                    }
                }

            }
        });
        shopicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shop_called == 0)
                {
                    ProgressDialog progressDialog = new ProgressDialog(Maps.this);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    Call<List<ReceiveModel>> shopcall = RetrofitInstance.getApiService().callshop(cur_lon, cur_lat);
                    shopcall.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            if (response.body() != null) {
                                result_shop = response.body();


                                shopmarker = new MapPOIItem[result_shop.size()];
                                for (int i = 0; i < shopmarker.length; i++) {
                                    shopmarker[i] = new MapPOIItem();
                                    MapPoint tmppoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(result_shop.get(i).getMapy()), Double.parseDouble(result_shop.get(i).getMapx()));
                                    shopmarker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                    shopmarker[i].setCustomImageResourceId(R.drawable.mark_shopping_right);
                                    shopmarker[i].setCustomImageAnchor(0.5f, 0.5f);
                                    shopmarker[i].setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0, 0));
                                    shopmarker[i].setMapPoint(tmppoint);
                                    shopmarker[i].setAlpha(1.0f);
                                    shopmarker[i].setItemName(result_shop.get(i).getTitle() + "#" + result_shop.get(i).getAddr1() + "#" + result_shop.get(i).getFirstimage());
                                    shopmarker[i].setCustomCalloutBalloon(getCalloutBalloon(shopmarker[i]));
                                    mapView.addPOIItem(shopmarker[i]);
                                    if(i == (shopmarker.length -1))
                                    {
                                        Handler handler1 = new Handler();
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();

                                            }
                                        },(shopmarker.length)*450);
                                    }
                                }
                                is_shop_marked = 2;
                                shopicon.setBackgroundResource(R.drawable.shop_btnimg_filled);
                            }

                            is_shop_marked = 2;
                            shopicon.setBackgroundResource(R.drawable.shop_btnimg_filled);


                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {


                        }
                    });
                    shop_called = 1;

                }
                if (shopmarker != null) {
                    if (is_shop_marked == 1) {
                        //여기 클릭하면 이미지가 바뀌고 서버와 통신해서 음식점을 받아와서 마커로 찍어준다
                        is_shop_marked = 2;
                        shopicon.setBackgroundResource(R.drawable.shop_btnimg_filled);
                        for (int i = 0; i < shopmarker.length; i++) {
                            if (!shopmarker[i].getItemName().equals(questmarker.getItemName())) {
                                shopmarker[i].setAlpha(1.0f);
                            }
                        }

                    } else if (is_shop_marked == 2) {
                        is_shop_marked = 1;
                        shopicon.setBackgroundResource(R.drawable.shop_btnimg);

                        for (int i = 0; i < shopmarker.length; i++) {
                            int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // UI 업데이트 작업 (예: setAlpha)

                                    shopmarker[finalI].setAlpha(0);
                                }
                            });
                        }


                    }

                }
            }
        });
        resicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(res_called == 0)
                {
                    ProgressDialog progressDialog = new ProgressDialog(Maps.this);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    Call<List<ReceiveModel>> rescall = RetrofitInstance.getApiService().callres(cur_lon, cur_lat);
                    rescall.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            result_res = response.body();
                            assert result_res != null;
                            resmarker = new MapPOIItem[result_res.size()];
                            Log.d("엥", "TLqkf" + result_res.size());
                            for (int i = 0; i < resmarker.length; i++) {
                                resmarker[i] = new MapPOIItem();
                                MapPoint tmppoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(result_res.get(i).getMapy()), Double.parseDouble(result_res.get(i).getMapx()));
                                resmarker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                resmarker[i].setCustomImageResourceId(R.drawable.mark_res_right);
                                resmarker[i].setCustomImageAnchor(0.5f, 0.5f);
                                resmarker[i].setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0, 0));
                                resmarker[i].setMapPoint(tmppoint);
                                resmarker[i].setItemName("resmark_" + i);
                                resmarker[i].setAlpha(1.0f);
                                resmarker[i].setItemName(result_res.get(i).getTitle() + "#" + result_res.get(i).getAddr1() + "#" + result_res.get(i).getFirstimage());
                                resmarker[i].setCustomCalloutBalloon(getCalloutBalloon(resmarker[i]));
                                mapView.addPOIItem(resmarker[i]);

                                if(i == (resmarker.length -1))
                                {
                                    Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                           progressDialog.dismiss();

                                        }
                                    },(resmarker.length)*450);
                                }
                            }

                            is_res_marked = 2;
                            resicon.setBackgroundResource(R.drawable.res_btnimg_filled);

                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });

                    res_called = 1;
                }
                if(resmarker!=null) {

                    if (is_res_marked == 1) {
                        //여기 클릭하면 이미지가 바뀌고 서버와 통신해서 음식점을 받아와서 마커로 찍어준다
                        is_res_marked = 2;
                        resicon.setBackgroundResource(R.drawable.res_btnimg_filled);

                        for (int i = 0; i < resmarker.length; i++) {
                            Log.d("???", resmarker[i].getItemName());

                            if (!resmarker[i].getItemName().equals(questmarker.getItemName())) {
                                resmarker[i].setAlpha(1.0f);
                            }
                        }

                    } else if (is_res_marked == 2) {
                        is_res_marked = 1;
                        resicon.setBackgroundResource(R.drawable.res_btnimg);

                        for (int i = 0; i < resmarker.length; i++) {
                            resmarker[i].setAlpha(0);
                        }

                    }
                }
            }
        });

        dataSource.open();

        int characterId = 0;
        Cursor characterCursor = dataSource.getAllCharacters();

        if (characterCursor != null && characterCursor.moveToFirst()) {
            do {
                // character_id 값을 읽어옵니다.
                characterId = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));

                // 여기에서 characterId 값을 사용하거나 출력할 수 있습니다.


            } while (characterCursor.moveToNext());

            // 사용이 끝나면 Cursor를 닫습니다.
        }

        dataSource.close();


        Call<ReceiveModel> questcall = RetrofitInstance.getApiService().challenge_is_exist(characterId);
        questcall.enqueue(new Callback<ReceiveModel>() {
            @Override
            public void onResponse(Call<ReceiveModel> call, Response<ReceiveModel> response) {
                Log.d("여기", String.valueOf(response.body()));
                if (!response.body().toString().equals("")) {
                    questmarker = new MapPOIItem();
                    MapPoint tmppoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(response.body().getMapy()), Double.parseDouble(response.body().getMapx()));


                    questmarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                    questmarker.setCustomImageResourceId(R.drawable.questmark_1);
                    questmarker.setCustomImageAnchor(0.5f, 0.5f);
                    questmarker.setItemName(response.body().getTitle() + "#" + response.body().getAddr1() + "#" + response.body().getFirstimage());
                    questmarker.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0, 0));
                    questmarker.setMapPoint(tmppoint);
                    questmarker.setCustomCalloutBalloon(getCalloutBalloon(questmarker));


                    mapView.addPOIItem(questmarker);


                }

            }

            @Override
            public void onFailure(Call<ReceiveModel> call, Throwable t) {

            }
        });



    }

    public View getCalloutBalloon(MapPOIItem mapPOIItem) {


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View balloonView = inflater.inflate(R.layout.ballon_frame, null);
        balloonView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        balloonView.layout(0, 0, balloonView.getMeasuredWidth(), balloonView.getMeasuredHeight());


        Display display = getWindowManager().getDefaultDisplay();
        int deviceWidth = display.getWidth();
        int deviceHeight = display.getHeight();

        // 가로와 높이 조정
        int newWidth = deviceWidth / 3;
        int newHeight = deviceHeight / 8;


        Log.d("오..ㅋㅋ", balloonView.getWidth() + "//" + balloonView.getHeight());


        // 커스텀 정보 창의 TextView에 itemName 설정
        TextView title = balloonView.findViewById(R.id.balloon_title);
        ImageView image = balloonView.findViewById(R.id.balloon_image);
        TextView addr = balloonView.findViewById(R.id.balloon_address);
        ViewGroup.LayoutParams titleparam = new ViewGroup.LayoutParams(deviceWidth / 5 * 4, deviceHeight / 5);
        title.setLayoutParams(titleparam);
        ViewGroup.LayoutParams imageparam = new ViewGroup.LayoutParams(deviceWidth / 5 * 2, deviceHeight / 2);
        title.setLayoutParams(imageparam);
        ViewGroup.LayoutParams addrparam = new ViewGroup.LayoutParams(deviceWidth / 6 * 2, deviceHeight / 2);
        title.setLayoutParams(addrparam);

        String tmp[] = mapPOIItem.getItemName().split("#");

        if (tmp.length == 2) {

            Request request = new Request.Builder()
                    .url("https://dapi.kakao.com/v2/search/image?query=" + tmp[0])
                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    Maps.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String res = null;
                            try {
                                res = response.body().string();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            final String[] tmp = res.split(",");

                            if (tmp.length > 4) {
                                res = tmp[5].substring(13);
                                String uri = res.substring(0, res.length() - 1);
                                Glide.with(Maps.this)
                                        .load(uri)
                                        .submit();
                            }

                        }
                    });

                }

                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    // 호출 실패 시 처리
                }
            });
        } else {

            String url = tmp[2];

            Maps.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(Maps.this)
                            .load(url)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    image.setImageDrawable(resource); // Set the loaded image in the ImageView.
                                    return true; // Indicate that we've handled setting the resource ourselves.
                                }

                            })
                            .submit();
                }
            });
        }

        addr.setText(tmp[1]);
        title.setText(tmp[0]);

        return balloonView;

    }


    @SuppressLint("Range")
    private void questdialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_quest);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        Display display = getWindowManager().getDefaultDisplay();
        int deviceWidth = display.getWidth();
        int deviceHeight = display.getHeight();
        // 가로와 높이 조정
        int newWidth1 = deviceWidth - 40;
        int newHeight1 = deviceHeight - 200;
        params.gravity = Gravity.CENTER;
        params.width = newWidth1;
        params.height = newHeight1;
        dialog.setCancelable(false);
        dialog.getWindow().setAttributes(params);
        dialog.show();
        ConstraintLayout photoquest_btn = dialog.findViewById(R.id.dialog_quest_photo_btn);
        TextView dialog_exit = dialog.findViewById(R.id.dialog_exit_btn);
        Button dialog_tip_btn = dialog.findViewById(R.id.dialog_quest_tip_btn);
        Button dialog_quest_receive_btn = dialog.findViewById(R.id.dialog_quest_receive_btn);
        ConstraintLayout receive_frame = dialog.findViewById(R.id.dialog_quest_mission_receive_frame);
        ConstraintLayout exist_frame = dialog.findViewById(R.id.dialog_quest_mission_exist_frame);
        TextView exist_title = dialog.findViewById(R.id.dialog_quest_mission_exist_frame_title);
        TextView exist_deatil = dialog.findViewById(R.id.dialog_quest_mission_exist_frame_detail);
        ImageView exist_img = dialog.findViewById(R.id.dialog_quest_mission_exist_frame_image);
        Button discardbtn = dialog.findViewById(R.id.dialog_quest_mission_exist_frame_discard);
        Button authbtn = dialog.findViewById(R.id.dialog_quest_mission_exist_frame_auth);
        TextView photo_text = dialog.findViewById(R.id.dialog_quest_photo_text);
        SubmitDataSource dataSource = new SubmitDataSource(Maps.this);
        dataSource.open();

        int characterId = 0;
        Cursor characterCursor = dataSource.getAllCharacters();

        if (characterCursor != null && characterCursor.moveToFirst()) {
            do {
                // character_id 값을 읽어옵니다.
                characterId = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));

                // 여기에서 characterId 값을 사용하거나 출력할 수 있습니다.


            } while (characterCursor.moveToNext());

            // 사용이 끝나면 Cursor를 닫습니다.
        }

        dataSource.close();
        Call<Integer> photocall = RetrofitInstance.getApiService().is_photo(characterId);
        photocall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.body()!=null) {
                    switch (response.body()) {
                        case 0: {
                            photoquest_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(Maps.this, MainPage.class);
                                    i.putExtra("missionIsTrue", 0);
                                    i.putExtra("missionName", "default");
                                    startActivity(i);
                                }
                            });
                            break;
                        }
                        case 1: {
                            photo_text.setText("오늘은 퀘스트를 완료하셨어요!");
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
        Call<ReceiveModel> call = RetrofitInstance.getApiService().challenge_is_exist(characterId);
        int finalCharacterId = characterId;
        call.enqueue(new Callback<ReceiveModel>() {
            @Override
            public void onResponse(Call<ReceiveModel> call, Response<ReceiveModel> response) {

                if (!Objects.equals(response.body().getMenu(), "")) {
                    receive_frame.setVisibility(View.GONE);
                    exist_frame.setVisibility(View.VISIBLE);
                    exist_title.setText(response.body().getTitle());
                    if (response.body().getFirstimage().equals("")) {

                        Request request = new Request.Builder()
                                .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().getTitle())
                                .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                .build();
                        OkHttpClient client = new OkHttpClient();
                        client.newCall(request).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String res = null;
                                        try {
                                            res = response.body().string();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        final String[] tmp = res.split(",");

                                        res = tmp[5].substring(13);
                                        String uri = res.substring(0, res.length() - 1);

                                        Glide.with(Maps.this)
                                                .load(uri)
                                                .into(exist_img);


                                    }
                                });

                            }
                        });
                    } else {
                        Maps.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                Glide.with(Maps.this)
                                        .load(response.body().getFirstimage())
                                        .into(exist_img);

                            }
                        });


                    }
                    exist_deatil.setText(response.body().getTitle() + " 가서 " + response.body().getMenu() + " 먹어보기 ");
                    discardbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Call<String> call = RetrofitInstance.getApiService().challenge_drop(finalCharacterId, response.body().getTitle());
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {


                                    receive_frame.setVisibility(View.VISIBLE);
                                    exist_frame.setVisibility(View.GONE);
                                    mapView.removePOIItem(questmarker);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });

                        }
                    });

                } else {
                    receive_frame.setVisibility(View.GONE);
                    exist_frame.setVisibility(View.VISIBLE);
                    exist_title.setText(response.body().getTitle());
                    Glide.with(Maps.this).load(response.body().getFirstimage()).into(exist_img);
                    exist_deatil.setText(response.body().getTitle() + " 가보기!");
                    discardbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Call<String> call = RetrofitInstance.getApiService().challenge_drop(finalCharacterId, response.body().getTitle());
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    receive_frame.setVisibility(View.VISIBLE);
                                    exist_frame.setVisibility(View.GONE);
                                    Log.d("확빠", questmarker.getItemName());
                                    mapView.removePOIItem(questmarker);

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ReceiveModel> call, Throwable t) {

            }
        });
        dialog_tip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipdialog();

            }
        });
        dialog_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog_quest_receive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quest_receive_dialog(dialog);

            }
        });
        authbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitDataSource dataSource = new SubmitDataSource(Maps.this);

                dataSource.open();
                int characterId = 0;
                Cursor characterCursor = dataSource.getAllCharacters();

                if (characterCursor != null && characterCursor.moveToFirst()) {
                    do {
                        // character_id 값을 읽어옵니다.
                        characterId = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));

                        // 여기에서 characterId 값을 사용하거나 출력할 수 있습니다.


                    } while (characterCursor.moveToNext());

                    // 사용이 끝나면 Cursor를 닫습니다.
                }

                int finalCharacterId1 = characterId;
                dataSource.close();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Location loc_current;
                loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Call<String> call = RetrofitInstance.getApiService().challenge_check(finalCharacterId1, cur_lon, cur_lat);

                int finalCharacterId2 = characterId;
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body().equals("2")) {
                            authdialog(dialog, (String) exist_title.getText());
                        } else if (response.body().equals("1")) {
                            Intent i = new Intent(Maps.this, MainPage.class);
                            i.putExtra("characterid", finalCharacterId2);
                            i.putExtra("missionIsTrue", 1);
                            i.putExtra("missionName", exist_title.getText());
                            startActivity(i);
                        } else if (response.body().equals("Z")) {
                            Toast.makeText(getApplicationContext(), "미션 위치 근처에서 인증해주세요!", Toast.LENGTH_SHORT).show();
                        } else if (response.body().equals("0")) {
                            receive_frame.setVisibility(View.VISIBLE);
                            exist_frame.setVisibility(View.GONE);
                            Toast.makeText(Maps.this, "미션 성공!", Toast.LENGTH_SHORT).show();
                            mapView.removePOIItem(questmarker);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
        });
    }

    @SuppressLint("Range")
    private void authdialog(Dialog maindialog, String title) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_quest_auth);

        TextView detail = dialog.findViewById(R.id.dialog_auth_text);
        Button yes_btn = dialog.findViewById(R.id.dialog_auth_yes_btn);
        Button no_btn = dialog.findViewById(R.id.dialog_auth_no_btn);
        SubmitDataSource dataSource = new SubmitDataSource(Maps.this);

        dataSource.open();
        int characterId = 0;
        Cursor characterCursor = dataSource.getAllCharacters();

        if (characterCursor != null && characterCursor.moveToFirst()) {
            do {
                // character_id 값을 읽어옵니다.
                characterId = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));

                // 여기에서 characterId 값을 사용하거나 출력할 수 있습니다.


            } while (characterCursor.moveToNext());

            // 사용이 끝나면 Cursor를 닫습니다.
        }

        dataSource.close();

        Call<QuizModel> call1 = RetrofitInstance.getApiService().callquiz(title);
        call1.enqueue(new Callback<QuizModel>() {
            @Override
            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {

                detail.setText(response.body().getQuiz_problem());
                View layout = getLayoutInflater().inflate(R.layout.dialog_quest_check, null);
                TextView toasttxt = layout.findViewById(R.id.dialog_quest_check_text);
                ImageView toastimg = layout.findViewById(R.id.dialog_quest_check_img);

                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (response.body().getQuiz_answer() == 1) {
                            toastimg.setBackgroundResource(R.drawable.failed);
                            toasttxt.setText("아쉽네요..");
                        } else {
                            toastimg.setBackgroundResource(R.drawable.success);
                            toasttxt.setText("퀘스트 성공!");

                        }
                        Toast customToast = new Toast(getApplicationContext());
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setView(layout);
                        customToast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                            }
                        }, 2000);

                    }
                });
                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (response.body().getQuiz_answer() == 0) {
                            toastimg.setBackgroundResource(R.drawable.failed);
                            toasttxt.setText("아쉽네요..");
                        } else {
                            toastimg.setBackgroundResource(R.drawable.success);
                            toasttxt.setText("퀘스트 성공!");

                        }
                        Toast customToast = new Toast(getApplicationContext());
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setView(layout);
                        customToast.setGravity(Gravity.CENTER, 0, 0);
                        customToast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                            }
                        }, 2000);


                    }
                });
            }

            @Override
            public void onFailure(Call<QuizModel> call, Throwable t) {

            }
        });

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) dptopx(this, 400);
        params.height = (int) dptopx(this, 600);
        dialog.getWindow().setAttributes(params);
        dialog.show();


    }

    @SuppressLint("Range")
    private void quest_receive_dialog(Dialog maindialog) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_quest_receive);
        ConstraintLayout mainframe = dialog.findViewById(R.id.dialog_quest_receive_mainframe);
        ConstraintLayout subframe = dialog.findViewById(R.id.dialog_quest_receive_subframe);
        ConstraintLayout suggestframe = dialog.findViewById(R.id.dialog_quest_receive_suggestframe);
        ConstraintLayout suggest_subframe = dialog.findViewById(R.id.suggest_frame);
        ConstraintLayout receive_food = dialog.findViewById(R.id.dialog_quest_receive_food);
        ConstraintLayout receive_shop = dialog.findViewById(R.id.dialog_quest_receive_shopping);
        ConstraintLayout receive_tour = dialog.findViewById(R.id.dialog_quest_receive_tour);
        ConstraintLayout receive_trad = dialog.findViewById(R.id.dialog_quest_receive_trad);
        ConstraintLayout receive_play = dialog.findViewById(R.id.dialog_quest_receive_play);
        ConstraintLayout sub_1 = dialog.findViewById(R.id.dialog_quest_receive_subframe_1);
        ConstraintLayout sub_2 = dialog.findViewById(R.id.dialog_quest_receive_subframe_2);
        ConstraintLayout sub_3 = dialog.findViewById(R.id.dialog_quest_receive_subframe_3);
        TextView sub_1_txt = dialog.findViewById(R.id.dialog_quest_receive_subframe_1_txt);
        TextView sub_2_txt = dialog.findViewById(R.id.dialog_quest_receive_subframe_2_txt);
        TextView sub_3_txt = dialog.findViewById(R.id.dialog_quest_receive_subframe_3_txt);
        TextView suggest_title = dialog.findViewById(R.id.dialog_quest_receive_suggest_title_txt);
        TextView suggest_deatil = dialog.findViewById(R.id.dialog_quest_receive_suggest_detail_txt);
        ImageView suggest_img = dialog.findViewById(R.id.dialog_quest_receive_suggest_img);
        Button suggest_accept = dialog.findViewById(R.id.dialog_quest_receive_suggest_accept);
        Button suggest_reroll = dialog.findViewById(R.id.dialog_quest_receive_suggest_reroll);
        Button noquest_btn = dialog.findViewById(R.id.dialog_quest_noquest_btn);
        ConstraintLayout noquest = dialog.findViewById(R.id.dialog_quest_noquest_frame);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        tmp = 0;
        SubmitDataSource dataSource = new SubmitDataSource(Maps.this);

        dataSource.open();
        int characterId = 0;
        Cursor characterCursor = dataSource.getAllCharacters();

        if (characterCursor != null && characterCursor.moveToFirst()) {
            do {
                // character_id 값을 읽어옵니다.
                characterId = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));

                // 여기에서 characterId 값을 사용하거나 출력할 수 있습니다.


            } while (characterCursor.moveToNext());

            // 사용이 끝나면 Cursor를 닫습니다.
        }

        int finalCharacterId1 = characterId;
        noquest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {


                ConstraintLayout receive_frame = maindialog.findViewById(R.id.dialog_quest_mission_receive_frame);
                ConstraintLayout exist_frame = maindialog.findViewById(R.id.dialog_quest_mission_exist_frame);
                TextView exist_title = maindialog.findViewById(R.id.dialog_quest_mission_exist_frame_title);
                TextView exist_deatil = maindialog.findViewById(R.id.dialog_quest_mission_exist_frame_detail);
                ImageView exist_img = maindialog.findViewById(R.id.dialog_quest_mission_exist_frame_image);
                Call<ReceiveModel> call = RetrofitInstance.getApiService().challenge_is_exist(finalCharacterId1);
                call.enqueue(new Callback<ReceiveModel>() {
                    @Override
                    public void onResponse(Call<ReceiveModel> call, Response<ReceiveModel> response) {

                        if (!Objects.equals(response.body().getTitle(), "")) {

                            receive_frame.setVisibility(View.GONE);
                            exist_frame.setVisibility(View.VISIBLE);
                            exist_title.setText(response.body().getTitle());
                            Glide.with(Maps.this).load(response.body().getFirstimage()).into(exist_img);

                            if (response.body().getFirstimage().equals("")) {
                                Request request = new Request.Builder()
                                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().getTitle())
                                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String res = null;
                                                try {
                                                    res = response.body().string();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                final String[] tmp = res.split(",");

                                                res = tmp[5].substring(13);
                                                String uri = res.substring(0, res.length() - 1);

                                                Glide.with(Maps.this)
                                                        .load(uri)
                                                        .into(exist_img);


                                            }
                                        });

                                    }
                                });
                            } else {
                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(Maps.this)
                                                .load(response.body().getFirstimage())
                                                .into(exist_img);
                                    }
                                });
                            }
                            if (response.body().getMenu().equals("")) {
                                exist_deatil.setText(response.body().getTitle() + " 가보기! ");
                            }else {
                                exist_deatil.setText(response.body().getTitle() + " 가서 " + response.body().getMenu() + " 먹어보기 ");
                        }
                        }


                    }

                    @Override
                    public void onFailure(Call<ReceiveModel> call, Throwable t) {

                    }
                });


                //여기까지


            }
        });
        dataSource.close();
        params.width = (int) dptopx(this, 400);
        params.height = (int) dptopx(this, 600);
        dialog.getWindow().setAttributes(params);
        dialog.show();
        suggest_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Call<String> call = RetrofitInstance.getApiService().challenge_accept(finalCharacterId1, (String) suggest_title.getText());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                        Call<ReceiveModel> questcall = RetrofitInstance.getApiService().challenge_is_exist(finalCharacterId1);
                        questcall.enqueue(new Callback<ReceiveModel>() {
                            @Override
                            public void onResponse(Call<ReceiveModel> call, Response<ReceiveModel> response) {
                                questmarker = new MapPOIItem();
                                MapPoint tmppoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(response.body().getMapy()), Double.parseDouble(response.body().getMapx()));


                                questmarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                                questmarker.setCustomImageResourceId(R.drawable.questmark_1);
                                questmarker.setCustomImageAnchor(0.5f, 0.5f);
                                questmarker.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0, 0));
                                questmarker.setMapPoint(tmppoint);
                                questmarker.setItemName("questmarker");
                                questmarker.setItemName(response.body().getTitle() + "#" + response.body().getAddr1() + "#" + response.body().getFirstimage());
                                questmarker.setCustomCalloutBalloon(getCalloutBalloon(questmarker));
                                mapView.addPOIItem(questmarker);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ReceiveModel> call, Throwable t) {


                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {


                    }
                });


            }
        });

        receive_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainframe.setVisibility(View.GONE);
                subframe.setVisibility(View.VISIBLE);
                sub_2.setVisibility(View.GONE);
                sub_1_txt.setText("식당");
                sub_3_txt.setText("편의점");
                tmp = 1;

            }
        });
        int finalCharacterId = characterId;

        sub_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tmp = 1 은 식당 / 편의점의 선택지인 경우
                if (tmp == 1) {
                    tmp = 2;
                    sub_1_txt.setText("음식점");
                    sub_3_txt.setText("디저트");


                    //식당

                } else if (tmp == 2) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc_current;
                    loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc_current != null) {
                        cur_lat = loc_current.getLatitude();
                        cur_lon = loc_current.getLongitude();
                        // 이제 latitude 변수를 사용할 수 있습니다.
                    }
                    else Log.d("우와아아아","여기에요");
                    //음식점 골랐어
                    Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_res_quest(
                            cur_lon,
                            cur_lat,
                            "1",
                            "1",
                            finalCharacterId);
                    call.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            subframe.setVisibility(View.INVISIBLE);
                            suggestframe.setVisibility(View.VISIBLE);
                            int[] i = {0};
                            Log.d("여기",response.toString());
                            if (response.body() != null) {
                                i = new int[]{response.body().size()};
                            }

                            if (response.body() == null) {
                                suggest_subframe.setVisibility(View.GONE);
                                noquest.setVisibility(View.VISIBLE);
                            } else if (response.body().get(0).getFirstimage().equals("")) {

                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가서 " + response.body().get(0).getMenu() + "먹어보기");

                                Request request = new Request.Builder()
                                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String res = null;
                                                try {
                                                    res = response.body().string();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                final String[] tmp = res.split(",");

                                                res = tmp[5].substring(13);
                                                String uri = res.substring(0, res.length() - 1);

                                                Glide.with(Maps.this)
                                                        .load(uri)
                                                        .into(suggest_img);


                                            }
                                        });

                                    }
                                });
                            } else {

                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가서 " + response.body().get(0).getMenu() + "먹어보기");

                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Glide.with(Maps.this)
                                                .load(response.body().get(0).getFirstimage())
                                                .into(suggest_img);

                                    }
                                });


                            }


                            int[] finalI = i;
                            suggest_reroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    finalI[0]--;

                                    if (finalI[0] == 0) {

                                        suggest_subframe.setVisibility(View.GONE);
                                        noquest.setVisibility(View.VISIBLE);
                                    } else {
                                        suggest_title.setText(response.body().get((response.body().size() - finalI[0])).getTitle());
                                        suggest_deatil.setText(response.body().get((response.body().size() - finalI[0])).getTitle() + "에 가서 " + response.body().get((response.body().size() - finalI[0])).getMenu() + "먹어보기");


                                        if (response.body().get((response.body().size() - finalI[0])).getFirstimage().equals("")) {
                                            String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();


                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                    .build();
                                            OkHttpClient client = new OkHttpClient();
                                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {


                                                }

                                                @Override
                                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                    Maps.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String res = null;
                                                            try {
                                                                res = response.body().string();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            final String[] tmp = res.split(",");
                                                            res = tmp[5].substring(13);
                                                            String uri = res.substring(0, res.length() - 1);

                                                            Glide.with(Maps.this)
                                                                    .load(uri)
                                                                    .into(suggest_img);


                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            Maps.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {


                                                    Glide.with(Maps.this)
                                                            .load(response.body().get((response.body().size() - finalI[0])).getFirstimage())
                                                            .into(suggest_img);

                                                }
                                            });


                                        }

                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });
                } else if (tmp == 3)//인생네컷 골랐을 때
                {


                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc_current;
                    loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc_current != null) {
                        cur_lat = loc_current.getLatitude();
                        cur_lon = loc_current.getLongitude();
                        // 이제 latitude 변수를 사용할 수 있습니다.
                    }
                    Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_play_quest(
                            cur_lon,
                            cur_lat,
                            "1",
                            finalCharacterId);
                    call.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            subframe.setVisibility(View.INVISIBLE);
                            suggestframe.setVisibility(View.VISIBLE);
                            int[] i = {0};
                            if (response.body() != null) {
                                i = new int[]{response.body().size()};
                            }

                            if (response.body() == null) {
                                suggest_subframe.setVisibility(View.GONE);
                                noquest.setVisibility(View.VISIBLE);
                            } else if (response.body().get(0).getFirstimage().equals("")) {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Request request = new Request.Builder()
                                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String res = null;
                                                try {
                                                    res = response.body().string();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                final String[] tmp = res.split(",");
                                                if (tmp.length > 4) {
                                                    res = tmp[5].substring(13);
                                                    String uri = res.substring(0, res.length() - 1);

                                                    Glide.with(Maps.this)
                                                            .load(uri)
                                                            .into(suggest_img);
                                                }


                                            }
                                        });

                                    }
                                });
                            } else {

                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Glide.with(Maps.this)
                                                .load(response.body().get(0).getFirstimage())
                                                .into(suggest_img);

                                    }
                                });


                            }

                            int[] finalI = i;
                            suggest_reroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finalI[0]--;
                                    if (finalI[0] == 0) {
                                        suggest_subframe.setVisibility(View.GONE);
                                        noquest.setVisibility(View.VISIBLE);
                                    } else {
                                        suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                        suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가서 " + response.body().get(((response.body().size() - finalI[0]))).getMenu() + "먹어보기");


                                        if (response.body().get((response.body().size() - finalI[0])).getFirstimage().equals("")) {
                                            String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get(response.body().size() - finalI[0]).getTitle();

                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                    .build();
                                            OkHttpClient client = new OkHttpClient();
                                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                                }

                                                @Override
                                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                    Maps.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String res = null;
                                                            try {
                                                                res = response.body().string();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            final String[] tmp = res.split(",");
                                                            if (tmp.length > 4) {
                                                                res = tmp[5].substring(13);
                                                                String uri = res.substring(0, res.length() - 1);


                                                                Glide.with(Maps.this)
                                                                        .load(uri)
                                                                        .into(suggest_img);
                                                            }


                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            Maps.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {


                                                    Glide.with(Maps.this)
                                                            .load(response.body().get((response.body().size() - finalI[0])).getFirstimage())
                                                            .into(suggest_img);

                                                }
                                            });


                                        }

                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {


                        }
                    });

                    //인생네컷

                }

            }
        });
        sub_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//방탈출

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location loc_current;
                loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc_current != null) {
                    cur_lat = loc_current.getLatitude();
                    cur_lon = loc_current.getLongitude();
                    // 이제 latitude 변수를 사용할 수 있습니다.
                }

                Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_play_quest(
                        cur_lon,
                        cur_lat,
                        "3",
                        finalCharacterId);
                call.enqueue(new Callback<List<ReceiveModel>>() {
                    @Override
                    public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {

                        subframe.setVisibility(View.INVISIBLE);
                        suggestframe.setVisibility(View.VISIBLE);
                        int[] i = {0};
                        if (response.body() != null) {
                            i = new int[]{response.body().size()};
                        }

                        if (response.body() == null) {
                            suggest_subframe.setVisibility(View.GONE);
                            noquest.setVisibility(View.VISIBLE);
                        } else if (response.body().get(0).getFirstimage().equals("")) {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");

                            Request request = new Request.Builder()
                                    .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                    .build();
                            OkHttpClient client = new OkHttpClient();
                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                }

                                @Override
                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                    Maps.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String res = null;
                                            try {
                                                res = response.body().string();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            final String[] tmp = res.split(",");
                                            if (tmp.length > 4) {
                                                res = tmp[5].substring(13);
                                                String uri = res.substring(0, res.length() - 1);

                                                Glide.with(Maps.this)
                                                        .load(uri)
                                                        .into(suggest_img);
                                            }


                                        }
                                    });

                                }
                            });
                        } else {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Maps.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Glide.with(Maps.this)
                                            .load(response.body().get(0).getFirstimage())
                                            .into(suggest_img);

                                }
                            });


                        }

                        int[] finalI = i;
                        suggest_reroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finalI[0]--;
                                if (finalI[0] == 0) {
                                    suggest_subframe.setVisibility(View.GONE);
                                    noquest.setVisibility(View.VISIBLE);
                                } else {
                                    suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                    suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가서 " + response.body().get(((response.body().size() - finalI[0]))).getMenu() + "먹어보기");


                                    if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                        String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();

                                        Request request = new Request.Builder()
                                                .url(url)
                                                .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                .build();
                                        OkHttpClient client = new OkHttpClient();
                                        client.newCall(request).enqueue(new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                            }

                                            @Override
                                            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                Maps.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String res = null;
                                                        try {
                                                            res = response.body().string();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        final String[] tmp = res.split(",");
                                                        if (tmp.length > 4) {
                                                            res = tmp[5].substring(13);

                                                            String uri = res.substring(0, res.length() - 1);


                                                            Glide.with(Maps.this)
                                                                    .load(uri)
                                                                    .into(suggest_img);
                                                        }


                                                    }
                                                });

                                            }
                                        });
                                    } else {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                Glide.with(Maps.this)
                                                        .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                        .into(suggest_img);

                                            }
                                        });


                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                    }
                });

                //방탈출

            }
        });
        sub_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tmp = 1인 경우에 식당/편의점에서 편의점을 고른 경우
                if (tmp == 1) {
                    //편의점일 경우는 통신
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc_current;
                    loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc_current != null) {
                        cur_lat = loc_current.getLatitude();
                        cur_lon = loc_current.getLongitude();
                        // 이제 latitude 변수를 사용할 수 있습니다.
                    }
                    Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_res_quest(
                            cur_lon,
                            cur_lat,
                            "2",
                            "",
                            finalCharacterId);
                    call.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            subframe.setVisibility(View.INVISIBLE);
                            suggestframe.setVisibility(View.VISIBLE);
                            int[] i = {0};
                            if (response.body() != null) {
                                i = new int[]{response.body().size()};
                            }

                            if (response.body() == null) {
                                suggest_subframe.setVisibility(View.GONE);
                                noquest.setVisibility(View.VISIBLE);
                            } else if (response.body().get(0).getFirstimage().equals("")) {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Request request = new Request.Builder()
                                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String res = null;
                                                try {
                                                    res = response.body().string();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                final String[] tmp = res.split(",");

                                                if (tmp.length > 4) {
                                                    res = tmp[5].substring(13);

                                                    String uri = res.substring(0, res.length() - 1);

                                                    Glide.with(Maps.this)
                                                            .load(uri)
                                                            .into(suggest_img);
                                                }


                                            }
                                        });

                                    }
                                });
                            } else {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Glide.with(Maps.this)
                                                .load(response.body().get(0).getFirstimage())
                                                .into(suggest_img);

                                    }
                                });


                            }


                            int[] finalI = i;
                            suggest_reroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finalI[0]--;

                                    if (finalI[0] == 0) {
                                        suggest_subframe.setVisibility(View.GONE);
                                        noquest.setVisibility(View.VISIBLE);
                                    } else {
                                        suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                        suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가서 " + response.body().get(((response.body().size() - finalI[0]))).getMenu() + "먹어보기");


                                        if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                            String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();

                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                    .build();
                                            OkHttpClient client = new OkHttpClient();
                                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                                }

                                                @Override
                                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                    Maps.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String res = null;
                                                            try {
                                                                res = response.body().string();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            final String[] tmp = res.split(",");

                                                            if (tmp.length > 4) {
                                                                res = tmp[5].substring(13);

                                                                String uri = res.substring(0, res.length() - 1);


                                                                Glide.with(Maps.this)
                                                                        .load(uri)
                                                                        .into(suggest_img);
                                                            }


                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            Maps.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {


                                                    Glide.with(Maps.this)
                                                            .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                            .into(suggest_img);

                                                }
                                            });


                                        }

                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });


                    //편의점
                } else if (tmp == 2) {
                    //너 지금 되게 디저트야
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc_current;
                    loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc_current != null) {
                        cur_lat = loc_current.getLatitude();
                        cur_lon = loc_current.getLongitude();
                        // 이제 latitude 변수를 사용할 수 있습니다.
                    }
                    Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_res_quest(
                            cur_lon,
                            cur_lat,
                            "1",
                            "2",
                            finalCharacterId);
                    call.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            subframe.setVisibility(View.INVISIBLE);
                            suggestframe.setVisibility(View.VISIBLE);
                            int[] i = {0};
                            if (response.body() != null) {
                                i = new int[]{response.body().size()};
                            }
                            if (response.body() == null) {

                                suggest_subframe.setVisibility(View.GONE);
                                noquest.setVisibility(View.VISIBLE);
                            } else if (response.body().get(0).getFirstimage().equals("")) {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");

                                Request request = new Request.Builder()
                                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {


                                    }

                                    @Override
                                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String res = null;
                                                try {
                                                    res = response.body().string();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                final String[] tmp = res.split(",");


                                                if (tmp.length > 4) {
                                                    res = tmp[5].substring(13);

                                                    String uri = res.substring(0, res.length() - 1);


                                                    Glide.with(Maps.this)
                                                            .load(uri)
                                                            .into(suggest_img);
                                                }


                                            }
                                        });

                                    }
                                });
                            } else {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Glide.with(Maps.this)
                                                .load(response.body().get(0).getFirstimage())
                                                .into(suggest_img);

                                    }
                                });


                            }


                            int[] finalI = i;
                            suggest_reroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finalI[0]--;

                                    if (finalI[0] == 0) {

                                        suggest_subframe.setVisibility(View.GONE);
                                        noquest.setVisibility(View.VISIBLE);
                                    } else {
                                        suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                        suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가서 " + response.body().get(((response.body().size() - finalI[0]))).getMenu() + "먹어보기");


                                        if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                            String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();
                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                    .build();
                                            OkHttpClient client = new OkHttpClient();
                                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {


                                                }

                                                @Override
                                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                    Maps.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String res = null;
                                                            try {
                                                                res = response.body().string();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }

                                                            final String[] tmp = res.split(",");

                                                            if (tmp.length > 4) {
                                                                res = tmp[5].substring(13);

                                                                String uri = res.substring(0, res.length() - 1);


                                                                Glide.with(Maps.this)
                                                                        .load(uri)
                                                                        .into(suggest_img);
                                                            }


                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            Maps.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {


                                                    Glide.with(Maps.this)
                                                            .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                            .into(suggest_img);

                                                }
                                            });


                                        }

                                    }
                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });

                    //디저트
                } else if (tmp == 3) {
                    //오락실
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location loc_current;
                    loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc_current != null) {
                        cur_lat = loc_current.getLatitude();
                        cur_lon = loc_current.getLongitude();
                        // 이제 latitude 변수를 사용할 수 있습니다.
                    }
                    Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_play_quest(
                            cur_lon,
                            cur_lat,
                            "2",
                            finalCharacterId);
                    call.enqueue(new Callback<List<ReceiveModel>>() {
                        @Override
                        public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                            subframe.setVisibility(View.INVISIBLE);
                            suggestframe.setVisibility(View.VISIBLE);
                            int[] i = {0};
                            if (response.body() != null) {
                                i = new int[]{response.body().size()};
                            }
                            if (response.body() == null) {
                                suggest_subframe.setVisibility(View.GONE);
                                noquest.setVisibility(View.VISIBLE);
                            } else if (response.body().get(0).getFirstimage().equals("")) {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Request request = new Request.Builder()
                                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                    }

                                    @Override
                                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String res = null;
                                                try {
                                                    res = response.body().string();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                final String[] tmp = res.split(",");
                                                if (tmp.length > 4) {
                                                    res = tmp[5].substring(13);

                                                    String uri = res.substring(0, res.length() - 1);
                                                    Glide.with(Maps.this)
                                                            .load(uri)
                                                            .into(suggest_img);
                                                }


                                            }
                                        });

                                    }
                                });
                            } else {
                                suggest_title.setText(response.body().get(0).getTitle());
                                suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                                Maps.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        Glide.with(Maps.this)
                                                .load(response.body().get(0).getFirstimage())
                                                .into(suggest_img);

                                    }
                                });


                            }


                            int[] finalI = i;
                            suggest_reroll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finalI[0]--;

                                    if (finalI[0] == 0) {
                                        suggest_subframe.setVisibility(View.GONE);
                                        noquest.setVisibility(View.VISIBLE);
                                    } else {
                                        suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                        suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가서 " + response.body().get(((response.body().size() - finalI[0]))).getMenu() + "먹어보기");
                                        if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                            String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();
                                            Request request = new Request.Builder()
                                                    .url(url)
                                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                    .build();
                                            OkHttpClient client = new OkHttpClient();
                                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                                @Override
                                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                                }

                                                @Override
                                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                    Maps.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String res = null;
                                                            try {
                                                                res = response.body().string();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            final String[] tmp = res.split(",");
                                                            if (tmp.length > 4) {
                                                                res = tmp[5].substring(13);

                                                                String uri = res.substring(0, res.length() - 1);


                                                                Glide.with(Maps.this)
                                                                        .load(uri)
                                                                        .into(suggest_img);
                                                            }


                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            Maps.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {


                                                    Glide.with(Maps.this)
                                                            .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                            .into(suggest_img);

                                                }
                                            });


                                        }

                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                        }
                    });


                    //오락실

                }

            }
        });
        receive_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainframe.setVisibility(View.GONE);
                subframe.setVisibility(View.VISIBLE);
                sub_2.setVisibility(View.VISIBLE);
                sub_1_txt.setText("인생 네컷");
                sub_2_txt.setText("방탈출");
                sub_3_txt.setText("오락실");

                tmp = 3;
            }
        });
        receive_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainframe.setVisibility(View.GONE);
                suggestframe.setVisibility(View.VISIBLE);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location loc_current;
                loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc_current != null) {
                    cur_lat = loc_current.getLatitude();
                    cur_lon = loc_current.getLongitude();
                    // 이제 latitude 변수를 사용할 수 있습니다.
                }
                Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_shopping_quest(
                        cur_lon,
                        cur_lat,
                        finalCharacterId);
                call.enqueue(new Callback<List<ReceiveModel>>() {
                    @Override
                    public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                        subframe.setVisibility(View.INVISIBLE);
                        suggestframe.setVisibility(View.VISIBLE);
                        int[] i = {0};
                        if (response.body() != null) {
                            i = new int[]{response.body().size()};
                        }
                        if (response.body() == null) {
                            suggest_subframe.setVisibility(View.GONE);
                            noquest.setVisibility(View.VISIBLE);
                        } else if (response.body().get(0).getFirstimage().equals("")) {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Request request = new Request.Builder()
                                    .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                    .build();
                            OkHttpClient client = new OkHttpClient();
                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                    Maps.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String res = null;
                                            try {
                                                res = response.body().string();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            final String[] tmp = res.split(",");
                                            if (tmp.length > 4) {
                                                res = tmp[5].substring(13);

                                                String uri = res.substring(0, res.length() - 1);

                                                Glide.with(Maps.this)
                                                        .load(uri)
                                                        .into(suggest_img);
                                            }


                                        }
                                    });

                                }
                            });
                        } else {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Maps.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Glide.with(Maps.this)
                                            .load(response.body().get(0).getFirstimage())
                                            .into(suggest_img);

                                }
                            });


                        }


                        int[] finalI = i;
                        suggest_reroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finalI[0]--;

                                if (finalI[0] == 0) {
                                    suggest_subframe.setVisibility(View.GONE);
                                    noquest.setVisibility(View.VISIBLE);
                                } else {
                                    suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                    suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가보기!");

                                    if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                        String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();
                                        Request request = new Request.Builder()
                                                .url(url)
                                                .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                .build();
                                        OkHttpClient client = new OkHttpClient();
                                        client.newCall(request).enqueue(new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                            }

                                            @Override
                                            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                Maps.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String res = null;
                                                        try {
                                                            res = response.body().string();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        final String[] tmp = res.split(",");
                                                        if (tmp.length > 4) {
                                                            res = tmp[5].substring(13);

                                                            String uri = res.substring(0, res.length() - 1);


                                                            Glide.with(Maps.this)
                                                                    .load(uri)
                                                                    .into(suggest_img);
                                                        }


                                                    }
                                                });

                                            }
                                        });
                                    } else {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                Glide.with(Maps.this)
                                                        .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                        .into(suggest_img);

                                            }
                                        });


                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                    }
                });


            }
        });
        receive_tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainframe.setVisibility(View.GONE);
                suggestframe.setVisibility(View.VISIBLE);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location loc_current;
                loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(loc_current != null) {
                    if (loc_current != null) {
                        cur_lat = loc_current.getLatitude();
                        cur_lon = loc_current.getLongitude();
                        // 이제 latitude 변수를 사용할 수 있습니다.
                    }
                }
                else {
                    cur_lat =  35.1379222;
                    cur_lon = 129.05562775;
                }

                Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_tour_quest(
                        cur_lon,
                        cur_lat,
                        finalCharacterId);
                call.enqueue(new Callback<List<ReceiveModel>>() {
                    @Override
                    public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {

                        subframe.setVisibility(View.INVISIBLE);
                        suggestframe.setVisibility(View.VISIBLE);
                        int[] i = {0};
                        if (response.body() != null) {
                            i = new int[]{response.body().size()};
                        }

                        if (response.body() == null) {
                            suggest_subframe.setVisibility(View.GONE);
                            noquest.setVisibility(View.VISIBLE);
                        } else if (response.body().get(0).getFirstimage().equals("")) {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Request request = new Request.Builder()
                                    .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                    .build();
                            OkHttpClient client = new OkHttpClient();
                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                    Log.d("aarevadsdf", call.toString() + "\n" + e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                    Maps.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String res = null;
                                            try {
                                                res = response.body().string();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Log.d("왜왜왜왜ㅗ애ㅐadsㅗ왜애", res);
                                            final String[] tmp = res.split(",");

                                            Log.d("왜왜왜왜ㅗ애ㅐㅗ왜애", tmp[3]);
                                            if (tmp.length > 4) {
                                                res = tmp[5].substring(13);

                                                String uri = res.substring(0, res.length() - 1);
                                                Log.i("zzz", "a");

                                                Glide.with(Maps.this)
                                                        .load(uri)
                                                        .into(suggest_img);
                                            }


                                        }
                                    });

                                }
                            });
                        } else {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Maps.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Glide.with(Maps.this)
                                            .load(response.body().get(0).getFirstimage())
                                            .into(suggest_img);

                                }
                            });


                        }

                        int[] finalI = i;
                        suggest_reroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finalI[0]--;

                                if (finalI[0] == 0) {
                                    Log.d("씨발", "ㅋㅋㅋㅋ");
                                    suggest_subframe.setVisibility(View.GONE);
                                    noquest.setVisibility(View.VISIBLE);
                                } else {
                                    suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                    suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가보기!");


                                    Log.d("asdfasdf", response.body().get((response.body().size() - finalI[0])).getFirstimage() + "");

                                    if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                        String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();
                                        Log.d("asdfsfaeanrobobmoafmiof", url);
                                        Request request = new Request.Builder()
                                                .url(url)
                                                .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                .build();
                                        OkHttpClient client = new OkHttpClient();
                                        client.newCall(request).enqueue(new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                                Log.d("adfmaeobninbadnio", call.toString() + "\n" + e.getMessage() + "\n" + e.toString());

                                            }

                                            @Override
                                            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                Maps.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String res = null;
                                                        try {
                                                            res = response.body().string();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Log.d("왜왜왜왜ㅗ애ㅐadsㅗ왜애", res);
                                                        final String[] tmp = res.split(",");

                                                        Log.d("왜왜왜왜ㅗ애ㅐㅗ왜애", tmp[3]);
                                                        if (tmp.length > 4) {
                                                            res = tmp[5].substring(13);

                                                            String uri = res.substring(0, res.length() - 1);


                                                            Glide.with(Maps.this)
                                                                    .load(uri)
                                                                    .into(suggest_img);
                                                        }


                                                    }
                                                });

                                            }
                                        });
                                    } else {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                Glide.with(Maps.this)
                                                        .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                        .into(suggest_img);

                                            }
                                        });


                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                    }
                });

            }
        });
        receive_trad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mainframe.setVisibility(View.GONE);
                suggestframe.setVisibility(View.VISIBLE);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location loc_current;
                loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc_current != null) {
                    cur_lat = loc_current.getLatitude();
                    cur_lon = loc_current.getLongitude();
                    // 이제 latitude 변수를 사용할 수 있습니다.
                }
                Call<List<ReceiveModel>> call = RetrofitInstance.getApiService().call_cultural_quest(
                        cur_lon,
                        cur_lat,
                        finalCharacterId);
                call.enqueue(new Callback<List<ReceiveModel>>() {
                    @Override
                    public void onResponse(Call<List<ReceiveModel>> call, Response<List<ReceiveModel>> response) {
                        subframe.setVisibility(View.INVISIBLE);
                        suggestframe.setVisibility(View.VISIBLE);
                        int[] i = {0};
                        if (response.body() != null) {
                            i = new int[]{response.body().size()};
                        }
                        if (response.body() == null) {
                            suggest_subframe.setVisibility(View.GONE);
                            noquest.setVisibility(View.VISIBLE);
                        } else if (response.body().get(0).getFirstimage().equals("")) {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Log.d("asdfsfaeanrobobmoafmiof", "alganaonoriogrmjo");
                            Request request = new Request.Builder()
                                    .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().get(0).getTitle())
                                    .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                    .build();
                            OkHttpClient client = new OkHttpClient();
                            client.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                                    Log.d("aarevadsdf", call.toString() + "\n" + e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                    Maps.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String res = null;
                                            try {
                                                res = response.body().string();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Log.d("왜왜왜왜ㅗ애ㅐadsㅗ왜애", res);
                                            final String[] tmp = res.split(",");

                                            Log.d("왜왜왜왜ㅗ애ㅐㅗ왜애", tmp[3]);
                                            if (tmp.length > 4) {
                                                res = tmp[5].substring(13);

                                                String uri = res.substring(0, res.length() - 1);
                                                Log.i("zzz", "a");

                                                Glide.with(Maps.this)
                                                        .load(uri)
                                                        .into(suggest_img);
                                            }


                                        }
                                    });

                                }
                            });
                        } else {
                            suggest_title.setText(response.body().get(0).getTitle());
                            suggest_deatil.setText(response.body().get(0).getTitle() + "에 가보기!");
                            Maps.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    Glide.with(Maps.this)
                                            .load(response.body().get(0).getFirstimage())
                                            .into(suggest_img);

                                }
                            });


                        }


                        int[] finalI = i;
                        suggest_reroll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finalI[0]--;
                                if (finalI[0] == 0) {
                                    Log.d("씨발", "ㅋㅋㅋㅋ");
                                    suggest_subframe.setVisibility(View.GONE);
                                    noquest.setVisibility(View.VISIBLE);
                                } else {
                                    suggest_title.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle());
                                    suggest_deatil.setText(response.body().get(((response.body().size() - finalI[0]))).getTitle() + "에 가보기!");


                                    Log.d("asdfasdf", response.body().get((response.body().size() - finalI[0])).getFirstimage() + "");

                                    if (response.body().get(((response.body().size() - finalI[0]))).getFirstimage().equals("")) {
                                        String url = "https://dapi.kakao.com/v2/search/image?query=" + response.body().get((response.body().size() - finalI[0])).getTitle();
                                        Log.d("asdfsfaeanrobobmoafmiof", url);
                                        Request request = new Request.Builder()
                                                .url(url)
                                                .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                                                .build();
                                        OkHttpClient client = new OkHttpClient();
                                        client.newCall(request).enqueue(new okhttp3.Callback() {
                                            @Override
                                            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                                Log.d("adfmaeobninbadnio", call.toString() + "\n" + e.getMessage() + "\n" + e.toString());

                                            }

                                            @Override
                                            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                                                Maps.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String res = null;
                                                        try {
                                                            res = response.body().string();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        Log.d("왜왜왜왜ㅗ애ㅐadsㅗ왜애", res);
                                                        final String[] tmp = res.split(",");

                                                        Log.d("왜왜왜왜ㅗ애ㅐㅗ왜애", tmp[3]);
                                                        if (tmp.length > 4) {
                                                            res = tmp[5].substring(13);

                                                            String uri = res.substring(0, res.length() - 1);


                                                            Glide.with(Maps.this)
                                                                    .load(uri)
                                                                    .into(suggest_img);
                                                        }


                                                    }
                                                });

                                            }
                                        });
                                    } else {
                                        Maps.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                Glide.with(Maps.this)
                                                        .load(response.body().get(((response.body().size() - finalI[0]))).getFirstimage())
                                                        .into(suggest_img);

                                            }
                                        });


                                    }

                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<ReceiveModel>> call, Throwable t) {

                    }
                });

            }
        });


        characterCursor.close();


    }

    private void tipdialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_quest_tip);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        TextView tipttitle = dialog.findViewById(R.id.dialog_quest_tip_txt_title);
        TextView tipdetail = dialog.findViewById(R.id.dialog_quest_tip_txt_detail);
        ImageView tipimg = dialog.findViewById(R.id.dialog_quest_tip_txt_img);
        Call<TipModel> call = RetrofitInstance.getApiService().calltip();
        call.enqueue(new Callback<TipModel>() {
            @Override
            public void onResponse(Call<TipModel> call, Response<TipModel> response) {
                tipttitle.setText(response.body().getTitle());
                tipdetail.setText(response.body().getDaily_quiz());

                Request request = new Request.Builder()
                        .url("https://dapi.kakao.com/v2/search/image?query=" + response.body().getTitle())
                        .addHeader("Authorization", "KakaoAK e8ab93c206c38e87788dd9cb4078e149")
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                        String res = response.body().string();
                        final String[] tmp = res.split(",");

                        res = tmp[5].substring(13);


                        String uri = res.substring(0, res.length() - 1);
                        Maps.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                Glide.with(Maps.this)
                                        .load(uri)
                                        .error(R.drawable.no_image)
                                        .into(tipimg);

                            }
                        });
                    }


                });
            }

            @Override
            public void onFailure(Call<TipModel> call, Throwable t) {


            }
        });
        params.width = (int) dptopx(this, 400);
        params.height = (int) dptopx(this, 600);
        dialog.getWindow().setAttributes(params);
        dialog.show();


    }


    //지도 초기화
    @Override
    public void onMapViewInitialized(MapView mapView) {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc_current;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        loc_current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc_current == null) {
            loc_current = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if(loc_current!= null) {
            cur_lat = loc_current.getLatitude();
            cur_lon = loc_current.getLongitude();
        }


        mapView.setMapViewEventListener(mapView.getMapViewEventListener());
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithMarkerHeadingWithoutMapMoving);
        // mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.arrow, new MapPOIItem.ImageOffset(0,0));


        mapView.setZoomLevel(2, false);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(cur_lat, cur_lon), true);
        return;

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    //아래쪽은 마커 클릭 이벤트 처리
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {


    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {


    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {


    }

    public float dptopx(Context context, float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        cur_lat = mapPoint.getMapPointGeoCoord().latitude;
        cur_lon = mapPoint.getMapPointGeoCoord().longitude;

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        cur_lat = location.getLatitude();
        cur_lon = location.getLongitude();
        Log.d("///", cur_lat + "//" + cur_lon);

    }
}