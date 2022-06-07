package com.example.kakaomap_personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RankingActivity extends AppCompatActivity {

    private Intent intent;

    private String userID;
    private String userPassword;
    private String userName;
    private int step_count;
    private int trash_count;
    private int total;
    private int now_rank;
    private int best_rank;
    private int now_step_count;

    private TextView layout_userName;
    private TextView layout_nowRank;
    private TextView layout_bestRank;
    private TextView layout_trashCnt;
    private TextView layout_stepCnt;
    private TextView layout_nowStepCnt;

    private TextView layout_user1;
    private TextView layout_user2;
    private TextView layout_user3;
    private TextView layout_user4;
    private TextView layout_user5;
    private TextView layout_user6;
    private TextView layout_user7;
    private TextView layout_user8;
    private TextView layout_user9;
    private TextView layout_user10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initView();

    }

    private void initView() {

        //mainActivity에서 전달한 정보들 받아오기
        intent = getIntent();
        userID = intent.getStringExtra("userID");
        userPassword = intent.getStringExtra("userPassword");
        userName = intent.getStringExtra("userName");
        step_count = intent.getIntExtra("step_count", 0);
        trash_count = intent.getIntExtra("trash_count", 0);
        total = intent.getIntExtra("total", 0);
        now_rank = intent.getIntExtra("now_rank", 0);
        best_rank = intent.getIntExtra("best_rank", 0);
        now_step_count = intent.getIntExtra("now_step_count", 0);

        layout_userName = findViewById(R.id.ranking_userName);
        layout_userName.setText(userName);

        layout_nowRank = findViewById(R.id.ranking_nowRank);
        layout_nowRank.setText(now_rank+"위");

        layout_bestRank = findViewById(R.id.ranking_bestRank);
        layout_bestRank.setText(best_rank+"위");

        layout_trashCnt = findViewById(R.id.ranking_trashCnt);
        layout_trashCnt.setText(trash_count+"회");

        layout_stepCnt = findViewById(R.id.ranking_stepCnt);
        layout_stepCnt.setText(step_count+"보");
        layout_nowStepCnt = findViewById(R.id.ranking_nowStepCnt);
        layout_nowStepCnt.setText("(+"+now_step_count+")");


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    Log.i("-------------","response-------------"+response);

                    JSONArray jsonArray = new JSONArray(response);

                    String[] rankers = new String[10];
                    for (int i=0; i<10; i++){
                        rankers[i]=jsonArray.getString(i);
                    }

                    layout_user1 = findViewById(R.id.ranking_user1);
                    layout_user1.setText("1. "+rankers[0]);
                    layout_user2 = findViewById(R.id.ranking_user2);
                    layout_user2.setText("2. "+rankers[1]);
                    layout_user3 = findViewById(R.id.ranking_user3);
                    layout_user3.setText("3. "+rankers[2]);
                    layout_user4 = findViewById(R.id.ranking_user4);
                    layout_user4.setText("4. "+rankers[3]);
                    layout_user5 = findViewById(R.id.ranking_user5);
                    layout_user5.setText("5. "+rankers[4]);
                    layout_user6 = findViewById(R.id.ranking_user6);
                    layout_user6.setText("6. "+rankers[5]);
                    layout_user7 = findViewById(R.id.ranking_user7);
                    layout_user7.setText("7. "+rankers[6]);
                    layout_user8 = findViewById(R.id.ranking_user8);
                    layout_user8.setText("8. "+rankers[7]);
                    layout_user9 = findViewById(R.id.ranking_user9);
                    layout_user9.setText("9. "+rankers[8]);
                    layout_user10 = findViewById(R.id.ranking_user10);
                    layout_user10.setText("10. "+rankers[9]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RankingRequest rankingRequest = new RankingRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(RankingActivity.this);
        queue.add(rankingRequest);
    }
}