package fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pigeon.LoginActivity;
import com.example.administrator.pigeon.R;
import com.example.administrator.pigeon.RegisterActivity;
import com.example.administrator.pigeon.SetInfoActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.xys.libzxing.zxing.encoding.EncodingUtils;
import com.yongchun.library.view.ImageSelectorActivity;

import java.util.ArrayList;

import bean.PhotoItem;
import cn.bmob.newim.BmobIM;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import myapp.MyApp;

/**
 * Created by Administrator on 2017/7/21.
 */

public class SelfInfoFragment extends Fragment implements View.OnClickListener {

    TextView textView_logout;
    LinearLayout layout_personal;
    ImageView img_info_avatar;
    ImageView img_info_qrcode;
    TextView text_info_name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_info,container,false);
        textView_logout = (TextView)view.findViewById(R.id.logout);
        text_info_name = (TextView)view.findViewById(R.id.text_info_name);
        layout_personal = (LinearLayout)view.findViewById(R.id.layout_personal);
        img_info_avatar = (ImageView)view.findViewById(R.id.img_info_avatar);
        img_info_qrcode = (ImageView)view.findViewById(R.id.img_info_qrcode);
        textView_logout.setOnClickListener(this);
        layout_personal.setOnClickListener(this);
        text_info_name.setText("用户名 : "+MyApp.INSTANCE().getCurrentuser().getUsername()+"\nID : "+MyApp.INSTANCE().getCurrentuser().getObjectId());
        //加载头像
        ImageLoader.getInstance().displayImage(MyApp.INSTANCE().getUserAvatarUrl(), img_info_avatar, Config.options);
        // 设置二维码
        Bitmap bitmap = EncodingUtils.createQRCode(MyApp.INSTANCE().getCurrentuser().getObjectId(), 500, 500, null);
        img_info_qrcode.setImageBitmap(bitmap);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确定退出?");
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        BmobIM.getInstance().disConnect();
                        MyApp.INSTANCE().getFriendList().clear();
                        MyApp.INSTANCE().setUserAvatarUrl("");
                        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.layout_personal:
                startActivity(new Intent(getActivity(),SetInfoActivity.class));
                break;
        }
    }

}
