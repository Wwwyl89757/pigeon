package config;

import android.graphics.Bitmap;

import com.example.administrator.pigeon.R;

import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Administrator on 2017/7/25.
 */

public class Config {

    //是否是debug模式
    public static final boolean DEBUG=true;
    //好友请求：未读-未添加->接收到别人发给我的好友添加请求，初始状态
    public static final int STATUS_VERIFY_NONE=0;
    //好友请求：已读-未添加->点击查看了新朋友，则都变成已读状态
    public static final int STATUS_VERIFY_READED=2;
    //好友请求：已添加
    public static final int STATUS_VERIFIED=1;
    //好友请求：拒绝
    public static final int STATUS_VERIFY_REFUSE=3;
    //好友请求：我发出的好友请求-暂未存储到本地数据库中
    public static final int STATUS_VERIFY_ME_SEND=4;

    public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.xiaolian)
            .showImageForEmptyUri(R.drawable.xiaolian)
            .showImageOnFail(R.drawable.xiaolian)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(20))
            .displayer(new FadeInBitmapDisplayer(300)).build();

}
