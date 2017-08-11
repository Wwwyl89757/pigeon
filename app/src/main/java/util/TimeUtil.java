package util;

import android.support.annotation.DimenRes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class TimeUtil {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
    private static Calendar calendar = Calendar.getInstance();
    private static String[] week = new String[]{"日","一","二","三","四","五","六"};
    private static String[] myWeek = new String[]{"周日","周一","周二","周三","周四","周五","周六"};



    /**
     * 更改日期格式
     *
     * **/
    public static String changeFormatDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = null;

        try {
            Date dt = dateFormat.parse(date);
            curDate = format.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return curDate;
    }

    /**
     * 返回当前时间
     * **/
    public static String getCurTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = ""+dateFormat.format(System.currentTimeMillis());
        return time;
    }


    /**
     * 获取运动记录是周几，今天则返回具体时间，其他返回具体周几
     * **/
    public static String getWeek(String date){
        String today = dateFormat.format(calendar.getTime());
        if(today.equals(date)){
            return getCurTime();
        }
        Calendar preCalender = Calendar.getInstance();
        preCalender.add(Calendar.DATE,-1);
        String yesterday = dateFormat.format(preCalender.getTime());
        if(yesterday.equals(date)){
            return "昨天";
        }
        int w = 0;
        try {
            Date dates = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates);
            w = calendar.get(Calendar.DAY_OF_WEEK)-1;
            if(w<0){
                w=0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return myWeek[w];
    }

    /**
     *获取是几号
     * **/
    public static int getCurrentDay(){
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取当前的日期
     * **/
    public static String getCurrentDate(){
        String currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }

    /**
     * 根据date列表获取day列表
     * **/
    public static List<Integer> dateListToDayList(List<String> dateList){
        Calendar calendar = Calendar.getInstance();
        List<Integer> dayList = new ArrayList<>();
        for(String date:dateList){
            try {
                calendar.setTime(dateFormat.parse(date));
                int day = calendar.get(Calendar.DATE);
                dayList.add(day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return dayList;
    }


    /**
     * 根据当前日期获取以含当天的前一周日期
     * **/
    public static List<String> getBeforeDateListByNow(){
        List<String> weekList = new ArrayList<>();

        for(int i=-6; i<=0; i++){
            //以周日为一周的第一天
            Calendar calender = Calendar.getInstance();
            calender.add(Calendar.DATE,i);
            String date = dateFormat.format(calender.getTime());
            weekList.add(date);
        }
        return weekList;
    }


    /**
     * 判断当前日期是周几
     * **/
    public static String getCurWeekDay(String curDate){
        int w = 0;
        try {
            Date date = dateFormat.parse(curDate);
            Calendar calender = Calendar.getInstance();
            calender.setTime(date);
            w= calender.get(Calendar.DAY_OF_WEEK)-1;
            if(w<0){
                w=0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week[w];
    }
}
