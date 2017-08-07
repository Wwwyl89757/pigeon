package com.example.administrator.pigeon;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ConversationActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        TextView showname_text = (TextView) findViewById(R.id.showname_text);
        Intent intent = getIntent();
        showname_text.setText(intent.getData().getQueryParameter("title"));
    }

    @OnClick(R.id.back_btn)
    public void onClick(View view){
        finish();
    }
}
