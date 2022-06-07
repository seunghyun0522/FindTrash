package com.example.kakaomap_personal;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignUpRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://seyeonbb.dothome.co.kr/Register.php";
    private Map<String, String> parameters;

    public SignUpRequest(String userID, String userPassword, String userName, int step_count, int trash_count, int total, int best_rank, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userName", userName);
        parameters.put("step_count", step_count+"");
        parameters.put("trash_count", trash_count+"");
        parameters.put("total", total+"");
        parameters.put("best_rank", best_rank+"");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }

}
