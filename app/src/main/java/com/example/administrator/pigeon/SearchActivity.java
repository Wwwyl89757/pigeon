package com.example.administrator.pigeon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

import bean.AddFriendMessage;
import bean.User;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by sfc on 2017/7/25.
 */

public class SearchActivity extends AppCompatActivity {
    @ViewInject(R.id.search_edittext)
    EditText search_edittext;
    @ViewInject(R.id.show_textview)
    TextView show_textview;
    @ViewInject(R.id.dialog_layout)
    LinearLayout dialog_layout;
    @ViewInject(R.id.back_imageview2)
    ImageView back_imageview2;
    String txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search_edittext.getText().toString().trim().equals("")){
                    dialog_layout.setVisibility(View.GONE);
                }else{
                    txt = search_edittext.getText().toString().trim();
                    Toast.makeText(SearchActivity.this, txt, Toast.LENGTH_SHORT).show();
                    dialog_layout.setVisibility(View.VISIBLE);
                    show_textview.setText("搜索："+txt);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.back_imageview2)
    public void backClick(View v){
        finish();
    }

    @OnClick(R.id.show_textview)
    public void onClickAddFriend(View view){
        String friendName = search_edittext.getText().toString().trim();
        BmobIMUserInfo info = new BmobIMUserInfo();
        info.setUserId("FzgjSSSZ");
        info.setName("123");
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        //新建一个添加好友的自定义消息实体
        AddFriendMessage msg =new AddFriendMessage();
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String,Object> map =new HashMap<>();
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        map.put("username", preferences.getString("username",""));//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
//        map.put("avatar",currentUser.getAvatar());//发送者的头像
        map.put("userId",preferences.getString("userId",""));//发送者的uid
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    Toast.makeText(SearchActivity.this,"好友请求发送成功，等待验证",Toast.LENGTH_SHORT).show();
                } else {//发送失败
                    Toast.makeText(SearchActivity.this,"发送失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
