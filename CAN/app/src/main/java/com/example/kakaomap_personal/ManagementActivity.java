package com.example.kakaomap_personal;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagementActivity extends AppCompatActivity {

    private ListView listView;
    private UserListAdapter adapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);


        Intent intent = getIntent();

        listView = (ListView)findViewById(R.id.ListView);
        userList = new ArrayList<User>();

        //어댑터 초기화부분 userList와 어댑터를 연결해준다.
        adapter = new UserListAdapter(getApplicationContext(), userList,this);
        listView.setAdapter(adapter);

        try{
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));


            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String userID;
            String userPassword;
            String userName;
            String userTotal;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);

                userName = object.getString("userName");
                userTotal = object.getString("total");
                userID= object.getString("userID");
                userPassword= object.getString("userPassword");
                //값들을 User클래스에 묶어줍니다
                User user = new User(userID,userPassword,userName, userTotal);
                if(!userID.equals("admin"))
                    userList.add(user);//리스트뷰에 값을 추가해줍니다
                count++;
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
