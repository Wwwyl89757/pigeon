package fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.pigeon.LoginActivity;
import com.example.administrator.pigeon.R;

import cn.bmob.newim.BmobIM;

/**
 * Created by Administrator on 2017/7/21.
 */

public class SelfInfoFragment extends Fragment implements View.OnClickListener {

    TextView textView_logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_info,container,false);
        textView_logout = (TextView)view.findViewById(R.id.logout);
        textView_logout.setOnClickListener(this);
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
                    }
                });
                builder.create();
                builder.show();
                break;
        }
    }
}
