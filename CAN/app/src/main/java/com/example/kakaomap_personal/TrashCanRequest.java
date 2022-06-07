package com.example.kakaomap_personal;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TrashCanRequest extends StringRequest {

    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://seyeonbb.dothome.co.kr/getCan.php";
    private Map<String, String> parameters;

    public TrashCanRequest(Double startLatitude, Double startLongitude, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("startLatitude", startLatitude+""); //key=value의 형태로 hashmap에 추가
        parameters.put("startLongitude", startLongitude+"");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }

}
