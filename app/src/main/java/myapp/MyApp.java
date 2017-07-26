package myapp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;
import io.rong.imkit.RongIM;
import util.MyMessageHandler;


/**
 * Created by Administrator on 2017/6/27.
 */

public class MyApp extends Application {


    private static MyApp INSTANCE;

    public static MyApp INSTANCE(){
        return INSTANCE;
    }
    private void setInstance(MyApp app) {
        setBmobIMApplication(app);
    }
    private static void setBmobIMApplication(MyApp a) {
        MyApp.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"7714967b6d8406fb9fe456b2fffff1e3");
        setInstance(this);
        RongIM.init(this);

        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getCurProcessName(this))){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));
        }

    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }
}
