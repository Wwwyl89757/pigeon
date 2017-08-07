package com.example.administrator.pigeon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import bean.NewFriend;
import bean.User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import model.UserModel;
import myapp.MyApp;

public class ValidateActivity extends AppCompatActivity {

    @ViewInject(R.id.img_vali_avatar)
    ImageView img_avatar;
    @ViewInject(R.id.text_vali_name)
    TextView text_name;
    @ViewInject(R.id.text_vali_message)
    TextView text_message;
    NewFriend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        ViewUtils.inject(this);
        Intent intent = this.getIntent();
        friend = (NewFriend) intent.getSerializableExtra("newfriend");
        ImageLoader.getInstance().displayImage(friend.getAvatar(),img_avatar, Config.options);
        text_name.setText(friend.getName());
        text_message.setText(friend.getMsg());
    }

    @OnClick(R.id.agree_image)
    public void onClickValidate(View view){
        UserModel userModel = UserModel.getInstance(this);
        userModel.addFriend(friend.getUid());
        userModel.sendAgreeAddFriendMessage(friend,new SaveListener(){
            @Override
            public void done(Object o, BmobException e) {
                if(e == null){
                    Toast.makeText(ValidateActivity.this,"添加好友信息发送成功",Toast.LENGTH_SHORT).show();

                }else {
//                    Toast.makeText(context,"添加好友信息发送失败",Toast.LENGTH_SHORT).show();
                    Log.i("error:",e.getErrorCode()+e.getMessage());
                }
            }
        });
        finish();
    }
}
