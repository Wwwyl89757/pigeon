package com.example.administrator.pigeon;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import cn.bmob.v3.Bmob;

public class WelcomeActivity extends AppCompatActivity {

    private static final int DELAY = 2000;
    private static final int GO_HOME = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        initLoad();
    }

    private void initLoad() {
        handler.sendEmptyMessageDelayed(GO_HOME, DELAY);
    }

    private void goHome() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            goHome();
        }
    };

}
