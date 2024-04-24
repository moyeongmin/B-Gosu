package com.pnu.b_gosu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pnu.b_gosu.Database.SubmitDataSource;
import com.pnu.b_gosu.R;
import com.pnu.b_gosu.Retrofit.InfoModel;
import com.pnu.b_gosu.Retrofit.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeskActivity extends AppCompatActivity {
    private int currentImageIndex = 0; // 현재 이미지 인덱스를 저장하는 변수 (0: picture1, 1: picture2, 2: picture3)

    int character_code = 1; // 선배 여기 수정해야합니다 ~~~
    int language_code = 1;   // 1: korean, 2:english 임의로 해놨습니당 사실 한국어 아니면 다 영어로 해놨어요
    int citizen_file;  // 시민증

    public static int characterid = 0;

    public static TextView[] ques = new TextView[6];
    public static TextView[] ans = new TextView[6];

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);
        SubmitDataSource dataSource = new SubmitDataSource(DeskActivity.this);
        dataSource.open();
        Cursor characterCursor = dataSource.getAllCharacters();

        if (characterCursor != null && characterCursor.moveToFirst()) {
            do {
                characterid = characterCursor.getInt(characterCursor.getColumnIndex("character_id"));

            } while (characterCursor.moveToNext());
        }

        dataSource.close();
        Display display = getWindowManager().getDefaultDisplay();
        int deviceWidth = display.getWidth();
        int deviceHeight = display.getHeight();

        // 가로와 높이 조정
        int newWidth1 = deviceWidth - 72;
        int newHeight1 = deviceHeight - 352;

        TextView mainbtn = findViewById(R.id.desk_btn_maps);

        mainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DeskActivity.this, MainPage.class);
                startActivity(i);
            }
        });
        // mainframe 뷰의 LayoutParams 설정
        ConstraintLayout mainframe = findViewById(R.id.desk_inside_mainframe);
        ConstraintLayout subframe = findViewById(R.id.desk_mainframe);


        ConstraintLayout.LayoutParams params1 = new ConstraintLayout.LayoutParams(newWidth1, newHeight1);
        params1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID; // 상단에 맞춤
        params1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // 시작 부분에 맞춤
        params1.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID; // 끝 부분에 맞춤
        params1.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params1.verticalBias = 0.5f;
        params1.horizontalBias = 0.5f;

        subframe.setLayoutParams(params1);

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

        final ImageButton citizen_button = findViewById(R.id.citizen_card_button);
        final ImageButton character_info_button = findViewById(R.id.character_info_button);
        final ImageButton stats_button = findViewById(R.id.stats_button);
        startAnimation(citizen_button,10f);
        startAnimation(character_info_button,-10f);
        startAnimation(stats_button,5f);

        Call<InfoModel> call = RetrofitInstance.getApiService().callinformation(characterid);
        call.enqueue(new Callback<InfoModel>() {
            @Override
            public void onResponse(Call<InfoModel> call, Response<InfoModel> response) {

                switch (character_code) {
                    case 1:
                        citizen_file = R.drawable.citizen_card1;
                        citizen_button.setImageResource(citizen_file);
                        break;
                    case 2:
                        citizen_file = R.drawable.citizen_card2;
                        citizen_button.setImageResource(citizen_file);
                        break;
                    case 3:
                        citizen_file = R.drawable.citizen_card3;
                        citizen_button.setImageResource(citizen_file);
                        break;
                }

            }

            @Override
            public void onFailure(Call<InfoModel> call, Throwable t) {

            }
        });


        // 시민 카드

        citizen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DeskActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.citizen_card);
                dialog.setCancelable(true);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int) dptopx(DeskActivity.this, 350);
                params.height = (int) dptopx(DeskActivity.this, 250);
                dialog.getWindow().setAttributes(params);

                ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                dialogImage.setImageResource(citizen_file);

                // TextView 텍스트 변경
                TextView citizenName = dialog.findViewById(R.id.citizen_name);
                TextView citizenBirth = dialog.findViewById(R.id.citizen_birth);
                TextView citizenBorn = dialog.findViewById(R.id.citizen_born);
                Call<InfoModel> call = RetrofitInstance.getApiService().callinformation(characterid);
                call.enqueue(new Callback<InfoModel>() {
                    @Override
                    public void onResponse(Call<InfoModel> call, Response<InfoModel> response) {



                        citizenName.setText(response.body().getCharacter_name());
                        citizenBirth.setText(response.body().getBirthday());
                        switch (response.body().getType()) {
                            case 1 :
                                dialogImage.setImageResource(R.drawable.citizen_card1);
                                citizenBorn.setText("국제시장");
                                break;
                            case 2 :
                                dialogImage.setImageResource(R.drawable.citizen_card2);
                                citizenBorn.setText("해운대");
                                break;
                            case 3 :
                                dialogImage.setImageResource(R.drawable.citizen_card3);
                                citizenBorn.setText("동백섬");
                                break;


                        }
                    }

                    @Override
                    public void onFailure(Call<InfoModel> call, Throwable t) {

                    }
                });


                dialog.show();
            }
        });

        // 캐릭터 도감
        character_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DeskActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.character_info);
                dialog.setCancelable(true);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int) dptopx(DeskActivity.this, 350);
                params.height = (int) dptopx(DeskActivity.this, 650);
                dialog.getWindow().setAttributes(params);

                final ImageButton right_button1 = dialog.findViewById(R.id.right_buttton1);
                final ImageButton left_button1 = dialog.findViewById(R.id.left_buttton1);
                final ImageView dialogImage = dialog.findViewById(R.id.dialog_image); // ImageView 참조 가져오기
                updateButtonState(left_button1, right_button1);
                dialogImage.setImageResource(getImageResource(0)); // 현재 이미지 설정

                left_button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentImageIndex > 0) {
                            currentImageIndex--;
                            dialogImage.setImageResource(getImageResource(currentImageIndex));
                        }
                        updateButtonState(left_button1, right_button1);
                    }
                });

                right_button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentImageIndex < 2) {
                            currentImageIndex++;
                            dialogImage.setImageResource(getImageResource(currentImageIndex));
                        }
                        updateButtonState(left_button1, right_button1);
                    }
                });

                updateButtonState(left_button1, right_button1); // 버튼 상태 초기 설정
                dialog.show();
            }
        });


        // 통계 카드
        stats_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DeskActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.stats);

                TextView title = findViewById(R.id.dialog_stats_title);

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int) dptopx(DeskActivity.this, 350);
                params.height = (int) dptopx(DeskActivity.this, 650);
                dialog.getWindow().setAttributes(params);
                for(int i = 0 ; i < 6 ; i ++){
                    String tmp = "dialog_stats_ques_"+(i+1);
                    int resID = getResources().getIdentifier(tmp,"id",getPackageName());
                    ques[i] = ((TextView)dialog.findViewById(resID));
                    tmp = "dialog_stats_ans_"+(i+1);
                    resID = getResources().getIdentifier(tmp,"id",getPackageName());
                    ans[i] = ((TextView)dialog.findViewById(resID));
                }
                Call<InfoModel> call = RetrofitInstance.getApiService().callinformation(characterid);
                call.enqueue(new Callback<InfoModel>() {
                    @Override
                    public void onResponse(Call<InfoModel> call, Response<InfoModel> response) {

                        ans[0].setText(response.body().getTimes());
                        ans[1].setText(response.body().getSuccess_count()+"회");
                        ans[2].setText(response.body().getSuccess_area());
                        ans[3].setText(response.body().getTotal_score1()+"점");
                        ans[4].setText(response.body().getTotal_score2()+"점");
                        ans[5].setText(response.body().getTotal_score2()+"개");
                    }

                    @Override
                    public void onFailure(Call<InfoModel> call, Throwable t) {

                    }
                });

                dialog.show();
            }
        });
    }

    private int getImageResource(int index) {
        switch (index) {
            case 0:
                if (language_code == 1)
                    return R.drawable.character_info1_ko;
                else
                    return R.drawable.character_info1_en;
            case 1:
                if (language_code == 1)
                    return R.drawable.character_info2_ko;
                else
                    return R.drawable.character_info2_en;
            case 2:
                if (language_code == 1)
                    return R.drawable.character_info3_ko;
                else
                    return R.drawable.character_info3_en;

        }
        return index;
    }


    private void updateButtonState(ImageButton leftButton, ImageButton rightButton) {
        if (currentImageIndex == 0) {
            leftButton.setEnabled(false);
            leftButton.setColorFilter(Color.GRAY);
        } else {
            leftButton.setEnabled(true);
            leftButton.setColorFilter(null);
        }

        if (currentImageIndex == 2) {
            rightButton.setEnabled(false);
            rightButton.setColorFilter(Color.GRAY);
        } else {
            rightButton.setEnabled(true);
            rightButton.setColorFilter(null);
        }
    }

    public float dptopx(Context context , float dp)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,dm);
    }

    public void startAnimation(View view, float startRotation) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", startRotation, -startRotation);
        animator.setDuration(500); // 애니메이션 지속 시간 설정
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.start(); // 애니메이션 시작
    }
}
