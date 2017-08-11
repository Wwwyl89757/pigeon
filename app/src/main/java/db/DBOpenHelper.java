package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/8/7.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DB_NAME = "StepCounter.db";
    //数据库版本
    private static final int DB_VERSION = 1;


    private static final String CREATE_BANNER = "create table step ("
            + " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "curDate TEXT, "
            + "totalSteps TEXT)";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //执行有更改的sql语句
        db.execSQL(CREATE_BANNER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
