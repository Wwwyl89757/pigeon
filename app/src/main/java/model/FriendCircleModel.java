package model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.pigeon.FriendCircleActivity;

import java.util.List;

import adapter.FriendCircleAdapter;
import bean.Comment;
import bean.FriendsCircle;
import bean.PhotoItem;
import bean.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import myapp.MyApp;

/**
 * Created by Administrator on 2017/7/27.
 */

public class FriendCircleModel {


    /**
     * 上传动态
     */
    public void sendDynamicItem(final FriendsCircle friendsCircle, final Context context) {
        final ProgressDialog progressDialog = ProgressDialog.show(context,"","正在上传");
        if (friendsCircle.getPhotoList().size() != 0) {
            final String[] array = new String[friendsCircle.getPhotoList().size()];
            for (int i = 0; i < friendsCircle.getPhotoList().size(); i++) {
                array[i] = friendsCircle.getPhotoList().get(i).getLocalFile().getAbsolutePath();
                Log.i("path", "sendDynamicItem: " + array[i] + " " + friendsCircle.getPhotoList().size());
            }
            BmobFile.uploadBatch(array, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list1.size() == array.length) {
                        friendsCircle.setPhotoList(list);
                        friendsCircle.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    context.startActivity(new Intent(context,FriendCircleActivity.class));
                                    ((Activity)context).finish();
                                    Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }else {
                                    Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传失败",Toast.LENGTH_SHORT).show();
                                    Log.i("error",e.getErrorCode()+e.getMessage());
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                    Log.i("TAG", "onProgress: i=" + i + ",i1=" + i1 + ",i2=" + i2 + ",i3=" + i3);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i("TAG", "onError: " + s);
                    progressDialog.dismiss();
                }
            });
        } else {
            friendsCircle.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null){
                        context.startActivity(new Intent(context,FriendCircleActivity.class));
                        ((Activity)context).finish();
                        Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传失败",Toast.LENGTH_SHORT).show();
                        Log.i("error",e.getErrorCode()+e.getMessage());
                        progressDialog.dismiss();
                    }
                }
            });
        }

    }

    /**
     * 获取所有的朋友圈消息
     *
     */
    public void getDynamicItem(final FriendCircleActivity view , final List<FriendsCircle> mlist, final FriendCircleAdapter mAdapter) {
        final BmobQuery<FriendsCircle> query = new BmobQuery<FriendsCircle>();
        query.order("-createdAt");
        query.findObjects(new FindListener<FriendsCircle>() {
            @Override
            public void done(List<FriendsCircle> list, BmobException e) {
                if (e == null){
                    mlist.clear();
                    for (FriendsCircle friendsCircle : list){
//                        queryLikes(friendsCircle,mAdapter,mlist);
                        for (User user : friendsCircle.getWhoLikes()){
                            friendsCircle.setPraiseflag("N");
                            if (MyApp.INSTANCE().getCurrentuser().getObjectId().equals(user.getObjectId())){
                                friendsCircle.setPraiseflag("Y");
                            }
                        }
                        mlist.add(friendsCircle);
                    }
                    mAdapter.notifyDataSetChanged();
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    /**
     * 获取当前用户的所有动态
     *
     * @param user
     */
    public void getDynamicItemByPhone(User user) {
        BmobQuery<FriendsCircle> query = new BmobQuery<FriendsCircle>();
        query.addWhereEqualTo("writer", user);
        query.findObjects(new FindListener<FriendsCircle>() {
            @Override
            public void done(List<FriendsCircle> list, BmobException e) {
                if (e == null){

                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    /**
     * 获取当前用户的朋友圈照片
     *
     * @param user
     */
    public void getPhoto(User user, final ImageView[] imageViews) {
        Log.i("getPhotouser",user.getObjectId()+user.getUsername());
        final ImageLoader imageLoader = ImageLoader.getInstance();
        BmobQuery<FriendsCircle> query = new BmobQuery<FriendsCircle>();
        query.addWhereEqualTo("writer", user);
        query.findObjects(new FindListener<FriendsCircle>() {
            @Override
            public void done(List<FriendsCircle> list, BmobException e) {
                if (e == null){
                    int count = 0;
                    for (int i = list.size() - 1;i >= 0 ; i --){
                        for (BmobFile item : list.get(i).getPhotoList()){
                            imageLoader.displayImage(item.getUrl(),imageViews[count], Config.options);
                            count ++;
                            if (count == 3){return;}
                        }
                    }
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

//    public void queryComments(final FriendsCircle friendsCircle, final FriendCircleAdapter adapter, final List<FriendsCircle> mlist){
//        BmobQuery<Comment> query = new BmobQuery<Comment>();
//        query.addWhereEqualTo("post",friendsCircle);
//        query.findObjects(new FindListener<Comment>() {
//            @Override
//            public void done(List<Comment> list, BmobException e) {
//                if(e==null){
//                    friendsCircle.getComments().clear();
//                    for (Comment comment : list){
//                        User user = comment.getAuthor();
//                        Log.i("username",user.getUsername());
//                        friendsCircle.getComments().add(comment);
//                    }
//                    mlist.add(friendsCircle);
//                    adapter.notifyDataSetChanged();
//                    Log.i("bmob","查询个数："+list.size());
//                }else{
//                    Log.i("bmob","失败："+e.getMessage());
//                }
//            }
//        });
//    }

//    public void queryLikes(final FriendsCircle friendsCircle, final FriendCircleAdapter adapter, final List<FriendsCircle> mlist){
//        BmobQuery<User> query = new BmobQuery<User>();
//        query.addWhereRelatedTo("likes", new BmobPointer(friendsCircle));
//        query.findObjects(new FindListener<User>() {
//            @Override
//            public void done(List<User> list, BmobException e) {
//                if(e==null){
//                    friendsCircle.getWhoLikes().clear();
//                    for (User user : list){
//                        friendsCircle.getWhoLikes().add(user);
//                    }
//                    queryComments(friendsCircle,adapter,mlist);
//                    Log.i("bmob","查询个数："+list.size());
//                }else{
//                    Log.i("bmob","失败："+e.getMessage());
//                }
//            }
//        });
//
//    }

    public void addComments(final Comment comment, final FriendsCircle post, final FriendCircleAdapter adapter){
//        comment.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null){
//                    Log.i("bmob","评论成功");
//                    comment.setObjectId(s);
//                    comments.add(comment);
//                    adapter.notifyDataSetChanged();
//                }else {
//                    Log.i("bmob","失败："+e.getMessage());
//                }
//            }
//        });
        post.getComments().add(comment);
        post.update(post.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    adapter.notifyDataSetChanged();
                }else {
                    Log.i("comment",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    public void addLikes(final User whoLikes, final FriendsCircle post, final FriendCircleAdapter adapter){
//        //将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
//        BmobRelation relation = new BmobRelation();
//        //将当前用户添加到多对多关联中
//        relation.add(whoLikes);
//         //多对多关联指向`post`的`likes`字段
//        post.setLikes(relation);
//        post.update(new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if(e==null){
//                    Log.i("bmob","多对多关联添加成功");
//                    post.getWhoLikes().add(whoLikes);
//                    adapter.notifyDataSetChanged();
//                }else{
//                    Log.i("bmob","失败："+e.getMessage());
//                }
//            }
//        });
        post.getWhoLikes().add(whoLikes);
        post.setPraiseflag("Y");
        post.update(post.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    adapter.notifyDataSetChanged();
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    public void deleteLikes(User currentuser, final FriendsCircle post, final FriendCircleAdapter adapter) {
        for (User user : post.getWhoLikes()){
            if (user.getObjectId().equals(currentuser.getObjectId())){
                post.getWhoLikes().remove(user);
            }
        }
        post.setPraiseflag("N");
        post.update(post.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Log.i("size",post.getWhoLikes().size()+"");
                    adapter.notifyDataSetChanged();
                }else {
                    Log.i("error",e.getErrorCode()+e.getMessage());
                }
            }
        });
    }
}
