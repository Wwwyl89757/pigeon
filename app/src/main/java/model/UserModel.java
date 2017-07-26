package model;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.administrator.pigeon.LoginActivity;
import com.example.administrator.pigeon.MainActivity;
import com.example.administrator.pigeon.R;
import com.example.administrator.pigeon.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import bean.Friend;
import bean.User;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import myapp.MyApp;


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
                            SharedPreferences preferences=context.getSharedPreferences("user",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString("username", username);
                            editor.putString("userId", list.get(0).getObjectId());
                            editor.commit();
                            getToken(username,list.get(0).getObjectId());
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

    private void getToken(final String username, final String objectId) {
        String result = "";
        AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
        //第一个参数是上下文对象，第二个参数是云端逻辑的方法名称，第三个参数是上传到云端逻辑的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
        //构建JSONObject来传递表单参数到Bmob云端逻辑
        JSONObject cloudCodeParams = new JSONObject();
        try {
            cloudCodeParams.put("userId", objectId);
            cloudCodeParams.put("name", username);
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
                    connect(token,username,objectId);
                } else {
                    Log.i(" error:" , e.getMessage());
                }
            }
        });
    }

    private void connect(String token, final String username,String objectId) {

        BmobIM.connect(objectId, new ConnectListener() {
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

                    Intent intent = new Intent(getContext(),MainActivity.class);
                    intent.putExtra("userId",userid);
                    intent.putExtra("username",username);
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
    public void register(String username,String password, String pwdagain) {
        if(username.length()>0 && password.length()>0 && pwdagain.length()>0 ){
            if(Pattern.matches("[0-9A-Za-z]{6,16}", password)){
                if (password.equals(pwdagain)){
                    checkInDatabase(username,password);
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

    private void checkInDatabase(final String username, final String pass) {
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
                        insertIntoDB(username,pass);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(context,"连接服务器错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertIntoDB(String username, String pass) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(pass);
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

    }


    /**
     * 同意添加好友：1、发送同意添加的请求，2、添加对方到自己的好友列表中
     */
    public void agreeAddFriend(User friend,SaveListener listener){
        Friend f = new Friend();
        User user = (User) User.getCurrentUser();
        f.setUser(user);
        f.setFriendUser(friend);
        f.save(listener);
    }

    /**
     * 查询好友
     * @param data
     */
    public ArrayList<User> queryFriends(User user,final ArrayList<HashMap<String, Object>> data, final SimpleAdapter adapter){
       final ArrayList<User> users = new ArrayList<>();
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
                Log.i("query","------------------");
                if(e == null){
                    for (Friend friend : list){
                        Log.i("query","+++++++++++");
                        HashMap map = new HashMap();
                        map.put("imgId", R.drawable.chat_avatar);
                        map.put("friendname",friend.getFriendUser().getUsername());
                        users.add(friend.getFriendUser());
                        data.add(map);
                        Log.i("data.size:",data.size()+"");
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        return users;
    }

    /**
     * 删除好友
     * @param f
     * @param listener
     */
//    public void deleteFriend(Friend f,DeleteListener listener){
//        Friend friend =new Friend();
//        friend.delete(getContext(),f.getObjectId(),listener);
//    }
}
