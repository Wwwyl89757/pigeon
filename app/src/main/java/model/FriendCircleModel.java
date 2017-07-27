package model;

import android.util.Log;
import android.widget.Toast;

import java.util.List;

import bean.FriendsCircle;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import myapp.MyApp;

/**
 * Created by Administrator on 2017/7/27.
 */

public class FriendCircleModel {

    /**
     * 上传动态
     */
    public void sendDynamicItem(final FriendsCircle friendsCircle) {
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
                                    Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传失败",Toast.LENGTH_SHORT).show();
                                    Log.i("error",e.getErrorCode()+e.getMessage());
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
                }
            });
        } else {
            friendsCircle.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null){
                        Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MyApp.INSTANCE().getApplicationContext(),"上传失败",Toast.LENGTH_SHORT).show();
                        Log.i("error",e.getErrorCode()+e.getMessage());
                    }
                }
            });
        }

    }

}
