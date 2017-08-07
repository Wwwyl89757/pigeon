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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.pigeon.ChangeNameActivity;
import com.example.administrator.pigeon.ChangePasswordActivity;
import com.example.administrator.pigeon.LoginActivity;
import com.example.administrator.pigeon.QRActivity;
import com.example.administrator.pigeon.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;

import bean.PhotoItem;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.datatype.BmobFile;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import listener.OnFragmentResultListener;
import model.UserModel;
import myapp.MyApp;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/7/21.
 */

public class SelfInfoFragment extends Fragment implements View.OnClickListener, OnFragmentResultListener {

    LinearLayout layout_info_avatar;
    LinearLayout layout_info_qr;
    LinearLayout info_phone_layout;
    LinearLayout info_id_layout;
    LinearLayout info_name_layout;
    TextView textView_logout;
    TextView text_info_phone;
    ImageView img_info_avatar;
    ImageView img_info_qrcode;
    TextView text_info_name;
    TextView text_info_id;
    TextView changepsw;
    PhotoItem photoItem;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_info,container,false);
        textView_logout = (TextView)view.findViewById(R.id.logout);
        text_info_name = (TextView)view.findViewById(R.id.text_info_name);
        text_info_id = (TextView)view.findViewById(R.id.text_info_id) ;
        layout_info_avatar = (LinearLayout)view.findViewById(R.id.layout_info_avatar);
        layout_info_qr = (LinearLayout)view.findViewById(R.id.layout_info_qr);
        info_phone_layout = (LinearLayout) view.findViewById(R.id.info_phone_layout);
        info_id_layout = (LinearLayout) view.findViewById(R.id.info_id_layout);
        info_name_layout = (LinearLayout) view.findViewById(R.id.info_name_layout);
        img_info_avatar = (ImageView)view.findViewById(R.id.img_info_avatar);
        img_info_qrcode = (ImageView)view.findViewById(R.id.img_info_qrcode);
        text_info_phone = (TextView) view.findViewById(R.id.text_info_phone);
        changepsw = (TextView) view.findViewById(R.id.changepsw);

        changepsw.setOnClickListener(this);
        textView_logout.setOnClickListener(this);
        layout_info_avatar.setOnClickListener(this);
        layout_info_qr.setOnClickListener(this);
        info_phone_layout.setOnClickListener(this);
        info_id_layout.setOnClickListener(this);
        info_name_layout.setOnClickListener(this);
        text_info_phone.setText(MyApp.INSTANCE().getCurrentuser().getMobilePhoneNumber());
        text_info_name.setText(MyApp.INSTANCE().getCurrentuser().getUsername());
        text_info_id.setText(MyApp.INSTANCE().getCurrentuser().getObjectId());
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
                        MyApp.INSTANCE().getCurrentuser().logOut();
                        BmobIM.getInstance().disConnect();
                        MyApp.INSTANCE().getFriendList().clear();
                        MyApp.INSTANCE().setUserAvatarUrl("");
                        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.layout_info_avatar:
                ImageSelectorActivity.start(getActivity(), 1, ImageSelectorActivity.MODE_SINGLE, true,true,true);
                break;
            case R.id.info_phone_layout:
                break;
            case R.id.info_id_layout:
                break;
            case R.id.info_name_layout:
                Intent intent = new Intent();
                intent.putExtra("oldname",text_info_name.getText().toString());
                intent.setClass(getActivity(),ChangeNameActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_info_qr:
                startActivity(new Intent(getActivity(),QRActivity.class));
                break;
            case R.id.changepsw:
                startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
                break;
        }

    }

    @Override
    public void OnFragmentResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){
            ArrayList<String> pathList = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            photoItem = new PhotoItem(pathList.get(0),false);
            BmobFile avatar = new BmobFile(new File(photoItem.getFilePath()));
            UserModel.getInstance(getActivity()).setAvatar(avatar);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("file:///" + photoItem.getFilePath(), img_info_avatar, Config.options);
        }
    }

    @Override
    public void onResume() {
        Log.d("abc", "onResume: "+MyApp.INSTANCE().getCurrentuser().getUsername());
        text_info_name.setText(MyApp.INSTANCE().getCurrentuser().getUsername());
        super.onResume();
    }
}

