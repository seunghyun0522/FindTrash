package com.example.kakaomap_personal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class UserListAdapter extends BaseAdapter {

    private Context context;
    private List<User> userList;
    private Activity parentActivity;

    //생성자 생성
    public UserListAdapter(Context context, List<User> userList, Activity parentActivity){
        this.context = context;
        this.userList = userList;
        this.parentActivity = parentActivity;
    }

    @Override
    public int getCount() {
        //현재사용자의 개수 반환
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //그대로 반환
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //하나의 사용자에대한 view를 보여주는 부분
        //한명의 사용자에대한 view가 만들어진다.
        View v = View.inflate(context,R.layout.user,null);
        final TextView userID = (TextView)v.findViewById(R.id.userID);
        TextView userPassword = (TextView)v.findViewById(R.id.userPassword);
        TextView userName = (TextView)v.findViewById(R.id.userName);
        TextView userTotal = (TextView)v.findViewById(R.id.userTotal);

        userID.setText(userList.get(position).getUserID());
        userPassword.setText(userList.get(position).getUserPassword());
        userName.setText(userList.get(position).getUserName());
        userTotal.setText(userList.get(position).getUserTotal());

        //특정 user에 아이디값을 그대로 반환할수 있게 해준다
        v.setTag(userList.get(position).getUserID());

        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("------------",response);
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("duplicate");
                            if(success){
                                userList.remove(position);
                                notifyDataSetChanged(); //데이터가 변경된 것을 어뎁터에 알려준다
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                DeleteRequest deleteRequest = new DeleteRequest(userID.getText().toString(),responseListener);
                RequestQueue queue = Volley.newRequestQueue(parentActivity);
                queue.add(deleteRequest);
            }
        });


        return v;

    }
}