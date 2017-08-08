package com.example.administrator.pigeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import bean.User;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import model.UserModel;
import myapp.MyApp;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class LoginActivity extends AppCompatActivity {
    @ViewInject(R.id.img_login_bg)
    ImageView img_bg;
    @ViewInject(R.id.edit_username)
    EditText editText_phoneNumber;
    @ViewInject(R.id.edit_pass)
    EditText editText_pass;
    @ViewInject(R.id.login_layout)
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                img_bg.startAnimation(animation);
            }
        }, 200);
        SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
        if(!preferences.getString("token","").equals("")){
            MyApp.INSTANCE().setToken(preferences.getString("token",""));
            User currentUser = new User();
            currentUser.setUsername(preferences.getString("username",""));
            currentUser.setObjectId(preferences.getString("userId",""));
            currentUser.setMobilePhoneNumber(preferences.getString("phoneNumber",""));
            currentUser.setSessionToken(preferences.getString("sessionToken",""));
            MyApp.INSTANCE().setCurrentuser(currentUser);
            MyApp.INSTANCE().setUserAvatarUrl(preferences.getString("avatarUrl",""));
            UserModel.getInstance(this).connect(preferences.getString("token",""),currentUser);
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }

    @OnClick(R.id.button_login)
    private void onClickLogin(View view){
        final String phoneNumber = editText_phoneNumber.getText().toString().trim();
        String password = editText_pass.getText().toString().trim();
        UserModel userModel = UserModel.getInstance(this);
        userModel.login(phoneNumber,password);
    }

    @OnClick(R.id.text_fogetpsw)
    public void toForgetPsw(View view){
        startActivity(new Intent(LoginActivity.this,ForgetPswActivity.class));
    }

    @OnClick(R.id.text_toregister)
    public void toRegister(View view){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

//    public void onBackPressed() {
//        //方式一：将此任务转向后台
////        moveTaskToBack(false);
//
////        方式二：返回手机的主屏幕
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
//    }

}
