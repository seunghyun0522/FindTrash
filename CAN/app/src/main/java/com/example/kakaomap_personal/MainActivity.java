package com.example.kakaomap_personal;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener, SensorEventListener {

    //xml
    private MapView mMapView;
    private ViewGroup mMapViewContainer;

    //value
    MapPoint currentMapPoint;
    MapPoint canMapPoint;

    Double startLatitude; //앱 실행 시 사용자 위도
    Double startLongitude; //앱 실행 시 사용자 경도
    Double currentLatitude; //사용자 현재 위치 위도
    Double currentLongitude; //사용자 현재 위치 경도
    Double latitude; //가장 가까운 쓰레기통 위도
    Double longitude; //가장 가까운 쓰레기통 경도

    MapPOIItem canMarker = new MapPOIItem();
    MapPOIItem startMarker = new MapPOIItem();

    private Intent intent;
    private TextView layout_userName;
    private int now_step_count;

    private String userID;
    private String userPassword;
    private String userName;
    private int step_count;
    private int trash_count;
    private int total;
    private int best_rank;
    private int now_rank;

    private int flag=0;
    public double distance=10.0;

    public ImageButton arrive_button; //05-21 sy onCurrentLocationUpdate에서도 사용하기 위해 전역변수 선언
    public ImageButton arrive_button2;//05-21 sy 위와 동일
    private boolean startingApp = true;

    Response.Listener<String> c_responseListener;

    SensorManager sensorManager;
    Sensor stepCountSensor;
    TextView stepCountView;

    int currentSteps = 0;
     @RequiresApi(api = Build.VERSION_CODES.Q)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrive_button = findViewById(R.id.main_arriveButton);
        arrive_button2 = findViewById(R.id.main_arriveButton2);
        stepCountView = findViewById(R.id.textView);

        initView(); //화면 초기화

         //활동 퍼미션 체크
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
             requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
         }

         // 걸음 센서 연결
         // * 옵션
         // - TYPE_STEP_DETECTOR:  리턴 값이 무조건 1, 앱이 종료되면 다시 0부터 시작
         // - TYPE_STEP_COUNTER : 앱 종료와 관계없이 계속 기존의 값을 가지고 있다가 1씩 증가한 값을 리턴
         //
         sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

         // 디바이스에 걸음 센서의 존재 여부 체크
         if (stepCountSensor == null) {
             Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
         }

    }

    public void onStart() {
        super.onStart();
        if(stepCountSensor !=null) {
            // 센서 속도 설정
            // * 옵션
            // - SENSOR_DELAY_NORMAL: 20,000 초 딜레이
            // - SENSOR_DELAY_UI: 6,000 초 딜레이
            // - SENSOR_DELAY_GAME: 20,000 초 딜레이
            // - SENSOR_DELAY_FASTEST: 딜레이 없음
            sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }
        @Override
    public void onSensorChanged(SensorEvent event) {
         Log.i("----","발생");
        // 걸음 센서 이벤트 발생시
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){

            if(event.values[0]==1.0f){
                // 센서 이벤트가 발생할때 마다 걸음수 증가
                currentSteps++;
                stepCountView.setText(String.valueOf(currentSteps));
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {

        //맵 바인.딩
        mMapView = new MapView(this);
        mMapViewContainer = findViewById(R.id.map_view);
        mMapViewContainer.addView(mMapView);

        //맵 리스너
        mMapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mMapView.setPOIItemEventListener(this);

        //맵 리스너 (현재위치 업데이트)
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); //현재위치 추적

        //LoginActivity에서 전달한 정보들 받아오기
        intent = getIntent();
        userID = intent.getStringExtra("userID");
        userPassword = intent.getStringExtra("userPassword");
        userName = intent.getStringExtra("userName");
        step_count = intent.getIntExtra("step_count", 0);
        trash_count = intent.getIntExtra("trash_count", 0);
        total = intent.getIntExtra("total", 0);
        best_rank = intent.getIntExtra("best_rank", 0);

        //회원 정보 적용
        layout_userName = findViewById(R.id.main_userName);
        layout_userName.setText(userName);

        c_responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.i("-------------","response-------------"+response);

                    JSONObject jsonObject = new JSONObject(response);

                    Toast.makeText(getApplicationContext(),"가장 가까운 쓰레기통의 위치를 찾는 데 성공했습니다.",Toast.LENGTH_SHORT).show();

                    latitude=Double.parseDouble(jsonObject.getString("latitude"));
                    longitude=Double.parseDouble(jsonObject.getString("longitude"));

                    canMapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    canMarker.setItemName("쓰레기통 위치");
                    canMarker.setMapPoint(canMapPoint);//마커 위치 설정
                    canMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); //마커 모습(기본)
                    canMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); //마커 모습(클릭)
                    mMapView.addPOIItem(canMarker); //지도 위에 마커 표시

                    flag=1; //가장 가까운 쓰레기통 찾았을 때 distance구하기 위해 flag 설정

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };


    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        currentLatitude = mapPointGeo.latitude;
        currentLongitude = mapPointGeo.longitude;

        if (startingApp==true){
            startLatitude = currentLatitude;
            startLongitude = currentLongitude;
            startingApp=false;

            TrashCanRequest trashCanRequest = new TrashCanRequest(startLatitude, startLongitude, c_responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(trashCanRequest);
        }

        if(flag==1){//가장 가까운 쓰레기통 찾았을 때
        distance=Math.sqrt((latitude-currentLatitude)*(latitude-currentLatitude)+(longitude-currentLongitude)*(longitude-currentLongitude));

        //쓰레기통에 가까워졌을 때
        if(distance<0.0004) {
            arrive_button.setVisibility(View.INVISIBLE);//회색 버튼 안보이게
            arrive_button2.setVisibility(View.VISIBLE);//초록색 버튼 보이게
            arrive_button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    now_step_count = currentSteps; //현재 걸음수 업데이트

                    //데베 업뎃을 위해 회원정보 업뎃
                    step_count = step_count + now_step_count;
                    trash_count = trash_count + 1;
                    total = step_count + trash_count * 1000;

                    //데베 업뎃
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                Log.i("------------", "Updateresponse"+response);

                                JSONObject jsonObject = new JSONObject(response);

                                best_rank = Integer.parseInt(jsonObject.getString("best_rank"));
                                now_rank = Integer.parseInt(jsonObject.getString("now_rank"));

                                intent = new Intent(MainActivity.this, RankingActivity.class);

                                //RankingActivity에 회원정보 전달
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPassword", userPassword);
                                intent.putExtra("userName", userName);
                                intent.putExtra("step_count", step_count);
                                intent.putExtra("trash_count", trash_count);
                                intent.putExtra("total", total);
                                intent.putExtra("best_rank", best_rank);
                                intent.putExtra("now_rank", now_rank);
                                intent.putExtra("now_step_count", now_step_count);

                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    UpdateRequest updateRequest = new UpdateRequest(userID, step_count, trash_count, total, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(updateRequest);

                }
            });
        }}
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
    public void onMapViewInitialized(MapView mapView) {

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

}