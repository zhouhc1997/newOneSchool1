package com.qinhu.oneschool.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatTime {
    public static String show(String s) {
        long windowtime = System.currentTimeMillis() / 1000;
        String str = "";
        try {
            long nowtime = dateToStamp(s) / 1000;
            int t = (int)(windowtime - nowtime);
            if(t < 180)
                str = "刚刚";
            else if(t < 300 && t >= 180)
                str = "3分钟前";
            else if(t < 600 && t >= 300)
                str = "5分钟前";
            else if(t < 1800 && t >= 600)
                str = "10分钟前";
            else if(t < 3600 && t >= 1800)
                str = "30分钟前";
            else if(t < 7200 && t >= 3600)
                str = "1小时前";
            else if(t < 21600 && t >= 7200)
                str = "2小时前";
            else if(t < 43200 && t >= 21600)
                str = "6小时前";
            else if(t < 86400 && t >= 43200)
                str = "12小时前";
            else if(t < 172800 && t >= 86400 )
                str = "1天前";
            else if(t >= 172800 && t < 60*60*24*7)
                str = "2天前";
            else if(t >= 60*60*24*7 && t < 60*60*24*7*2)
                str = "1周前";
            else if(t >= 60*60*24*7*2 && t < 60*60*24*30)
                str = "两周前";
            else
                str = "1个月前";

        } catch (ParseException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return str;
    }


    public static long dateToStamp(String s) throws ParseException {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    //年月   ------   "YYYY 年 MM 月"
    public static String StringToYearMonth(String s){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(dateToStamp(s));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("YYYY 年 MM 月");
        return format.format(calendar.getTime());
    }

    //年 月 日   ------   "yyyy-mm-dd"
    public static String StringToYearMonthDay(String s){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(dateToStamp(s));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }

    public static String StringToDay(String s){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(dateToStamp(s));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int t = Calendar.DAY_OF_MONTH;
        return calendar.get(t) +"";
    }

    public static String StringToHour(String s){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(dateToStamp(s));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int h = Calendar.HOUR_OF_DAY;

        int t = Calendar.MINUTE;

        return calendar.get(h) + ":" + calendar.get(t);
    }

    public static String StringToWeek(String s){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(dateToStamp(s));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int t = Calendar.DAY_OF_WEEK;
        return getWeekStringOfNumber(calendar.get(t));
    }

    //获得本周一的日期
    public static String getMondayOFWeek(){
        Calendar cal = Calendar.getInstance();
        String monday;
        cal.add(Calendar.DATE, 7);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        return monday;
    }


    public static String getDateOFWeek(int i,int n){
        Calendar cal = Calendar.getInstance();
        String date;
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, 7 * n);
        cal.set(Calendar.DAY_OF_WEEK, i+1);
        date = new SimpleDateFormat("MM-dd").format(cal.getTime());
        return date;
    }


    public static int differentDaysByMillisecond(Date date1) {
        Date now = new Date();
        int days = (int) ((now.getTime() - date1.getTime()) / (1000*3600*24));
        return days+6;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    public static String getWeekStringOfDate(Date dt){
        return getWeekStringOfNumber(getWeekOfDate(dt));
    }

    public static String getWeekStringOfNumber(int i){
        String WeekString = "";
        switch (i){
            case 1:
                WeekString = "星期一";
                break;
            case 2:
                WeekString = "星期二";
                break;
            case 3:
                WeekString = "星期三";
                break;
            case 4:
                WeekString = "星期四";
                break;
            case 5:
                WeekString = "星期五";
                break;
            case 6:
                WeekString = "星期六";
                break;
            case 0:
                WeekString = "星期日";
                break;
            default:
                break;
        }
        return WeekString;
    }


    public static String CurrentDate(){
        return getCurrentYear() + "/" + getCurrentMonth() + "/" + getCurrentDay();
    }

    public static String Current_Date(){
        return getCurrentYear() + "-" + getCurrentMonth() + "-" + getCurrentDay();
    }

    public static String  Tomorrow_Date(){
        if (getCurrentDay()+1 > getCurrentMonthLastDay()){
            if (getCurrentMonth()+1 > 12){
                return (getCurrentYear()+1) + "-" + 1 + "-" + 1;
            }else {
                return getCurrentYear() + "-" + (getCurrentMonth()+1) + "-" + 1;
            }
        }
        return getCurrentYear() + "-" + getCurrentMonth() + "-" + getCurrentDay();
    }

    public static int getCurrentYear(){
        Calendar ca = Calendar.getInstance();
        return ca.get(Calendar.YEAR); //获取年份
    }

    public static int getCurrentMonth(){
        Calendar ca = Calendar.getInstance();
        return ca.get(Calendar.MONTH)+1;//获取月份
    }

    public static int getCurrentDay(){
        Calendar ca = Calendar.getInstance();
        return ca.get(Calendar.DATE); //获取日
    }

    //获取当前月的天数
    public static int getCurrentMonthLastDay()
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
}
