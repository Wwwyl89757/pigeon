package com.example.administrator.pigeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import bean.User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import myapp.MyApp;

/**
 * Created by sfc on 2017/8/4.
 */

public class ChangeNameActivity extends AppCompatActivity {
    @ViewInject(R.id.changenickname_et)
    EditText nickName;
    @ViewInject(R.id.savenewname_btn)
    Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changename);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getExtras();
        String oldname = bundle.getString("oldname");
        nickName.setText(oldname);
        nickName.setSelection(nickName.getText().toString().length());
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(true);
            }
        });
    }

    @OnClick({R.id.changeback_img,R.id.savenewname_btn})
    public void changeClick(View v){
        switch (v.getId()){
            case R.id.changeback_img:
                finish();
                break;
            case R.id.savenewname_btn:
                MyApp.INSTANCE().getCurrentuser().setUsername(nickName.getText().toString());
                User user = new User();
                user.setUsername(nickName.getText().toString());
                user.setSessionToken(MyApp.INSTANCE().getCurrentuser().getSessionToken());
                user.update(MyApp.INSTANCE().getCurrentuser().getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            SharedPreferences preferences = ChangeNameActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username",MyApp.INSTANCE().getCurrentuser().getUsername());
                            editor.commit();
                        }else{
                            Log.i("error", e.getErrorCode() + e.getMessage());
                        }
                    }
                });
                finish();
                break;
        }
    }
}
