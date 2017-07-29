package com.example.administrator.pigeon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;

import bean.PhotoItem;
import cn.bmob.v3.datatype.BmobFile;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import model.UserModel;

public class SetInfoActivity extends AppCompatActivity {

    @ViewInject(R.id.img_setAvatar)
    ImageView img_setAvatar;
    PhotoItem photoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setinfo);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.button_setAvatar)
    public void setAvatar(View view){
        ImageSelectorActivity.start(SetInfoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, true,true,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){

            ArrayList<String> pathList = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
//            startActivity(new Intent(this,SelectResultActivity.class).putExtra(SelectResultActivity.EXTRA_IMAGES,images));
            photoItem = new PhotoItem(pathList.get(0),false);
            BmobFile avatar = new BmobFile(new File(photoItem.getFilePath()));
            UserModel.getInstance(this).setAvatar(avatar);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("file:///" + photoItem.getFilePath(), img_setAvatar, Config.options);
        }
    }
}
