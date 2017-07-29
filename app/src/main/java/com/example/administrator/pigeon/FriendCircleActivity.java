package com.example.administrator.pigeon;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class FriendCircleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendcircle);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.button_toeditcircle)
    public void toEditCircle(View view){
        startActivity(new Intent(FriendCircleActivity.this,EditCircleActivity.class));
    }
}

