package com.example.administrator.pigeon;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import model.UserModel;
import myapp.MyApp;

/**
 * Created by Administrator on 2017/8/4.
 */

public class ForgetPswActivity extends AppCompatActivity implements TextWatcher {
    @ViewInject(R.id.mp_edit_phone)
    EditText mp_edit_phone;
    @ViewInject(R.id.mp_edit_pass)
    EditText mp_edit_pass;
    @ViewInject(R.id.mp_edit)
    EditText mp_edit;
    @ViewInject(R.id.mp_image_phone)
    ImageView mp_image_phone;
    @ViewInject(R.id.mp_edit_secode)
    EditText mp_edit_secode;
    @ViewInject(R.id.mp_image_pass)
    ImageView mp_image_pass;
    @ViewInject(R.id.mp_image)
    ImageView mp_image;
    @ViewInject(R.id.mp_button_register)
    Button mp_button_register;
    @ViewInject(R.id.mp_img_bg)
    ImageView img_bg;
    @ViewInject(R.id.mp_button_getcode)
    Button mp_button_getcode;

    String SMSCheck;
    String regex;
    int smsId;

    String phoneNumber;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpsw);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mp_edit_phone.addTextChangedListener(this);
        mp_edit_pass.addTextChangedListener(this);
        mp_edit.addTextChangedListener(this);
        SMSCheck = "您的验证码是%smscode%，有效期为%ttl%分钟。您正在使用%appname%的验证码。";
        regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(ForgetPswActivity.this, R.anim.translate_anim);
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

    //清除输入框
    @Override
    public void afterTextChanged(Editable s) {
        String phone = mp_edit_phone.getText().toString().trim();
        String pass = mp_edit_pass.getText().toString().trim();
        String repass = mp_edit.getText().toString().trim();
        if (phone.length() > 0) {
            mp_image_phone.setVisibility(View.VISIBLE);
        } else {
            mp_image_phone.setVisibility(View.INVISIBLE);
        }
        if (pass.length() > 0) {
            mp_image_pass.setVisibility(View.VISIBLE);
        } else {
            mp_image_pass.setVisibility(View.INVISIBLE);
        }
        if (repass.length() > 0) {
            mp_image.setVisibility(View.VISIBLE);
        } else {
            mp_image.setVisibility(View.INVISIBLE);
        }
    }


    @OnClick({R.id.mp_image_phone,R.id.mp_image_pass,R.id.mp_image,
            R.id.mp_button_getcode,R.id.mp_button_register})
    private void onClickImage(View view){
        switch (view.getId()) {
            case R.id.mp_image_phone:
                mp_edit_phone.setText("");
                break;
            case R.id.mp_image_pass:
                mp_edit_pass.setText("");
                break;
            case R.id.mp_image:
                mp_edit.setText("");
                break;
            case R.id.mp_button_register:
                messageValidation();
                break;
            default:
                break;
        }
    }



    public void messageValidation(){

        String code = mp_edit_secode.getText().toString().trim();
        String pass = mp_edit_pass.getText().toString().trim();
        String repass = mp_edit.getText().toString().trim();
        if(pass.equals(repass)){
            //重置密码

            user.resetPasswordBySMSCode(mp_edit_secode.getText().toString().trim()+"",
                    mp_edit_pass.getText().toString().trim(), new UpdateListener(){
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                startActivity(new Intent(ForgetPswActivity.this,LoginActivity.class));
                                finish();
                                Toast.makeText(ForgetPswActivity.this, "成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ForgetPswActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                Log.e("error","===="+e.getMessage());
                            }

                        }
                    });
        }else {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
        }

    }



    public void selectPhone(){
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("mobilePhoneNumber", mp_edit_phone.getText().toString().trim());
        query.findObjects(new FindListener<User>() {

            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null){
                    if(list.size() ==0){
                        Toast.makeText(ForgetPswActivity.this, "该用户还未注册", Toast.LENGTH_SHORT).show();
                    }else {
                        user = list.get(0);
                        Log.e("error",user.getObjectId()+user.getUsername());
//                        messageValidation();
                    }
                }else {
                    Log.e("error","e = "+e.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.mp_button_getcode)
    public void getCode(View view){
        phoneNumber = mp_edit_phone.getText().toString().trim();
        if(!Pattern.matches(regex,phoneNumber)){
            Toast.makeText(ForgetPswActivity.this,"手机号码不符合格式要求",Toast.LENGTH_SHORT).show();
            return;
        }

        BmobSMS.requestSMSCode(this, phoneNumber, SMSCheck, new RequestSMSCodeListener() {

            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                if(e == null){
                    smsId = integer;
                    Toast.makeText(ForgetPswActivity.this,"ok " + integer,Toast.LENGTH_SHORT).show();
                    Log.e("demo", ""+integer);
                }else {
                    Toast.makeText(ForgetPswActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}