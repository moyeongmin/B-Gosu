package com.pnu.b_gosu.Airport;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnu.b_gosu.Database.SubmitDataSource;
import com.pnu.b_gosu.MainPage;
import com.pnu.b_gosu.R;
import com.pnu.b_gosu.Retrofit.RetrofitInstance;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Airport extends AppCompatActivity {

    public Animation fade_in;
    public Animation fade_out;
    public static String name;
    private Date startDate = null;
    private Date endDate = null;
    public static int tmp;
    public static CheckBox[] checkBoxes = new CheckBox[26];
    public static int[] check = new int[26];
    public static int[] submit = new int[9];
    public static String start_date;
    public static String end_date;
    public static int cal_submit_status = 0;
    public static int deviceWidth;
    public static int deviceHeight;

    public static MaterialCalendarView calendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_airport);
        ConstraintLayout layout = findViewById(R.id.fullframe);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        TextView wall = findViewById(R.id.airport_wall);
        TextView npctxt = findViewById(R.id.npc_txt);
        ConstraintLayout container = findViewById(R.id.container_user);
        ConstraintLayout npc_box = findViewById(R.id.npc_box);
        TextView npc_nextbtn = findViewById(R.id.npc_nextbtn);
        TextView cal_submit = findViewById(R.id.cal_submit);
        Calendar calendar = Calendar.getInstance();
        ImageView upside_img =findViewById(R.id.airportimg);
        calendarView = findViewById(R.id.calendarview);
        cal_submit.setVisibility(View.VISIBLE);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        Display display = getWindowManager().getDefaultDisplay();
        deviceWidth = display.getWidth();
        deviceHeight = display.getHeight();
        ViewGroup.LayoutParams imgparam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, deviceHeight/3);
        upside_img.setLayoutParams(imgparam);


        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.airport_sound);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });




        ViewGroup.LayoutParams params = npc_box.getLayoutParams();
        LocalDate mDate = LocalDate.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));




        EditText editText = findViewById(R.id.editext_user);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i) {
                    case KeyEvent.KEYCODE_ENTER:
                        name = editText.getText().toString();
                        editText.setVisibility(View.GONE);
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(deviceWidth*90/100, deviceHeight*35/100);
                        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.topToTop = npc_box.getId();
                        params.verticalBias = (float) 0.55;
                        container.setLayoutParams(params);
                        Display display = getWindowManager().getDefaultDisplay();
                        int deviceWidth = display.getWidth();
                        int deviceHeight = display.getHeight();
                        CalendarView.LayoutParams calparams = new CalendarView.LayoutParams(deviceWidth*90/100, deviceHeight*35/100);
                        container.removeAllViews();

                        npctxt.setHeight(deviceHeight/10);
                        npctxt.setText(R.string.npc_txt_2);
                        container.addView(calendarView,calparams);
                        calendarView.setVisibility(View.VISIBLE);
                        cal_submit.setHeight(deviceHeight/10);
                        cal_submit_status = 1;
                        return true;
                }

                return false;
            }
        });
        cal_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //날짜 범위 선택이 끝나면 다 끄고 npc 텍스트를 바꿔줍니다.
                //tmp를 1로 바꿔 현재 상태를 지정해줍니다.
                if(cal_submit_status == 1) {

                    List<CalendarDay> selcal = calendarView.getSelectedDates();
                    cal_submit.setVisibility(View.GONE);

                    npctxt.setText(R.string.npc_txt_3_1);
                    npctxt.setHeight(deviceHeight*30/100);
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    npc_box.setLayoutParams(params);
                    container.setVisibility(View.GONE);
                    npc_nextbtn.setVisibility(View.VISIBLE);
                    tmp = 0;
                }
                else if(cal_submit_status == 0)
                {
                    name = editText.getText().toString();
                    editText.setVisibility(View.GONE);
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(deviceWidth*90/100, deviceHeight*35/100);
                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.topToTop = npc_box.getId();
                    params.verticalBias = (float) 0.55;
                    container.setLayoutParams(params);
                    Display display = getWindowManager().getDefaultDisplay();
                    int deviceWidth = display.getWidth();
                    int deviceHeight = display.getHeight();
                    CalendarView.LayoutParams calparams = new CalendarView.LayoutParams(deviceWidth*90/100, deviceHeight*35/100);
                    container.removeAllViews();

                    npctxt.setHeight(deviceHeight/10);
                    npctxt.setText(R.string.npc_txt_2);
                    container.addView(calendarView,calparams);
                    calendarView.setVisibility(View.VISIBLE);
                    cal_submit.setHeight(deviceHeight/10);
                    cal_submit_status = 1;

                }
            }
        });
        npc_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tmp)
                {
                    case 0: {
                        surveydialog();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                npctxt.setText(R.string.npc_txt_4_1);

                            }
                        },3000);

                        tmp = 1;
                        break;

                    }
                    case 1: {
                        npctxt.setText(R.string.npc_txt_4_2);
                        npctxt.setHeight(deviceHeight*40/100);
                    tmp = 2;
                    break;
                    }
                    case 2:{
                    npctxt.setText(R.string.npc_txt_4_3);
                        npctxt.setHeight(deviceHeight*30/100);
                        tmp = 3;
                        break;
                    }
                    case 3:{
                        wall.setVisibility(View.VISIBLE);
                        wall.startAnimation(fade_out);
                        break;

                    }
                }
            }
        });

        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(Airport.this, MainPage.class);
                i.putExtra("key",1);
                startActivity(i);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                startDate = convertToLocalDateToDate(dates.get(0));

                endDate = convertToLocalDateToDate(dates.get(dates.size() - 1));
                        // 선택된 범위 처리
                Calendar calStart = Calendar.getInstance();
                calStart.setTime(startDate);
                int startYear = calStart.get(Calendar.YEAR);
                int startMonth = calStart.get(Calendar.MONTH) + 1;
                int startDay= calStart.get(Calendar.DAY_OF_MONTH);

                Calendar calEnd= Calendar.getInstance();
                calEnd.setTime(endDate);
                int endYear= calEnd.get(Calendar.YEAR);
                int endMonth= calEnd.get(Calendar.MONTH) +1;
                int endDay= calEnd.get(Calendar.DAY_OF_MONTH);



                start_date = String.format("%d-%02d-%02d", startYear, startMonth-1, startDay);
                end_date = String.format("%d-%02d-%02d", endYear, endMonth-1, endDay);
                Log.d("호우ㅋㅋㅋ",start_date);
                Log.d("호우우우ㅋㅋㅋ",end_date);

                    String formattedStartDate = String.format("%d년 %02d월 %02d일", startYear, startMonth, startDay);
                    String formattedEndDate = String.format("%d년 %02d월 %02d일", endYear, endMonth, endDay);

                    String message = "시작 날짜: " + formattedStartDate + "종료 날짜: " + formattedEndDate;

                    }

        });






        fade_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        wall.startAnimation(fade_in);




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






    }

    public float dptopx(Context context , float dp)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,dm);
    }
    private Date convertToLocalDateToDate(CalendarDay calendarDay) {
        LocalDate localDate = LocalDate.of(calendarDay.getYear(), calendarDay.getMonth() + 1, calendarDay.getDay());
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void surveydialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_survey);
        ConstraintLayout surveymainframe = dialog.findViewById(R.id.survey_mainframe);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        TextView title = dialog.findViewById(R.id.survey_title);
        params.width = deviceWidth/10*9;
        params.height = deviceHeight/10*9;
        dialog.setCancelable(false);
        dialog.getWindow().setAttributes(params);
        dialog.show();

        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,deviceHeight/10*7);
        params1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params1.topToBottom = title.getId();
        params1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params1.verticalBias = (float)0.2;
        surveymainframe.setLayoutParams(params1);
        TextView closebtn = dialog.findViewById(R.id.dialog_submit_btn);
        for(int i = 0 ; i < 26 ; i ++){
            String tmp = "checkBox_ans"+(i+1);
            int resID = getResources().getIdentifier(tmp,"id",getPackageName());
            checkBoxes[i] = ((CheckBox)dialog.findViewById(resID));
        }
        for(int  i = 0 ; i < 6; i++){
            int k = i;
            checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxes[k].isChecked())
                    {
                        for(int p = 0 ; p < 6; p++)
                        {
                            checkBoxes[p].setEnabled(false);
                        }
                        checkBoxes[k].setEnabled(true);

                    }
                    else{
                        for(int p = 0 ; p < 6; p++)
                        {
                            checkBoxes[p].setEnabled(true);
                        }
                    }
                }
            });
        }
        for(int i = 6 ; i < 8; i++)
        {
            int k = i;
            checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxes[k].isChecked())
                    {
                        for(int p = 6 ; p < 8; p++)
                        {
                            checkBoxes[p].setEnabled(false);
                        }
                        checkBoxes[k].setEnabled(true);

                    }
                    else{
                        for(int p = 6 ; p < 8; p++)
                        {
                            checkBoxes[p].setEnabled(true);
                        }
                    }
                }
            });

        }
        for(int i = 8 ; i < 11; i++)
        {
            int k = i;
            checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxes[k].isChecked())
                    {
                        for(int p = 8 ; p < 11; p++)
                        {
                            checkBoxes[p].setEnabled(false);
                        }
                        checkBoxes[k].setEnabled(true);

                    }
                    else{
                        for(int p = 8 ; p < 11; p++)
                        {
                            checkBoxes[p].setEnabled(true);
                        }
                    }
                }
            });

        }

        for(int i = 11 ; i < 15; i++)
        {
            int k = i;
            checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxes[k].isChecked())
                    {
                        for(int p = 11 ; p < 15; p++)
                        {
                            checkBoxes[p].setEnabled(false);
                        }
                        checkBoxes[k].setEnabled(true);

                    }
                    else{
                        for(int p = 11 ; p < 15; p++)
                        {
                            checkBoxes[p].setEnabled(true);
                        }
                    }
                }
            });

        }
        for(int i = 15 ; i < 20; i++)
        {
            int k = i;
            checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxes[k].isChecked())
                    {
                        for(int p = 15 ; p < 20; p++)
                        {
                            checkBoxes[p].setEnabled(false);
                        }
                        checkBoxes[k].setEnabled(true);

                    }
                    else{
                        for(int p = 15 ; p < 20; p++)
                        {
                            checkBoxes[p].setEnabled(true);
                        }
                    }
                }
            });

        }
        for(int i = 23 ; i < 26; i++)
        {
            int k = i;
            checkBoxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBoxes[k].isChecked())
                    {
                        for(int p = 23 ; p < 26; p++)
                        {
                            checkBoxes[p].setEnabled(false);
                        }
                        checkBoxes[k].setEnabled(true);

                    }
                    else{
                        for(int p = 23 ; p < 26; p++)
                        {
                            checkBoxes[p].setEnabled(true);
                        }
                    }
                }
            });

        }
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0;  i < 26 ; i ++)
                {
                    if(checkBoxes[i].isChecked()) check[i] = 1;
                    else check[i] = 2;

                }
                int p=0;
                for (int i = 0 ; i < 26; i ++)
                {
                    if(checkBoxes[i].isEnabled()) p++;
                }
                if(p>9) Toast.makeText(Airport.this, "모든 체크박스를 체크해 주세요!", Toast.LENGTH_SHORT).show();
                else {

                    //1번
                    if (check[0] == 1) submit[4] = 1;
                    else if (check[1] == 1) submit[4] = 2;
                    else if (check[2] == 1) submit[4] = 3;
                    else if (check[3] == 1) submit[4] = 4;
                    else if (check[4] == 1) submit[4] = 5;
                    else if (check[5] == 1) submit[4] = 6;

                    //2번
                    if (check[6] == 1) submit[5] = 1;
                    else if (check[7] == 1) submit[5] = 2;


                    //3번
                    if (check[8] == 1) submit[6] = 1;
                    else if (check[9] == 1) submit[6] = 2;
                    else if (check[10] == 1) submit[6] = 3;

                    //4번
                    if (check[11] == 1) submit[7] = 1;
                    else if (check[12] == 1) submit[7] = 2;
                    else if (check[13] == 1) submit[7] = 3;
                    else if (check[14] == 1) submit[7] = 4;

                    //5번
                    if (check[15] == 1) submit[8] = 1;
                    else if (check[16] == 1) submit[8] = 2;
                    else if (check[17] == 1) submit[8] = 3;
                    else if (check[18] == 1) submit[8] = 4;
                    else if (check[19] == 1) submit[8] = 5;

                    //6번
                    if (check[20] == 1) submit[3] = 1;
                    else if (check[20] == 2) submit[3] = 2;
                    if (check[21] == 1) submit[1] = 1;
                    else if (check[21] == 2) submit[1] = 2;
                    if (check[22] == 1) submit[2] = 1;
                    else if (check[22] == 2) submit[2] = 2;

                    //7번
                    if (check[23] == 1) submit[1] = 1;
                    else if (check[24] == 1) submit[0] = 2;
                    else if (check[25] == 1) submit[0] = 3;


                    for (int i = 0 ; i < submit.length; i++) {
                    }
                    SubmitDataSource dataSource = new SubmitDataSource(Airport.this);

                    dataSource.open();

                    long rowId = dataSource.insertSubmitData(submit[0], submit[1], submit[2], submit[3], submit[4],
                            submit[5], submit[6], submit[7], submit[8]);

                    dataSource.close();
                    if (rowId != -1) {
                        //성공

                    }


                   if (start_date == null)
                   {
                       ;
                       startDate = convertToLocalDateToDate(Objects.requireNonNull(calendarView.getSelectedDate()));

                       // 선택된 범위 처리
                       Calendar calStart = Calendar.getInstance();
                       calStart.setTime(startDate);
                       int startYear = calStart.get(Calendar.YEAR);
                       int startMonth = calStart.get(Calendar.MONTH) + 1;
                       int startDay= calStart.get(Calendar.DAY_OF_MONTH);

                       start_date = String.format("%d-%02d-%02d", startYear, startMonth-1, startDay);
                       end_date = String.format("%d-%02d-%02d", startYear, startMonth-1, startDay);
                   }
                   Log.d("startdate4",start_date+  "// " + end_date);

                    Call<Integer> call = RetrofitInstance.getApiService().Surveysubmit(
                            getDeviceId(Airport.this),
                            ""+submit[0],
                            ""+submit[1],
                            ""+submit[2],
                            ""+submit[3],
                            ""+submit[4],
                            ""+submit[5],
                            ""+submit[6],
                            ""+submit[7],
                            ""+submit[8],
                            name,
                            start_date,
                            end_date
                    );
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            Log.d("ㅋㅋ",response.body()+"//");

                            SubmitDataSource dataSource = new SubmitDataSource(Airport.this);

                            dataSource.open();

                            long rowId = dataSource.insertCharacter(response.body());
                            if (rowId != -1) {

                                //성공

                            } else {}


                            dataSource.close();


                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {



                        }
                    });



                    dialog.dismiss();

                }
            }
        });




    }

    public static String getDeviceId(Context context) {
        String deviceId = "";

        String android_id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceId = android_id;

        return deviceId;
    }




}