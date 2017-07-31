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

public class SearchActivity extends AppCompatActivity {
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.show_textview)
    public void onClickAddFriend(View view){
        String tx = show_textview.getText().toString();
        String friendName = tx.substring(3,tx.length());
        Toast.makeText(SearchActivity.this,friendName,Toast.LENGTH_SHORT).show();
        //创建查询对象
        BmobQuery<User> query = new BmobQuery<>();
        //添加查询条件
        query.addWhereEqualTo("username", friendName);
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
        BmobIMUserInfo info = new BmobIMUserInfo();
        info.setUserId("FzgjSSSZ");
        info.setName("123");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bmap_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //是搜索框默认展开
        searchItem.expandActionView();
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    dialog_layout.setVisibility(View.GONE);
                    search_layout.setVisibility(View.GONE);
                    none_text.setVisibility(View.GONE);
                }else {
                    dialog_layout.setVisibility(View.VISIBLE);
                    show_textview.setText("搜索："+newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.layout_add)
    public void toDetails(View view){
        boolean isFriend = false;
        Intent intent = new Intent(this, DetailsActivity.class);
        for (User frienduser:MyApp.INSTANCE().getFriendList()){
            if (frienduser.getObjectId().equals(user.getObjectId())){
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
    }
}
