package com.pnu.b_gosu.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissionsup {
    private Context context;
    private Activity activity;

    int currentVersion = Build.VERSION.SDK_INT;
    //요청할 권한 배열 저장
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,

    };
    private List permissionList;

    //권한 요청시 발생하는 창에 대한 결과값을 받기 위해 지정해주는 int 형
    //원하는 임의의 숫자 지정
    private final int MULTIPLE_PERMISSIONS = 1023; //요청에 대한 결과값 확인을 위해 RequestCode를 final로 정의

    //생성자에서 Activity와 Context를 파라미터로 받아
    public Permissionsup(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    //배열로 선언한 권한 중 허용되지 않은 권한 있는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(context, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){
            return false;
        }
        return true;
    }

    //배열로 선언한 권한에 대해 사용자에게 허용 요청
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void requestPermission(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                List<String> permissionsToRequest = new ArrayList<>();
                for (String permission : permissions) {
                    // Android 13 이상인 경우, WRITE_EXTERNAL_STORAGE 권한을 CAMERA로 변경
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
                    } else {
                        permissionsToRequest.add(permission);
                    }
                }

                // 사용자에게 권한 요청
                ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[0]), MULTIPLE_PERMISSIONS);
            } else {
                // Android 버전이 13 미만인 경우, 이전 권한을 그대로 사용
                ActivityCompat.requestPermissions(activity, permissions, MULTIPLE_PERMISSIONS);
            }
        }

    //요청한 권한에 대한 결과값 판단 및 처리
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        //우선 requestCode가 아까 위에 final로 선언하였던 숫자와 맞는지, 결과값의 길이가 0보다는 큰지 먼저 체크
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.length >0)) {
            for(int i=0; i< grantResults.length; i++){
                //grantResults 가 0이면 사용자가 허용한 것 / -1이면 거부한 것
                //-1이 있는지 체크하여 하나라도 -1이 나온다면 false를 리턴
                if(grantResults[i] == -1){
                    return false;
                }
            }
        }
        return true;
    }

}