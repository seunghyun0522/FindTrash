package com.example.kakaomap_personal;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://seyeonbb.dothome.co.kr/Update.php";
    private Map<String, String> parameters;

    public UpdateRequest(String userID, int step_count, int trash_count, int total, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("step_count", step_count+"");
        parameters.put("trash_count", trash_count+"");
        parameters.put("total", total+"");
        parameters.put("userID", userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }

}
