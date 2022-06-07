package com.example.kakaomap_personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class TitleActivity extends AppCompatActivity {
    int nCurrentPermission=0;
    static final int PERMISSIONS_REQUEST=0x0000001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        onCheckPermisssion();

        ImageButton login_button = findViewById(R.id.title_loginButton);
        ImageButton signin_button = findViewById(R.id.title_signinButton);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

    }
    public void onCheckPermisssion(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(this,"앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch(requestCode){
            case PERMISSIONS_REQUEST:
                if(grantResults.length>0
                        && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"앱 실행을 위한 권한이 설정되었습니다", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}