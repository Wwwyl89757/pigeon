package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pigeon.DiscussionActivity;
import com.example.administrator.pigeon.R;
import com.example.administrator.pigeon.SearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.SortBean;
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
                    startActivity(new Intent(getActivity(),DiscussionActivity.class));
                }else {
                    Toast.makeText(getActivity(),RongIM.getInstance().toString(),Toast.LENGTH_SHORT).show();
                    RongIM.getInstance().startPrivateChat(getActivity(),MyApp.INSTANCE().getFriendList().get(position-2).getObjectId(), "标题");
                }
            }
        });


        //添加尾部好友数
        //View view = LayoutInflater.from(getActivity()).inflate(R.layout.number_friends, null);
        //sortListView.addFooterView(view);

        sortListView.setAdapter(adapter);
    }



    private void getAllFriend() {

        if(MyApp.INSTANCE().getFriendList().isEmpty()){
            UserModel userModel = UserModel.getInstance(getActivity());
            userModel.queryFriends(MyApp.INSTANCE().getCurrentuser(),sourceDateList,adapter);
        }else {
            String[] names = new String[MyApp.INSTANCE().getFriendList().size()];
            String[] avatars = new String[MyApp.INSTANCE().getFriendList().size()];
            for (int i = 0;i < MyApp.INSTANCE().getFriendList().size();i ++){
                names[i] = MyApp.INSTANCE().getFriendList().get(i).getUsername();
                avatars[i] = MyApp.INSTANCE().getFriendList().get(i).getAvatar().getUrl();
            }
            UserModel.getInstance(getActivity()).filledData(sourceDateList,names,avatars);
            // 根据a-z进行排序源数据
            Collections.sort(sourceDateList, pinyinComparator);
            sourceDateList.add(0,new SortBean("新的朋友","↑"));
            sourceDateList.add(1,new SortBean("群聊","↑"));
            adapter.notifyDataSetChanged();
        }


    }


}
