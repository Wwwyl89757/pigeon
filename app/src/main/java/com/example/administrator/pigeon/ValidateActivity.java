package com.example.administrator.pigeon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import bean.NewFriend;
import bean.User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import model.UserModel;
import myapp.MyApp;

public class ValidateActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.agree_image)
    public void onClickValidate(View view){
        Intent intent = this.getIntent();
        NewFriend friend = (NewFriend) intent.getSerializableExtra("newfriend");
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
