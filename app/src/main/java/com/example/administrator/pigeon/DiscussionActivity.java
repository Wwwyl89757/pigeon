package com.example.administrator.pigeon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import myapp.MyApp;

public class DiscussionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.button_discuss)
    public void createDiscuss(View view){

    }

}
