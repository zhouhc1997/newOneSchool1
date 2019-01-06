package com.qinhu.oneschool.SQLliteObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SQLliteQuery{
    private static SQLliteOpen sqLliteOpen;
    private  SQLiteDatabase db;
    private Class[] classes;

    private void CreateDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        sqLliteOpen = new SQLliteOpen(context, name, factory, version);
        db = sqLliteOpen.getWritableDatabase();
    }

    public SQLliteOpen getSqLliteOpen() {
        return sqLliteOpen;
    }

    public void CreatTable(Context context, Object object) {
        this.CreateDatabase(context,"test.db",null,1);
        List<String> list = getFileds(object);
        String creat = "create table if not exists "+object.getClass().getSimpleName()+"("+"id INTEGER PRIMARY KEY AUTOINCREMENT,";
        for(int i = 0,size = list.size();i < size;i++){
            creat += list.get(i)+" "+classes[i].getSimpleName()+",";
        }
        creat = creat.substring(0,creat.length()-1)+")";
        Log.d("msg2",creat);
        db.execSQL(creat);
        db.close();
    }
    private List<String> getFileds(Object t){
        List<String> list = new ArrayList<>();
        Field[] fields = t.getClass().getDeclaredFields();
        classes = new Class[fields.length];
        for(int i = 0,size = fields.length-1;i < size;i++){
            if(fields[i].getName().contains("public static")||fields[i].getName().equals("$change"))
                continue;
            list.add(fields[i].getName());
            classes[i] = fields[i].getType();
        }
        return list;
    }
}
