package fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.pigeon.DetailsActivity;
import com.example.administrator.pigeon.DiscussionActivity;
import com.example.administrator.pigeon.R;
import com.example.administrator.pigeon.SearchActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.Friend;
import bean.SortBean;
import bean.User;
import io.rong.imkit.RongIM;
import model.UserModel;
import myapp.MyApp;
import util.PinyinComparator;
import view.SideBar;
import adapter.SideBarAdapter;

public class ChatFragment extends Fragment{

    private List<SortBean> sourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private SideBar sideBar;
    private TextView dialog;
    private SideBarAdapter adapter;
    private ListView sortListView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_chat,container,false);
        sideBar = (SideBar) view.findViewById(R.id.letter_show);

        dialog = (TextView) view.findViewById(R.id.dialog);
        sortListView = (ListView) view.findViewById(R.id.friend_show);
        sideBar.setTextView(dialog);
        init();
        return view;
    }

    private void init() {
        sourceDateList = new ArrayList<>();
        adapter = new SideBarAdapter(getActivity(), sourceDateList);
        getAllFriend();
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                //将第position个item显示在listView的最上面一项
                if(position != -1){
                    sortListView.setSelection(position);
                }
                if(s.equals("☆")){
                    sortListView.setSelection(4);
                }
            }
        });

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    startActivity(new Intent(getActivity(),SearchActivity.class));
                }else if(position == 1){
                    Intent intent = new Intent(new Intent(getActivity(),DiscussionActivity.class));
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sourceDateList", (Serializable) sourceDateList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    for (Friend friend : MyApp.INSTANCE().getFriendList()){
                        if (sourceDateList.get(position).getUser().equals(friend.getFriendUser())){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("frienduser",friend.getFriendUser());
                            intent.putExtras(bundle);
                        }
                    }
                    startActivity(intent);
                }
            }
        });

        sortListView.setAdapter(adapter);
    }

    private void getAllFriend() {

        if(MyApp.INSTANCE().getFriendList().isEmpty()){
            Log.i("current",MyApp.INSTANCE().getCurrentuser().getObjectId());
            UserModel userModel = UserModel.getInstance(getActivity());
            userModel.queryFriends(MyApp.INSTANCE().getCurrentuser(),sourceDateList,adapter);
        }else {
            UserModel.getInstance(getActivity()).filledData(sourceDateList,MyApp.INSTANCE().getFriendList());
            for (SortBean sortBean : sourceDateList){
                Log.i("sortBean1",sortBean.getFirstLetter());
            }
            // 根据a-z进行排序源数据
            pinyinComparator = new PinyinComparator();
            Collections.sort(sourceDateList, pinyinComparator);
            for (SortBean sortBean : sourceDateList){
                Log.i("sortBean2",sortBean.getFirstLetter());
            }
            User user1 = new User();user1.setUsername("新的朋友");
            sourceDateList.add(0,new SortBean(user1,"↑"));
            User user2 = new User();user2.setUsername("群聊");
            sourceDateList.add(1,new SortBean(user2,"↑"));
            adapter.notifyDataSetChanged();
        }
    }
}
