package model;

import android.content.Context;

import myapp.MyApp;

/**
 * Created by Administrator on 2017/7/25.
 */

public class BaseModel {
    public int CODE_NULL=1000;
    public static int CODE_NOT_EQUAL=1001;

    public static final int DEFAULT_LIMIT=20;

    public Context getContext(){
        return MyApp.INSTANCE();
    }
}
