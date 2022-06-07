package com.example.kakaomap_personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private ImageButton login_button;
    private EditText edit_id;
    private EditText edit_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //뷰와 객체 연결
        login_button = findViewById(R.id.login_loginButton);
        edit_id = findViewById(R.id.login_id_Edit);
        edit_pw = findViewById(R.id.login_pw_Edit);

        //버튼 클릭 시
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //editText에서 입력한 id와 pw를 가져온다
                String userID = edit_id.getText().toString();
                String userPassword = edit_pw.getText().toString();

                if (userID.equals("admin")){
                    new BackgroundTask().execute();
                }
                else {
                    //HTTP 통신의 구현을 목적으로 개발된 volley API의 핵심 클래스 중 하나인 네트워크 리스너
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);//네트워크에서 가져온 문자열을 저장한 JSON 객체 생성

                                boolean success = jsonObject.getBoolean("success");//해당하는 키의 value 반환 (*)

                                if (success) { // 로그인에 성공한 경우
                                    //해당하는 키의 value 반환
                                    String userID = jsonObject.getString("userID");
                                    String userPassword = jsonObject.getString("userPassword");
                                    String userName = jsonObject.getString("userName"); //오타 수정

                                    //22-05-13 추가
                                    int step_count = Integer.parseInt(jsonObject.getString("step_count"));
                                    int trash_count = Integer.parseInt(jsonObject.getString("trash_count"));
                                    int total = Integer.parseInt(jsonObject.getString("total"));
                                    int best_rank = Integer.parseInt(jsonObject.getString("best_rank"));
                                    //

                                    //application context가 필요한 경우 getApplicationContext()
                                    //activity context가 필요한 경우 this

                                    Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show(); //로그인 성공 알림

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                    //mainActivity로 회원 정보 전달
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("userPassword", userPassword);
                                    intent.putExtra("userName", userName);
                                    intent.putExtra("step_count", step_count);
                                    intent.putExtra("trash_count", trash_count);
                                    intent.putExtra("total", total);
                                    intent.putExtra("best_rank", best_rank);

                                    //송신
                                    //Intent putExtra (String name, int value)
                                    //Intent putExtra (String name, String value)
                                    //Intent putExtra (String name, boolean value)
                                    //수신
                                    //int getIntExtra (String name, int defaultValue)
                                    //String getStringExtra(String name)
                                    //boolean getBooleanExtra (String name, boolean defaultValue)

                                    //회원 정보 잘 가져왔나 확인
                                    //Toast.makeText(getApplicationContext(),userID+" "+total ,Toast.LENGTH_SHORT).show();

                                    startActivity(intent); //다른 액티비티로 이동


                                } else { // 로그인에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show(); //로그인 실패 알림
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    };

                    LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener); // LoginRequest.java 참고
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(loginRequest);
                }

                //Request 객체를 만들고, 이 객체를 RequestQueue에 넣어줍니다.
                //그러면 RequestQueue가 알아서 스레드를 생성하여 서버에 요청하고 응답을 받아줍니다.
                //응답이 오면 RequestQueue에서 Request에 등록된 ResponseListener로 응답을 전달해줍니다.
                //또한 만일 응답을 받았을 때 처리할 메서드를 만들었다면 응답이 왔을 때 그 메서드가 자동으로 호출됩니다.

            }

        });

    }
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://seyeonbb.dothome.co.kr/Manage_List.php";
        }

        @Override
        protected String doInBackground(Void... voids) {

            try{
                URL url = new URL(target);//URL 객체 생성

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();

                //한줄씩 읽어서 stringBuilder에 저장함
                while((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(LoginActivity.this, ManagementActivity.class);
            intent.putExtra("userList", result);//파싱한 값을 넘겨줌
            LoginActivity.this.startActivity(intent);//ManagementActivity로 넘어감

        }

    }
}