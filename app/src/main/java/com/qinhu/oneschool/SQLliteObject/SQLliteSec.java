package com.qinhu.oneschool.SQLliteObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SQLliteSec<T extends SQLlite> extends SQLliteQuery {
    private SecListener mSeclistener;
    private SQLiteDatabase db;
    private Class[] classes;
    private T t ;

    public SQLliteSec(T t){
        this.t = t;
    }

    private void setDatabase() {
        db = super.getSqLliteOpen().getWritableDatabase();
    }

    public void getObject(String id, SecListener<T> listener) {
        this.setDatabase();
        try {
            mSeclistener = listener;
            List<String> list = getFileds();
            String sql = "select * from " + t.getClass().getSimpleName() + " where id = " + id;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    for (int i = 0, size = list.size(); i < size; i++) {
                        String s = cursor.getString(cursor.getColumnIndex(list.get(i)));
                        Method method = t.getClass().getDeclaredMethod("set" + toUpperFristChar(list.get(i)), classes[i]);
                        method.invoke(t, s);
                    }
                }
                mSeclistener.done(t, true);
            } else
                mSeclistener.done(null, false);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            mSeclistener.done(null, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mSeclistener.done(null, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            mSeclistener.done(null, false);
        }

    }

    private List<String> getFileds() {
        List<String> list = new ArrayList<>();
        Field[] fields = t.getClass().getDeclaredFields();
        classes = new Class[fields.length];
        for (int i = 0, size = fields.length - 1; i < size; i++) {
            if (fields[i].getName().contains("public static") || fields[i].getName().equals("$change"))
                continue;
            list.add(fields[i].getName());
            classes[i] = fields[i].getType();
        }
        return list;
    }

    private static String toUpperFristChar(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] -= 32;
        return String.valueOf(charArray);
    }
}
