package com.example.administrator.pigeon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.PhotoChooseAdapter;
import bean.FriendsCircle;
import bean.PhotoItem;
import bean.User;
import cn.bmob.v3.datatype.BmobFile;

import model.FriendCircleModel;
import myapp.MyApp;

public class EditCircleActivity extends AppCompatActivity {


    private final int REQUEST_CODE = 0x01;
    @ViewInject(R.id.edit_content)
    EditText editContent;
    @ViewInject(R.id.edit_gridView)
    GridView gridView;

    private PhotoChooseAdapter mPhotoChooseAdapter;
    private final String LOGINUSER = "loginuser";
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editcircle);

        init();
    }

    private void init() {
        ViewUtils.inject(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mUser = MyApp.INSTANCE().getCurrentuser();
        mPhotoChooseAdapter = new PhotoChooseAdapter(EditCircleActivity.this);
        gridView.setAdapter(mPhotoChooseAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mPhotoChooseAdapter.getCount() - 1) {
                    int mode = ImageSelectorActivity.MODE_MULTIPLE;
                    ImageSelectorActivity.start(EditCircleActivity.this, 6, mode, true ,true ,true);
                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 选择结果回调
        if (requestCode == ImageSelectorActivity.REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            List<PhotoItem> list = new ArrayList<>();
            ArrayList<String> pathList = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            Toast.makeText(EditCircleActivity.this,pathList.size()+"",Toast.LENGTH_SHORT).show();
            if (pathList.size() != 0) {
                for (String path : pathList) {
                    list.add(new PhotoItem(path, false));
                }
            }

            mPhotoChooseAdapter.addData(list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_selected:
                FriendsCircle friendsCircle = new FriendsCircle();
                friendsCircle.setWriter(mUser);
                List<BmobFile> fileList = new ArrayList<>();
                ArrayList<PhotoItem> photoItems = (ArrayList<PhotoItem>) mPhotoChooseAdapter.getData();
                for (int i = 0; i < photoItems.size() - 1; i++) {
                    fileList.add(new BmobFile(new File(photoItems.get(i).getFilePath())));
                }
                friendsCircle.setText(editContent.getText().toString());
                friendsCircle.setPhotoList(fileList);
                new FriendCircleModel().sendDynamicItem(friendsCircle);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
