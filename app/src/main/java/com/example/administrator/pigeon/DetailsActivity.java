package com.example.administrator.pigeon;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import bean.Friend;
import bean.User;
import cn.bmob.newim.bean.BmobIMUserInfo;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import model.FriendCircleModel;
import model.UserModel;
import myapp.MyApp;


public class DetailsActivity extends AppCompatActivity {

    @ViewInject(R.id.img_details_avatar)
    ImageView img_details_avatar;
    @ViewInject(R.id.text_details_name)
    TextView text_details_name;
    @ViewInject(R.id.button_details_add)
    Button button_add;
    @ViewInject(R.id.button_details_delete)
    Button button_delete;
    @ViewInject(R.id.img_cricle1)
    ImageView imageView1;
    @ViewInject(R.id.img_cricle2)
    ImageView imageView2;
    @ViewInject(R.id.img_cricle3)
    private ImageView imageView3;
    private User user;
    private boolean isFriend;
    private ImageView[] imgs;
    private FriendCircleModel fcmodel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("frienduser");
        isFriend = intent.getBooleanExtra("isFriend",true);
        if (isFriend){
            button_add.setVisibility(View.GONE);
            button_delete.setVisibility(View.VISIBLE);
        }else {
            button_delete.setVisibility(View.GONE);
            button_add.setVisibility(View.VISIBLE);
        }
        ImageLoader.getInstance().displayImage(user.getAvatar().getUrl(),img_details_avatar, Config.options);
        text_details_name.setText(user.getUsername());
        imgs = new ImageView[]{imageView1,imageView2,imageView3};
        fcmodel = new FriendCircleModel();
        fcmodel.getPhoto(user,imgs);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("详细资料");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.button_startchat)
    public void startChat(View view){
        RongIM.getInstance().startPrivateChat(DetailsActivity.this,user.getObjectId(), user.getUsername());
        finish();
    }

    @OnClick(R.id.button_details_add)
    public void onClickAdd(View view ){
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(),user.getUsername(),user.getAvatar().getUrl());
        UserModel userModel = UserModel.getInstance(DetailsActivity.this);
        userModel.sendFriendRequest(info);
    }

    @OnClick(R.id.button_details_delete)
    public void onClickDelete(View view){
        Log.i("size",MyApp.INSTANCE().getFriendList().size()+"");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除?");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Friend friend : MyApp.INSTANCE().getFriendList()){
                    if (friend.getFriendUser().getObjectId().equals(user.getObjectId())){
                        Log.i("friendId",friend.getObjectId());
                        UserModel.getInstance(DetailsActivity.this).deleteFriend(friend);
                    }
                }
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
