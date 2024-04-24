package com.pnu.b_gosu.Permission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.pnu.b_gosu.Airport.Airport;
import com.pnu.b_gosu.R;

public class Permissionpage extends AppCompatActivity {

    private Permissionsup permission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        permissionCheck();

    }

    // 권한 체크
    private void permissionCheck() {

        // PermissionSupport.java 클래스 객체 생성
        permission = new Permissionsup(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()){
            //권한 요청
            permission.requestPermission();
        }else {
            // 권한이 모두 허용되었을 때 Airport 액티비티를 시작합니다.
            startAirportActivity();
        }
    }

    // Request Permission에 대한 결과 값 받아와
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            // 다시 permission 요청
            permission.requestPermission();
        }else {
            // 권한이 모두 허용되었을 때 Airport 액티비티를 시작합니다.
            startAirportActivity();
        }
    }
    private void startAirportActivity() {
        Intent i = new Intent(Permissionpage.this, Airport.class);
        startActivity(i);
        finish(); // 현재 액티비티를 종료하여 뒤로 가기 버튼을 눌렀을 때 이 액티비티로 돌아오지 않도록 합니다.
    }

}