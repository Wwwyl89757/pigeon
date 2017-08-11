package com.example.administrator.pigeon;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pigeon.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bean.StepCounter;
import calendar.BeforeOrAfterCalendarView;
import config.Config;
import dao.StepDataDao;
import service.StepService;
import util.StepCountCheckUtil;
import util.TimeUtil;

public class PedometerActivity extends AppCompatActivity implements android.os.Handler.Callback{

    @ViewInject(R.id.movement_records_calender_ll)
    private LinearLayout movement_records_calender_ll;

    @ViewInject(R.id.movement_total_km_tv)
    TextView totalKmTv;

    @ViewInject(R.id.movement_total_km_time_tv)
    TextView kmTimeTv;

    @ViewInject(R.id.movement_total_steps_tv)
    TextView totalStepsTv;

    @ViewInject(R.id.movement_total_steps_time_tv)
    TextView stepsTimeTv;

    @ViewInject(R.id.is_support_tv)
    TextView supportTv;

    /**
     * 屏幕长度和宽度
     */
    public static int screenWidth, screenHeight;

    private BeforeOrAfterCalendarView calendarView;

    private String curSelDate;

    private DecimalFormat format = new DecimalFormat("#.##");
    private List<StepCounter> stepList = new ArrayList<>();
    private StepDataDao dataDao;


    private boolean isBind = false;

    private Messenger getReplyMessenger = new Messenger(new Handler(this));
    private Messenger messenger;

    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        ViewUtils.inject(this);
        curSelDate = TimeUtil.getCurrentDate();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initListener();

    }


    private void initData() {
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        calendarView = new BeforeOrAfterCalendarView(this);
        movement_records_calender_ll.addView(calendarView);

        if(StepCountCheckUtil.isSupportStepCountSensor(this)){
            getRecordList();
            supportTv.setVisibility(View.GONE);
            setData();
            setupService();
        }else {
            totalStepsTv.setText("0");
            supportTv.setVisibility(View.GONE);
        }
    }



    private void initListener() {
        calendarView.setOnBoaCalenderClickListener(new BeforeOrAfterCalendarView.BoaCalenderClickListener() {
            @Override
            public void onClickToRefresh(int position, String curDate) {
                //获取当前选中的时间
                curSelDate = curDate;
                //根据日期去取数据
                setData();
            }
        });
    }



    /**
     *开启计步服务
     * **/
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent,conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }


    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */

    private ServiceConnection conn = new ServiceConnection() {

        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {

            /**
             * 设置定时器，每个三秒钟去更新一次运动步数
             */
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        messenger = new Messenger(service);
                        Message msg = Message.obtain(null, Config.MSG_FROM_CLIENT);
                        msg.replyTo = getReplyMessenger;
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            timer = new Timer();
            timer.schedule(timerTask,0,3000);
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    /**
     * 设置记录数据
     * **/
    private void setData() {
        StepCounter counter = dataDao.getCurDataByDate(curSelDate);
        if(counter != null){
            int steps = Integer.parseInt(counter.getSteps());
            //获取全局步数
            totalStepsTv.setText(String.valueOf(steps));
            //计算总公里数
            totalKmTv.setText(countTotalKM(steps));
        }else {
            //获取全局步数
            totalStepsTv.setText("0");
            //计算总公里数
            totalKmTv.setText("0");
        }
        //设置时间
        String time = TimeUtil.getWeek(curSelDate);
        kmTimeTv.setText(time);
        stepsTimeTv.setText(time);
    }

    /**
     * 简易计算公里数，假设一步大约0.8米
     *
     * steps用户当前步数
     *
     * **/
    private String countTotalKM(int steps) {
        double totalMeters = steps *0.8;
        //保留两位有效数字
        return format.format(totalMeters / 1000);
    }


    /**
     * 获取全部运动历史记录
     * **/
    private void getRecordList() {
        dataDao = new StepDataDao(this);
        stepList.addAll(dataDao.getAllData());
        if(stepList.size()>=7){
            //当历史记录达到或者超过7条，就删掉7天之前的数据
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case Config.MSG_FROM_SERVER:
                if(curSelDate.equals(TimeUtil.getCurrentDate())){
                    int steps = msg.getData().getInt("steps");
                    totalStepsTv.setText(String.valueOf(steps));
                    totalKmTv.setText(countTotalKM(steps));
                }
                break;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑Service,多次绑定会异常
        if(isBind)this.unbindService(conn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
