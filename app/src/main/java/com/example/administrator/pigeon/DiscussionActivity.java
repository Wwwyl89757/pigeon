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
        Toast.makeText(DiscussionActivity.this,MyApp.INSTANCE().friendIdList.size()+"",Toast.LENGTH_SHORT).show();
        RongIM.getInstance().createDiscussionChat(this, MyApp.INSTANCE().friendIdList, "群聊", new RongIMClient.CreateDiscussionCallback() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(DiscussionActivity.this,"讨论组ID" + s,Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.i("error：",errorCode.getMessage());
            }

        });
    }

}
