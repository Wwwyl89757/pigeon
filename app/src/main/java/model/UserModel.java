package model;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Freezable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.pigeon.DetailsActivity;
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
import cn.bmob.v3.BmobSMS;
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
import io.rong.imlib.model.UserInfo;
import manager.NewFriendManager;
import myapp.MyApp;
import util.CharacterParser;
import util.PinyinComparator;


/**
 * Created by Administrator on 2017/7/25.
 */

public class  UserModel extends BaseModel {

    private  static  Context context;
    public static UserModel getInstance(Context context) {
        return new UserModel(context);
    }
    private static final UserModel instance = new UserModel(context);
    private UserModel(Context context) {
        this.context = context;
    }
    private ProgressDialog progressDialog;

    /** 登录
     * @param password
     */
    public void login(final String phoneNumber, final String password) {
        progressDialog = ProgressDialog.show(context,"","正在登录");
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber",phoneNumber);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){
                        Toast.makeText(context,"该用户不存在",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else {
                        doLogin(list.get(0).getUsername(),password);
                    }
                }
            }
        });
    }

    private void doLogin(String username,String password) {
        final User loginUser =new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);
        if("".equals(username.trim()) || "".equals(password.trim())){
            Toast.makeText(context,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();;
        }else {
            loginUser.login(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if(e == null){
                        getToken(user);
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        Toast.makeText(context,"登录失败",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
                    //对返回值进行Json解析
                    String token = doJson(result);
                    //通过token进行连接
                    connect(token,user);
                } else {
                    Log.i(" error:" , e.getMessage());
                    progressDialog.dismiss();
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
                    progressDialog.dismiss();
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
                        MyApp.INSTANCE().setUserAvatarUrl(user.getAvatar().getUrl());
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("token", MyApp.INSTANCE().getToken());
                        editor.putString("username",MyApp.INSTANCE().getCurrentuser().getUsername());
                        editor.putString("userId",MyApp.INSTANCE().getCurrentuser().getObjectId());
                        editor.putString("sessionToken",user.getSessionToken());
                        editor.putString("avatarUrl",user.getAvatar().getUrl());
                        editor.putString("phoneNumber",user.getMobilePhoneNumber());
                        editor.commit();
                    }
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                    progressDialog.dismiss();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(context,errorCode+"",Toast.LENGTH_SHORT).show();
                    Log.i("errorCode",errorCode+"");
                    progressDialog.dismiss();
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
    public void register(final String phoneNumber, String code, final String username, final String password, final String pwdagain, final BmobFile avatar) {
        progressDialog = ProgressDialog.show(context,"","注册中");
        BmobSMS.verifySmsCode( phoneNumber, code, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    veryOther(phoneNumber, username, password, pwdagain, avatar);
                } else {
                    Toast.makeText(context, "验证码错误" + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("验证码错误",e.toString()+e.getLocalizedMessage());
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void veryOther(String phoneNumber, String username, String password,String pwdagain, BmobFile avatar) {
        if(username.length()>0 && password.length()>0 && pwdagain.length()>0 ){
            if (Pattern.matches("[0-9A-Za-z]{6,16}", password)) {
                if (password.equals(pwdagain)) {
                    checkInDatabase(phoneNumber,username, password, avatar);
                } else {
                    Toast.makeText(context, "密码不一致", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "密码不符合格式要求", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(context, "注册信息请填写完整", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

    private void checkInDatabase(final String phoneNumber,final String username, final String pass,final BmobFile avatar) {
        //创建查询对象
        BmobQuery<User> query = new BmobQuery<>();
        //添加查询条件
        query.addWhereEqualTo("mobilePhoneNumber", phoneNumber);
        //执行查询方法
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null){
                    if(list.size() != 0){
                        Toast.makeText(context,"该手机号已经被注册",Toast.LENGTH_SHORT).show();
                    }else {
                        insertIntoDB(phoneNumber,username,pass,avatar);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(context,"连接服务器错误",Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void insertIntoDB(String phoneNumber,String username, String pass,final BmobFile avatar) {

        final User user = new User();
        user.setMobilePhoneNumber(phoneNumber);
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
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    }) ;
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
                progressDialog.dismiss();
            }
        });

    }


    /**
     * 同意添加好友：1、发送同意添加的请求，2、添加对方到自己的好友列表中
     */
    public void agreeAddFriend(final User friend){
        for (Friend friend1 : MyApp.INSTANCE().getFriendList()){
            if (friend1.getFriendUser().getObjectId().equals(friend.getObjectId())){
                Toast.makeText(context,"对方已经是您的好友",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        final Friend f = new Friend();
        f.setUser(MyApp.INSTANCE().getCurrentuser());
        f.setFriendUser(friend);
        f.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    f.setObjectId(s);
                    MyApp.INSTANCE().getFriendList().add(f);
                    Toast.makeText(context, "添加好友成功", Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                    context.startActivity(new Intent(context,MainActivity.class));
                } else {
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
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(new FindListener<Friend>(){
            @Override
            public void done(List<Friend> list, BmobException e) {
                if(e == null){
                    Log.i("list.size",list.size()+"");
                    for (int i = 0;i < list.size();i ++){
                        MyApp.INSTANCE().getFriendList().add(list.get(i));
                    }
                    filledData(data,MyApp.INSTANCE().getFriendList());
                    for (SortBean sortBean : data){
                        Log.i("sortBean2",sortBean.getFirstLetter());
                    }
                    // 根据a-z进行排序源数据
                    Collections.sort(data, pinyinComparator);
                    for (SortBean sortBean : data){
                        Log.i("sortBean3",sortBean.getFirstLetter());
                    }
                    User user1 = new User();user1.setUsername("新的朋友");
                    data.add(0,new SortBean(user1,"↑"));
                    User user2 = new User();user2.setUsername("群聊");
                    data.add(1,new SortBean(user2,"↑"));
                    adapter.notifyDataSetChanged();
                    setProvider();
                    RongIM.getInstance().setMessageAttachedUserInfo(true);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }

            }
        });

    }

    //设置内容提供者
    private void setProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {

            @Override
            public UserInfo getUserInfo(String userId) {

                return findUserById(userId);//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
            }

        }, true);
    }

    private UserInfo findUserById(String userId) {
        if (userId.equals(MyApp.INSTANCE().getCurrentuser().getObjectId())){
            return new UserInfo(userId, MyApp.INSTANCE().getCurrentuser().getUsername(),Uri.parse(MyApp.INSTANCE().getCurrentuser().getAvatar().getUrl()) );
        }
        for (Friend friend : MyApp.INSTANCE().getFriendList()){
            if (friend.getFriendUser().getObjectId().equals(userId)){
                return new UserInfo(userId, friend.getFriendUser().getUsername(),Uri.parse(friend.getFriendUser().getAvatar().getUrl()) );
            }
        }
        return null;
    }

    /**
     * 为ListView填充数据
     * @return
     */
    public void filledData(List<SortBean> mSortList, List<Friend> friendList){
        CharacterParser characterParser = CharacterParser.getInstance();
        for(int i=0; i<friendList.size(); i++){
            SortBean sortBean = new SortBean();
            sortBean.setUser(friendList.get(i).getFriendUser());
            //sortBean.setCount(0);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(friendList.get(i).getFriendUser().getUsername());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortBean.setFirstLetter(sortString.toUpperCase());
            }else{
                sortBean.setFirstLetter("#");
            }
            mSortList.add(sortBean);
        }

    }

    /**
     * 删除好友
     */
    public void deleteFriend(final Friend f){
        f.delete(f.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Toast.makeText(context,"删除好友成功",Toast.LENGTH_SHORT).show();
                    MyApp.INSTANCE().getFriendList().remove(f);
                    DetailsActivity activity = (DetailsActivity)context;
                    activity.finish();
                    context.startActivity(new Intent(context,MainActivity.class));
                }else {
                    Toast.makeText(context,"删除好友失败",Toast.LENGTH_SHORT).show();
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    //发送好友请求
    public void sendFriendRequest(BmobIMUserInfo info){
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        //新建一个添加好友的自定义消息实体
        AddFriendMessage msg =new AddFriendMessage();
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String,Object> map =new HashMap<>();
        map.put("username", MyApp.INSTANCE().getCurrentuser().getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("avatar",MyApp.INSTANCE().getUserAvatarUrl());//发送者的头像
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
                    user.setSessionToken(MyApp.INSTANCE().getCurrentuser().getSessionToken());
                    //保存数据
                    user.update(MyApp.INSTANCE().getCurrentuser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Log.i("ObjectId",MyApp.INSTANCE().getCurrentuser().getObjectId());
                            if (e == null) {
                                MyApp.INSTANCE().getCurrentuser().setAvatar(avatar);
                                MyApp.INSTANCE().setUserAvatarUrl(avatar.getUrl());
                                SharedPreferences preferences=context.getSharedPreferences("user",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("avatarUrl",avatar.getUrl());
                                editor.commit();
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

    public void toDetails(final String result){
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(result, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null){
                    if (user.getObjectId().equals("")){
                        Toast.makeText(context,"该用户不存在",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean isFriend = false;
                    Intent intent = new Intent(context, DetailsActivity.class);
                    for (Friend friend:MyApp.INSTANCE().getFriendList()){
                        if (friend.getFriendUser().getObjectId().equals(result)){
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
                    context.startActivity(intent);
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

}
