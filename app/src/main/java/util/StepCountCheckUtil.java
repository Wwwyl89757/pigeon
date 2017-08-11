package util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

/**
 * 检测手机是否支持计步工具
 *
 * Created by Administrator on 2017/8/7.
 */

public class StepCountCheckUtil {
    private Context context;
    //判断是否有传感器
    private boolean hasSensor;

    public StepCountCheckUtil(Context context) {
        this.context = context;
        hasSensor = isSupportStepCountSensor();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean isSupportStepCountSensor() {
        return context.getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }


    /**
     * 判断该设备是否支持计步
     * **/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isSupportStepCountSensor(Context context){
        SensorManager manager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor countSensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        return countSensor!=null || detectorSensor!=null;
    }
}
