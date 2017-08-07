package com.example.administrator.pigeon;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import config.Config;
import io.rong.imageloader.core.ImageLoader;
import myapp.MyApp;

public class QRActivity extends AppCompatActivity {
    @ViewInject(R.id.qr_code)
    private ImageView qr_code;
    @ViewInject(R.id.img_qr_avatar)
    private ImageView img_qr_avatar;
    @ViewInject(R.id.qr_user_name)
    private TextView qr_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        Bitmap bitmap = EncodingUtils.createQRCode(MyApp.INSTANCE().getCurrentuser().getObjectId(), 500, 500, null);
        qr_code.setImageBitmap(bitmap);
        qr_user_name.setText(MyApp.INSTANCE().getCurrentuser().getUsername());
        ImageLoader.getInstance().displayImage(MyApp.INSTANCE().getUserAvatarUrl(), img_qr_avatar, Config.options);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
}
