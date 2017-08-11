package dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bean.StepCounter;
import db.DBOpenHelper;

/**
 * Created by Administrator on 2017/8/8.
 */

public class StepDataDao {
    private DBOpenHelper openHelper;
    private SQLiteDatabase stepDb;

    public StepDataDao(Context context) {
        openHelper = new DBOpenHelper(context);
    }


    /**
     *添加一条记录
     * **/
    public void addNewData(StepCounter counter){
        stepDb = openHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("curDate",counter.getCurDate());
        values.put("totalSteps",counter.getSteps());
        stepDb.insert("step",null,values);
        stepDb.close();
    }


    /**
     *根据日期查询数据
     * **/
    public StepCounter getCurDataByDate(String curDate){
        stepDb = openHelper.getReadableDatabase();
        StepCounter stepCounter = null;
        Cursor cursor = stepDb.query("step",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndexOrThrow("curDate"));
            if(curDate.equals(date)){
                String steps = cursor.getString(cursor.getColumnIndexOrThrow("totalSteps"));
                stepCounter = new StepCounter(date,steps);
                break;
            }
        }
        stepDb.close();
        cursor.close();
        return stepCounter;
    }

    /**
     *查询所有记录
     **/
    public List<StepCounter> getAllData(){
        List<StepCounter> stepList = new ArrayList<>();
        stepDb = openHelper.getReadableDatabase();

        Cursor cursor = stepDb.rawQuery("SELECT * FROM step",null);
        Log.e("error",cursor.getColumnCount()+"");
        while (cursor.moveToNext()){
            String curData = cursor.getString(cursor.getColumnIndex("curDate"));
            String totalSteps = cursor.getString(cursor.getColumnIndex("totalSteps"));
            StepCounter counter = new StepCounter(curData,totalSteps);

            stepList.add(counter);
        }
        stepDb.close();
        cursor.close();
        return stepList;
    }



    /**
     * 更新数据
     * **/
    public void updateCurData(StepCounter counter){
        stepDb = openHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("curDate",counter.getCurDate());
        values.put("totalSteps",counter.getSteps());
        stepDb.update("step",values,"curDate = ?",new String[]{counter.getCurDate()});

        stepDb.close();
    }


    /**
     * 删除指定日期的记录
     * **/
    public void deleteCurData(String curDate){
        stepDb = openHelper.getReadableDatabase();

        if(stepDb.isOpen()){
            stepDb.delete("step","curDate",new String[]{curDate});
            stepDb.close();
        }
    }

}
