package com.pnu.b_gosu;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;


import com.pnu.b_gosu.Permission.Permissionpage;
import com.pnu.b_gosu.R;
import com.pnu.b_gosu.Retrofit.RetrofitInstance;

import java.security.MessageDigest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("HardwareIds")
    public Animation fade_out;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        VideoView videoView = findViewById(R.id.videoview);

        int id_video = getResources().getIdentifier("logo_anime","mp4",getPackageName());
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/logo_anime");


        //디바이스 고유 Android_id 값을 이용하여 사용자 식별

        TextView wall = findViewById(R.id.fadeout_wall);

        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.logosound);
        mediaPlayer.start();


        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                wall.setVisibility(View.VISIBLE);
                wall.startAnimation(fade_out);

            }
        });

        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Call<String> call = RetrofitInstance.getApiService().login(getDeviceId(MainActivity.this));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().equals("A"))
                        {
                            Intent i = new Intent(MainActivity.this, MainPage.class);
                            i.putExtra("key",1);
                            startActivity(i);
                        }
                        else if(response.body().equals("B")){
                            Intent i = new Intent(MainActivity.this, Permissionpage.class);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Hash Key", something);
                clipboard.setPrimaryClip(clip);
                Log.d("Hash key", something);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}