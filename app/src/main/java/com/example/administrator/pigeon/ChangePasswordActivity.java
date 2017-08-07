package com.example.administrator.pigeon;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by sfc on 2017/8/7.
 */

public class ChangePasswordActivity extends AppCompatActivity {
    @ViewInject(R.id.oldpsw_et)
    EditText oldpsw_et;
    @ViewInject(R.id.newpsw_et)
    EditText newpsw_et;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepsw);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        init();
    }

    private void init() {
        ViewUtils.inject(this);
    }

    @OnClick({R.id.changepswback_img,R.id.changepsw_btn})
    public void chagepswClick(View v){
        switch (v.getId()){
            case R.id.changepsw_btn:
                final ProgressDialog dialog = ProgressDialog.show(this,"","正在提交");
                BmobUser.updateCurrentUserPassword(oldpsw_et.getText().toString(), oldpsw_et.getText().toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            if(newpsw_et.getText().toString().equals(oldpsw_et.getText().toString())){
                                dialog.dismiss();
                                getFocus(newpsw_et);
                                newpsw_et.setError("新密码不能和旧密码相同");
                            }else {
                                if (Pattern.matches("[0-9A-Za-z]{6,16}",newpsw_et.getText().toString())){
                                    BmobUser.updateCurrentUserPassword(oldpsw_et.getText().toString(), newpsw_et.getText().toString(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            dialog.dismiss();
                                            if (e == null){
                                                Toast.makeText(ChangePasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }else {
                                                Toast.makeText(ChangePasswordActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                                                Log.d("abc", e.getErrorCode()+e.getMessage());
                                            }
                                        }
                                    });
                                }else{
                                    dialog.dismiss();
                                    getFocus(newpsw_et);
                                    newpsw_et.setError("密码为空或者格式错误");
                                }
                            }
                        }else{
                            dialog.dismiss();
                            getFocus(oldpsw_et);
                            oldpsw_et.setError("密码错误");
                        }
                    }
                });
                break;
            case R.id.changepswback_img:
                finish();
                break;
        }
    }

    public void getFocus(View v){
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.requestFocusFromTouch();
    }
}
