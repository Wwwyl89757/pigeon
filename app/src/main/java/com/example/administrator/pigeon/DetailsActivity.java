package com.example.administrator.pigeon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import bean.User;
import cn.bmob.newim.bean.BmobIMUserInfo;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import model.UserModel;
import myapp.MyApp;

public class DetailsActivity extends AppCompatActivity {

    @ViewInject(R.id.img_details_avatar)
    ImageView img_details_avatar;
    @ViewInject(R.id.text_details_name)
    TextView text_details_name;
    @ViewInject(R.id.button_details_add)
    Button button_add;

    User user;
    boolean isFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ViewUtils.inject(this);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("frienduser");
        isFriend = intent.getBooleanExtra("isFriend",true);
        if (isFriend){
            button_add.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(user.getAvatar().getUrl(),img_details_avatar, Config.options);
        text_details_name.setText(user.getUsername());
    }

    @OnClick(R.id.button_startchat)
    public void startChat(View view){
        RongIM.getInstance().startPrivateChat(DetailsActivity.this,user.getObjectId(), user.getUsername());
        finish();
    }

    @OnClick(R.id.button_details_add)
    public void onClickAdd(View view ){
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(),user.getUsername(),user.getAvatar().getUrl());
        Log.i("BmobIMUserInfo",user.getObjectId()+user.getUsername());
        UserModel userModel = UserModel.getInstance(DetailsActivity.this);
        userModel.sendFriendRequest(info);
    }
}
