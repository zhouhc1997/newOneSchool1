package com.qinhu.oneschool.SQLliteObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLlite extends SQLliteQuery{

    private SQLiteDatabase db;
    private UpdateListener mUpdatelistener;
    private SaveListener mSavelistener;

    public void update(String id, Object object, UpdateListener listener ) {
        this.setDatabase();
        try {
        mUpdatelistener = listener;
        List<String> list = new ArrayList<>();
        list = getFileds();
        List<String> changeList = new ArrayList<>();
        for(int i=0,size = list.size();i<size;i++){
            try {
                Method method = this.getClass().getDeclaredMethod("get"+toUpperFristChar(list.get(i)),null);
                Object o = method.invoke(object,null);
                if(o==null||"".equals(o.toString())||o.toString()==null)
                    continue;
                changeList.add(o.toString()+","+i);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
            String sql = "update "+this.getClass().getSimpleName()+" set ";
            for(int i = 0,size = changeList.size()-1;i < size;i++){
                sql += list.get(Integer.parseInt(changeList.get(i).split(",")[1])) +"="+changeList.get(i).split(",")[0]+",";
            }
            sql+= list.get(Integer.parseInt(changeList.get(changeList.size()-1).split(",")[1])) +"="+changeList.get(changeList.size()-1).split(",")[0]+" where id ="+id;
            Log.d("msg2",sql);
            db.execSQL(sql);
            db.close();
            mUpdatelistener.done(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mUpdatelistener.done(false);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            mUpdatelistener.done(false);
        }
    }
    public void delete(int id, UpdateListener listener) {
        this.setDatabase();
        mUpdatelistener = listener;
        String sql = "delete from "+this.getClass().getSimpleName() +" where id = "+id;
        db.execSQL(sql);
        db.close();
        mUpdatelistener.done(true);
    }

    public void insert(Object object,SaveListener listener) {
        this.setDatabase();
        try {
            mSavelistener = listener;
            List<String> list = getFileds();
            ContentValues cv = new ContentValues();
            for (int i = 0, size = list.size(); i < size; i++) {
                Method method = null;
                method = this.getClass().getDeclaredMethod("get" + toUpperFristChar(list.get(i)), null);

                Object o = null;
                o = method.invoke(object, null);
                if (o == null || "".equals(o.toString()) || o.toString() == null)
                    continue;
                cv.put(list.get(i),o.toString());

            }
            Log.d("msg2",db.toString());
            db.insert(this.getClass().getSimpleName(),null,cv) ;
            mSavelistener.done(true);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            mSavelistener.done(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mSavelistener.done(false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            mSavelistener.done(false);
        }
    }

    private static String toUpperFristChar(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] -= 32;
        return String.valueOf(charArray);
    }
    private List<String> getFileds(){
        List<String> list = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for(int i = 0,size = fields.length-1;i < size;i++){
            if(fields[i].getName().contains("public static")||fields[i].getName().equals("$change"))
                continue;
            list.add(fields[i].getName());
        }
        return list;
    }
    private void setDatabase() {
        db = super.getSqLliteOpen().getWritableDatabase();
    }
}
