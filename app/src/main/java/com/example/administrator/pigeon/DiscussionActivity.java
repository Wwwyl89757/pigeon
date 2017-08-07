package com.example.administrator.pigeon;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.SideBarAdapter;
import adapter.SideBarDiscussAdapter;
import bean.FriendsCircle;
import bean.PhotoItem;
import bean.SortBean;
import bean.User;
import cn.bmob.v3.datatype.BmobFile;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import model.FriendCircleModel;
import myapp.MyApp;
import view.SideBar;

public class DiscussionActivity extends AppCompatActivity {

    @ViewInject(R.id.list_discus_all)
    private ListView sortListView;
    @ViewInject(R.id.text_discus_dialog)
    private TextView dialog;
    @ViewInject(R.id.sidebar_discus_letter)
    private SideBar sideBar;

    private SideBarDiscussAdapter adapter;
    private List<SortBean> sourceDateList;
    private List<String> groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        init();

    }

    private void init() {
        ViewUtils.inject(this);
        groupId = new ArrayList<>() ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("发起群聊");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        sourceDateList = (List)intent.getSerializableExtra("sourceDateList");
        sourceDateList.remove(0);
        sourceDateList.remove(0);
        adapter = new SideBarDiscussAdapter(this, sourceDateList);
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                //将第position个item显示在listView的最上面一项
                if(position != -1){
                    sortListView.setSelection(position);
                }
                if(s.equals("☆")){
                    sortListView.setSelection(4);
                }
            }
        });
       sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               adapter.itemSelect(position);
           }
       });
        sortListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_selected:
                for (SortBean member : sourceDateList){
                    if (member.isCheckBox()){
                        groupId.add(member.getUser().getObjectId());
                    }
                }
                RongIM.getInstance().createDiscussionChat(this, groupId, "群聊", new RongIMClient.CreateDiscussionCallback() {
                    @Override
                    public void onSuccess(String s) {
//                        RongIM.getInstance().startDiscussionChat(DiscussionActivity.this, s, "群聊");
                        DiscussionActivity.this.finish();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.i("error",errorCode.getMessage());
                    }
                });
                break;
        }
        return true;
    }

}
