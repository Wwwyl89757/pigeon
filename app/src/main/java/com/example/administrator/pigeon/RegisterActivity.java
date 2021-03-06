package com.example.administrator.pigeon;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import bean.PhotoItem;
import bean.User;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.bean.BmobSmsState;
import cn.bmob.sms.listener.QuerySMSStateListener;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import config.Config;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imageloader.core.display.RoundedBitmapDisplayer;
import model.UserModel;

public class RegisterActivity extends AppCompatActivity implements TextWatcher {

    @ViewInject(R.id.edit_register_phone)
    EditText edit_register_phone;
    @ViewInject(R.id.edit_register_name)
    EditText edit_register_name;
    @ViewInject(R.id.edit_register_pass)
    EditText edit_register_pass;
    @ViewInject(R.id.edit_repass)
    EditText edit_repass;
    @ViewInject(R.id.image_phone)
    ImageView image_phone;
    @ViewInject(R.id.edit_secode)
    EditText edit_secode;
    @ViewInject(R.id.image_name)
    ImageView image_name;
    @ViewInject(R.id.image_pass)
    ImageView image_pass;
    @ViewInject(R.id.image_repass)
    ImageView image_repass;
    @ViewInject(R.id.button_register)
    Button button_register;
    @ViewInject(R.id.img_avatar)
    ImageView img_avatar;
    @ViewInject(R.id.img_register_bg)
    ImageView img_bg;

    PhotoItem photoItem;
    String SMSCheck;
    String regex;
    int smsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        edit_register_phone.addTextChangedListener(this);
        edit_register_name.addTextChangedListener(this);
        edit_register_pass.addTextChangedListener(this);
        edit_repass.addTextChangedListener(this);
        SMSCheck = "您的验证码是%smscode%，有效期为%ttl%分钟。您正在使用%appname%的验证码。";
        regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.translate_anim);
                img_bg.startAnimation(animation);
            }
        }, 200);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BmobSMS.initialize(this, "7714967b6d8406fb9fe456b2fffff1e3");

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String phone = edit_register_phone.getText().toString().trim();
        String name = edit_register_name.getText().toString().trim();
        String pass = edit_register_pass.getText().toString().trim();
        String repass = edit_repass.getText().toString().trim();
        if (phone.length() > 0) {
            image_phone.setVisibility(View.VISIBLE);
        } else {
            image_phone.setVisibility(View.INVISIBLE);
        }
        if (name.length() > 0) {
            image_name.setVisibility(View.VISIBLE);
        } else {
            image_name.setVisibility(View.INVISIBLE);
        }
        if (pass.length() > 0) {
            image_pass.setVisibility(View.VISIBLE);
        } else {
            image_pass.setVisibility(View.INVISIBLE);
        }
        if (repass.length() > 0) {
            image_repass.setVisibility(View.VISIBLE);
        } else {
            image_repass.setVisibility(View.INVISIBLE);
        }
    }


    @OnCompoundButtonCheckedChange(R.id.checkbox_agree)
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            button_register.setEnabled(true);
        }else{
            button_register.setEnabled(false);
        }
    }

    @OnClick({R.id.image_phone,R.id.image_name, R.id.image_pass,R.id.image_repass})
    private void onClickImage(View view){
        switch (view.getId()) {
            case R.id.image_phone:
                edit_register_phone.setText("");
                break;
            case R.id.image_name:
                edit_register_name.setText("");
                break;
            case R.id.image_pass:
                edit_register_pass.setText("");
                break;
            case R.id.image_repass:
                edit_repass.setText("");
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.button_register)
    private void onClickRegister(View view){
        String name = edit_register_name.getText().toString().replace(" ", "");
        String pass = edit_register_pass.getText().toString();
        String repass = edit_repass.getText().toString();
        String phoneNumber = edit_register_phone.getText().toString().trim();
        String code = edit_secode.getText().toString().trim();
        if (photoItem == null){
            Toast.makeText(RegisterActivity.this,"请上传头像",Toast.LENGTH_SHORT).show();
        }else {
            BmobFile avatar = new BmobFile(new File(photoItem.getFilePath()));
            UserModel userModel = UserModel.getInstance(RegisterActivity.this);
            userModel.register(phoneNumber,code,name,pass,repass,avatar);
        }
    }

    @OnClick(R.id.layout_regis_avatar)
    public void getAvatar(View view){
        ImageSelectorActivity.start(RegisterActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, true,true,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){

            ArrayList<String> pathList = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
//            startActivity(new Intent(this,SelectResultActivity.class).putExtra(SelectResultActivity.EXTRA_IMAGES,images));
            photoItem = new PhotoItem(pathList.get(0),false);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("file:///" + photoItem.getFilePath(), img_avatar, Config.options);
        }
    }


    public void onBackPressed() {
//        //方式一：将此任务转向后台
////        moveTaskToBack(false);
//
//        方式二：返回手机的主屏幕
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
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

    @OnClick(R.id.button_getcode)
    public void getCode(View view){
        String phoneNumber = edit_register_phone.getText().toString().trim();
        if(!Pattern.matches(regex,phoneNumber)){
            Toast.makeText(RegisterActivity.this,"手机号码不符合格式要求",Toast.LENGTH_SHORT).show();
            return;
        }

        BmobSMS.requestSMSCode(this, phoneNumber, SMSCheck, new RequestSMSCodeListener() {

            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                if(e == null){
                    smsId = integer;
                    Toast.makeText(RegisterActivity.this,"ok " + integer,Toast.LENGTH_SHORT).show();
                    Log.e("demo", ""+integer);
                }else {
                    Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
