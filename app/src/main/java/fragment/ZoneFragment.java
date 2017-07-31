package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.pigeon.DetailsActivity;
import com.example.administrator.pigeon.EditCircleActivity;
import com.example.administrator.pigeon.FriendCircleActivity;
import com.example.administrator.pigeon.R;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import model.UserModel;
import myapp.MyApp;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ZoneFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_zone,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        view.findViewById(R.id.layout_tocircle).setOnClickListener(this);
        view.findViewById(R.id.layout_scan).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_tocircle:
                startActivity(new Intent(getActivity(), FriendCircleActivity.class));
                break;
            case R.id.layout_scan:
                startActivityForResult(new Intent(getActivity(),CaptureActivity.class),0);
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            final String result = data.getExtras().getString("result");
            BmobQuery<User> query = new BmobQuery<>();
            query.getObject(result, new QueryListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null){
                        if (user.getObjectId().equals("")){
                            Toast.makeText(getActivity(),"该用户不存在",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean isFriend = false;
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        for (User frienduser:MyApp.INSTANCE().getFriendList()){
                            if (frienduser.getObjectId().equals(result)){
                                isFriend = true;
                            };
                        }
                        if (MyApp.INSTANCE().getCurrentuser().getObjectId().equals(user.getObjectId())){
                            isFriend = true;
                        }
                        intent.putExtra("isFriend",isFriend);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("frienduser",user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else {
                        Log.i("error",e.getErrorCode()+e.getMessage());
                    }
                }
            });
        }
    }
}
