package fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pigeon.DiscussionActivity;
import com.example.administrator.pigeon.R;
import com.example.administrator.pigeon.SearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Friend;
import bean.User;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.NativeObject;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import model.UserModel;
import myapp.MyApp;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listView;
    SimpleAdapter adapter;
    ArrayList<User> user_list;
    ArrayList<HashMap<String, Object>> data;

//    ArrayList<User> user_all;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_chat,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        listView = (ListView) view.findViewById(R.id.chat_listview);
        user_list = new ArrayList<>();
        data = new ArrayList<HashMap<String,Object>>();
        HashMap map1 = new HashMap();
        map1.put("imgId",R.drawable.new_friend);
        map1.put("friendname","新的朋友");
        data.add(map1);
        HashMap map2 = new HashMap();
        map2.put("imgId",R.drawable.group_chat);
        map2.put("friendname","群组");
        data.add(map2);
        adapter = new SimpleAdapter(this.getActivity(),data,R.layout.item_chat,new String[]{"imgId","friendname"},new int[]{R.id.chat_item_img,R.id.chat_item_tx});
        getAllFriend();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void getAllFriend() {
        UserModel userModel = UserModel.getInstance(getActivity());

       userModel.queryFriends(MyApp.INSTANCE().getCurrentuser(),data,adapter);
//        Toast.makeText(getActivity(),user_list.size()+"userlist",Toast.LENGTH_SHORT).show();
//        for (int i = 0; i < user_list.size(); i++){
//            MyApp.INSTANCE().friendIdList.add(user_list.get(i).getObjectId());
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0){
            startActivity(new Intent(getActivity(),SearchActivity.class));
        }else if(position == 1){
            startActivity(new Intent(getActivity(),DiscussionActivity.class));
        }else {
            Toast.makeText(getActivity(),RongIM.getInstance().toString(),Toast.LENGTH_SHORT).show();
            RongIM.getInstance().startPrivateChat(getActivity(),user_list.get(position - 2).getObjectId() , "标题");
        }

    }

}
