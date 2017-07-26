package util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.pigeon.MainActivity;
import com.example.administrator.pigeon.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.AddFriendMessage;
import bean.AgreeAddFriendMessage;
import bean.NewFriend;
import bean.User;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import config.Config;
import event.RefreshEvent;
import io.rong.eventbus.EventBus;
import manager.NewFriendManager;
import model.UserModel;

/**
 * Created by Administrator on 2017/7/25.
 */

public class MyMessageHandler extends BmobIMMessageHandler {

    private Context context;

    public MyMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
        //当接收到服务器发来的消息时，此方法被调用
//        Logger.i(event.getConversation().getConversationTitle() + "," + event.getMessage().getMsgType() + "," + event.getMessage().getContent());
//        excuteMessage(event);
        //当接收到服务器发来的消息时，此方法被调用
        //可以统一在此检测更新会话及用户信息
        Toast.makeText(context,"接收消息",Toast.LENGTH_SHORT).show();
        processCustomMessage(event.getMessage(),event.getFromUserInfo());
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
        Map<String,List<MessageEvent>> map =event.getEventMap();
//        Logger.i("离线消息属于" + map.size() + "个用户");
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list =entry.getValue();
            int size = list.size();
            for(int i=0;i<size;i++){
//                excuteMessage(list.get(i));
            }
        }
    }


    /**
     * 显示对方添加自己为好友的通知
     * @param friend
     */
    private void showAddNotify(NewFriend friend){
        Intent pendingIntent = new Intent(context, MainActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //这里可以是应用图标，也可以将聊天头像转成bitmap
        Bitmap largetIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        BmobNotificationManager.getInstance(context).showNotification(largetIcon,
                friend.getName(), friend.getMsg(), friend.getName() + "请求添加你为朋友", pendingIntent);
//        Toast.makeText(context,"showBmobNotification",Toast.LENGTH_SHORT).show();

        sendAgreeAddFriendMessage(friend, new SaveListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e == null){
                    Toast.makeText(context,"同意添加好友信息发送成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"同意添加好友信息发送失败",Toast.LENGTH_SHORT).show();
                    Log.i("error:",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    /**
     * 显示对方同意添加自己为好友的通知
     * @param info
     * @param agree
     */
    private void showAgreeNotify(BmobIMUserInfo info, AgreeAddFriendMessage agree){
        Intent pendingIntent = new Intent(context, MainActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bitmap largetIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        BmobNotificationManager.getInstance(context).showNotification(largetIcon,info.getName(),agree.getMsg(),agree.getMsg(),pendingIntent);

    }

    /**
     * 添加对方为自己的好友
     * @param uid
     */
    private void addFriend(String uid){
        User user =new User();
        user.setObjectId(uid);
        UserModel.getInstance(context).agreeAddFriend(user, new SaveListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if(e == null){
                            Toast.makeText(context,"添加好友成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,"添加好友失败",Toast.LENGTH_SHORT).show();
                            Log.i("error:",e.getErrorCode()+e.getMessage());
                        }
                    }
                }

           );
    }

    private void processCustomMessage(BmobIMMessage msg, BmobIMUserInfo info) {
//
//        Log.i("accept：","接收到消息");
        String type = msg.getMsgType();
        //发送页面刷新的广播
//        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new RefreshEvent());
        //处理消息
        if (type.equals("add")) {//接收到的添加好友的请求

            NewFriend friend = AddFriendMessage.convert(msg);
            //本地好友请求表做下校验，本地没有的才允许显示通知栏--有可能离线消息会有些重复
            long id = NewFriendManager.getInstance(context).insertOrUpdateNewFriend(friend);
//            Toast.makeText(context,id+"",Toast.LENGTH_SHORT).show();
            if (id > 0) {
//                Toast.makeText(context,"接收消息",Toast.LENGTH_SHORT).show();
                showAddNotify(friend);
            }
        } else if (type.equals("agree")) {//接收到的对方同意添加自己为好友,此时需要做的事情：1、添加对方为好友，2、显示通知
            AgreeAddFriendMessage agree = AgreeAddFriendMessage.convert(msg);
            addFriend(agree.getFromId());//添加消息的发送方为好友
            //这里应该也需要做下校验--来检测下是否已经同意过该好友请求，我这里省略了
            showAgreeNotify(info, agree);
        } else {
            Toast.makeText(context, "接收到的自定义消息：" + msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra(), Toast.LENGTH_SHORT).show();
        }

        EventBus.getDefault().unregister(this);
    }

    /**
     * 发送同意添加好友的请求
     */
    private void sendAgreeAddFriendMessage(final NewFriend add,final SaveListener listener){
        //发给谁，就填谁的用户信息
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
    //启动一个暂态会话，也就是isTransient为true,表明该会话仅执行发送消息的操作，不会保存会话和消息到本地数据库中，
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,true,null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg =new AgreeAddFriendMessage();
        SharedPreferences preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        msg.setContent("我通过了你的好友验证请求，我们可以开始聊天了!");//---这句话是直接存储到对方的消息表中的
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
}
