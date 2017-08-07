package com.example.administrator.pigeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.AddFriendMessage;
import bean.Friend;
import bean.User;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import model.UserModel;
import myapp.MyApp;

/**
 * Created by sfc on 2017/7/25.
 */

public class SearchActivity extends AppCompatActivity implements TextWatcher {
    @ViewInject(R.id.show_textview)
    TextView show_textview;
    @ViewInject(R.id.dialog_layout)
    LinearLayout dialog_layout;
    @ViewInject(R.id.layout_add)
    LinearLayout search_layout;
    @ViewInject(R.id.text_search)
    TextView search_text;
    @ViewInject(R.id.search_none)
    TextView none_text;
    @ViewInject(R.id.img_search_avatar)
    ImageView img_avatar;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        View mainActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_search, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(mainActionBarView);
        EditText edit_search = (EditText) mainActionBarView.findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(this);
    }


    @OnClick(R.id.show_textview)
    public void onClickAddFriend(View view){
        String tx = show_textview.getText().toString();
        String phoneNum = tx.substring(3,tx.length());
        //创建查询对象
        BmobQuery<User> query = new BmobQuery<>();
        //添加查询条件
        query.addWhereEqualTo("mobilePhoneNumber", phoneNum);
        //执行查询方法
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                Toast.makeText(SearchActivity.this,list.size()+"",Toast.LENGTH_SHORT).show();
                if (e == null){
//                    dialog_layout.setVisibility(View.GONE);
                    if(list.size() == 0){
                        search_layout.setVisibility(View.GONE);
                        none_text.setVisibility(View.VISIBLE);
                        none_text.setText("该用户不存在");
                    }else {
                        none_text.setVisibility(View.GONE);
                        search_layout.setVisibility(View.VISIBLE);
                        search_text.setText(list.get(0).getUsername());
                        ImageLoader.getInstance().displayImage(list.get(0).getAvatar().getUrl(),img_avatar, Config.options);
                        user = list.get(0);
                    }
                }else {
                    Toast.makeText(SearchActivity.this,"查询好友失败",Toast.LENGTH_SHORT).show();
                    Log.i("error:",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @OnClick(R.id.layout_add)
    public void toDetails(View view){
        boolean isFriend = false;
        Intent intent = new Intent(this, DetailsActivity.class);
        for (Friend friend:MyApp.INSTANCE().getFriendList()){
            if (friend.getFriendUser().getObjectId().equals(user.getObjectId())){
                isFriend = true;
            };
        }
        if (MyApp.INSTANCE().getCurrentuser().getObjectId().equals(user.getObjectId())){
            isFriend = true;
        }
        intent.putExtra("isFriend",isFriend);
        Bundle bundle = new Bundle();
        bundle.putSerializable("frienduser",user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0){
            dialog_layout.setVisibility(View.GONE);
            search_layout.setVisibility(View.GONE);
            none_text.setVisibility(View.GONE);
        }else {
            dialog_layout.setVisibility(View.VISIBLE);
            show_textview.setText("搜索：" + s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
