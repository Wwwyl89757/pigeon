package model;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.pigeon.LoginActivity;
import com.example.administrator.pigeon.MainActivity;
import com.example.administrator.pigeon.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import adapter.SideBarAdapter;
import bean.AddFriendMessage;
import bean.AgreeAddFriendMessage;
import bean.Friend;
import bean.NewFriend;
import bean.SortBean;
import bean.User;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import config.Config;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import manager.NewFriendManager;
import myapp.MyApp;
import util.CharacterParser;
import util.PinyinComparator;


/**
 * Created by Administrator on 2017/7/25.
 */

public class UserModel extends BaseModel {

    private  static  Context context;
    public static UserModel getInstance(Context context) {
        return new UserModel(context);
    }
    private static final UserModel instance = new UserModel(context);
    private UserModel(Context context) {
        this.context = context;
    }

    /** 登录
     * @param username
     * @param password
     */
    public void login(final String username, String password) {

        final User user =new User();
        user.setUsername(username);
        user.setPassword(password);
        if("".equals(username.trim()) || "".equals(password.trim())){
            Toast.makeText(context,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();;
        }else {
            //创建查询对象
            BmobQuery<User> query = new BmobQuery<>();
            //添加查询条件
            query.addWhereEqualTo("username", username);
            query.addWhereEqualTo("password", password);
            //执行查询方法
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e == null){
                        if(list.size() != 0){
//                        Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();

                            getToken(list.get(0));
                        }else {
                            Toast.makeText(context,"登陆失败",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        Toast.makeText(context,"连接服务器错误",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getToken(final User user) {
        String result = "";
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        //构建JSONObject来传递表单参数到Bmob云端逻辑
        JSONObject cloudCodeParams = new JSONObject();
        try {
            cloudCodeParams.put("userId", user.getObjectId());
            cloudCodeParams.put("name", user.getUsername());
            cloudCodeParams.put("portraitUri", "http://g.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=22d43f5f92eef01f4d4110c1d5ceb513/1b4c510fd9f9d72af6029626d22a2834349bbba3.jpg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ace.callEndpoint("getToken", cloudCodeParams, new CloudCodeListener() {
            @Override
            public void done(Object object, BmobException e) {
                if (e == null) {
                    String result = object.toString();
                    Log.i("云端逻辑返回值：" , result);
//                    Toast.makeText(LoginActivity.this,result,Toast.LENGTH_SHORT).show();
                    //对返回值进行Json解析
                    String token = doJson(result);
                    //通过token进行连接
                    connect(token,user);
                } else {
                    Log.i(" error:" , e.getMessage());
                }
            }
        });
    }

    private void connect(String token, final User user) {

        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Log.i("BmobConnect","BmobIMconnect success");
                } else {
                    Log.i("BmobConnect",e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });

        if (context.getApplicationInfo().packageName.equals(MyApp.getCurProcessName(context.getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess" + userid);
                    SharedPreferences preferences=context.getSharedPreferences("user",Context.MODE_PRIVATE);
                    if(preferences.getString("token","").equals("") ){
                        MyApp.INSTANCE().setCurrentuser(user);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("token", MyApp.INSTANCE().getToken());
                        editor.putString("username",MyApp.INSTANCE().getCurrentuser().getUsername());
                        editor.putString("userId",MyApp.INSTANCE().getCurrentuser().getObjectId());
                        editor.putString("avatarUrl",user.getAvatar().getUrl());
                        editor.commit();
                    }
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(context,errorCode+"",Toast.LENGTH_SHORT).show();
                    Log.i("errorCode",errorCode+"");
                }
            });
        }
    }

    private String doJson(String result) {
        String token = "";
        result = result.replace("\\","");
        result = result.substring(1,result.length()-1);
        Log.d("result",result);
        try {
            JSONObject resultObj = new JSONObject(result);
            token = resultObj.getString("token");
            MyApp.INSTANCE().setToken(token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 退出登录
     */
    public void logout(){
        User.logOut();
    }

    public User getCurrentUser(){

        return (User) User.getCurrentUser();
    }
    /**
     * @param username
     * @param password
     * @param pwdagain
     */
    public void register(String username, String password, String pwdagain, BmobFile avatar) {
        if(username.length()>0 && password.length()>0 && pwdagain.length()>0 ){
            if(Pattern.matches("[0-9A-Za-z]{6,16}", password)){
                if (password.equals(pwdagain)){
                    checkInDatabase(username,password,avatar);
                }else{
                    Toast.makeText(context, "密码不一致", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "密码不符合格式要求", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "注册信息请填写完整", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInDatabase(final String username, final String pass,final BmobFile avatar) {
        //创建查询对象
        BmobQuery<User> query = new BmobQuery<>();
        //添加查询条件
        query.addWhereEqualTo("username", username);
        //执行查询方法
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null){
                    if(list.size() != 0){
                        Toast.makeText(context,"该账户已经被注册",Toast.LENGTH_SHORT).show();
                    }else {
                        insertIntoDB(username,pass,avatar);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(context,"连接服务器错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertIntoDB(String username, String pass,final BmobFile avatar) {

        final User user = new User();
        user.setUsername(username);
        user.setPassword(pass);
        user.setAvatar(avatar);
        avatar.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    //保存数据
                    user.signUp(new SaveListener<User>(){

                        @Override
                        public void done(User bmobUser, BmobException e) {
                            if(e == null){
                                Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context,LoginActivity.class));
                            }else {
//                    Toast.makeText(RegisterActivity.this,"插入数据库失败",Toast.LENGTH_SHORT).show();
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    }) ;
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });

    }


    /**
     * 同意添加好友：1、发送同意添加的请求，2、添加对方到自己的好友列表中
     */
    public void agreeAddFriend(final User friend){
        Friend f = new Friend();
        f.setUser(MyApp.INSTANCE().getCurrentuser());
        f.setFriendUser(friend);
        f.save(new SaveListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    MyApp.INSTANCE().getFriendList().add(friend);
                    Toast.makeText(context, "添加好友成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "添加好友失败", Toast.LENGTH_SHORT).show();
                    Log.i("error:", e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    /**
     * 查询好友
     * @param data
     */
    public void queryFriends(User user, final List<SortBean> data, final SideBarAdapter adapter){
        final PinyinComparator pinyinComparator = new PinyinComparator();
        BmobQuery<Friend> query = new BmobQuery<>();
//        final User user = (User) User.getCurrentUser();
//        Toast.makeText(context,user.getUsername(),Toast.LENGTH_SHORT).show();
        Log.i("username",user.getUsername());
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(new FindListener<Friend>(){
            @Override
            public void done(List<Friend> list, BmobException e) {
                if(e == null){
                    String[] names = new String[list.size()];
                    String[] avatars = new String[list.size()];
                    Log.i("list.size",list.size()+"");
                    for (int i = 0;i < list.size();i ++){
                        MyApp.INSTANCE().getFriendList().add(list.get(i).getFriendUser());
                        names[i] = list.get(i).getFriendUser().getUsername();
                        avatars[i] = list.get(i).getFriendUser().getAvatar().getUrl();
                    }
                    filledData(data,names,avatars);
                    // 根据a-z进行排序源数据
                    Collections.sort(data, pinyinComparator);
                    data.add(0,new SortBean("新的朋友","↑"));
                    data.add(1,new SortBean("群聊","↑"));
                    adapter.notifyDataSetChanged();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }

            }
        });

    }

    /**
     * 为ListView填充数据
     * @return
     */
    public void filledData(List<SortBean> mSortList, String [] names, String[] avatars){
        CharacterParser characterParser = CharacterParser.getInstance();
        for(int i=0; i<names.length; i++){
            SortBean sortBean = new SortBean();
            sortBean.setName(names[i]);
            sortBean.setFriendPortrait(avatars[i]);
            //sortBean.setCount(0);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(names[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortBean.setFirstLetter(sortString.toUpperCase());
            }else{
                sortBean.setFirstLetter("#");
            }

            mSortList.add(sortBean);
            Log.e("error", sortBean.getFirstLetter());
        }

    }

    /**
     * 删除好友
     */
//    public void deleteFriend(Friend f,DeleteListener listener){
//        Friend friend =new Friend();
//        friend.delete(getContext(),f.getObjectId(),listener);
//    }

    //发送好友请求
    public void sendFriendRequest(BmobIMUserInfo info){
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        //新建一个添加好友的自定义消息实体
        AddFriendMessage msg =new AddFriendMessage();
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String,Object> map =new HashMap<>();
        map.put("username", MyApp.INSTANCE().getCurrentuser().getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
//        map.put("avatar",currentUser.getAvatar());//发送者的头像
        map.put("userId",MyApp.INSTANCE().getCurrentuser().getObjectId());//发送者的uid
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    Toast.makeText(context,"好友请求发送成功，等待验证",Toast.LENGTH_SHORT).show();
                } else {//发送失败
                    Toast.makeText(context,"发送失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 发送同意添加好友的请求
     */
    public void sendAgreeAddFriendMessage(final NewFriend add, final SaveListener listener){
        //发给谁，就填谁的用户信息
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //启动一个暂态会话，也就是isTransient为true,表明该会话仅执行发送消息的操作，不会保存会话和消息到本地数据库中，
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg =new AgreeAddFriendMessage();
        SharedPreferences preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
//        msg.setContent("我通过了你的好友验证请求，我们可以开始聊天了!");//---这句话是直接存储到对方的消息表中的
        Map<String,Object> map =new HashMap<>();
        map.put("msg",preferences.getString("username","")+"同意添加你为好友");//显示在通知栏上面的内容
        map.put("uid",add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e){
                if (e == null) {//发送成功
                    //修改本地的好友请求记录
                    NewFriend newFriend =  new NewFriend();
                    newFriend.setUid(add.getUid());
                    newFriend.setTime(add.getTime());
                    NewFriendManager.getInstance(context).updateNewFriend(newFriend, Config.STATUS_VERIFIED);
                    Toast.makeText(context,"添加好友成功",Toast.LENGTH_SHORT).show();
                } else {//发送失败
                    Log.d("error:",e.getErrorCode()+e.getMessage());
                    Toast.makeText(context,"添加好友失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 添加对方为自己的好友
     * @param uid
     */
    public void addFriend(String uid){
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(uid, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null){
                    agreeAddFriend(user);
                }else {
                    Log.d("eror:",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    /**
     * 修改头像
     */
    public void setAvatar(final BmobFile avatar){

        avatar.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = new User();
                    user.setAvatar(avatar);
                    //保存数据
                    user.update(MyApp.INSTANCE().getCurrentuser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(context, "头像更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "头像更新失败", Toast.LENGTH_SHORT).show();
                                Log.i("error", e.getErrorCode() + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.i("error", e.getErrorCode() + e.getMessage());
                }
            }
        });


    }
}
