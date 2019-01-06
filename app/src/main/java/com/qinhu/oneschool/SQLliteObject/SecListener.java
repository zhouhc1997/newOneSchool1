package com.qinhu.oneschool.SQLliteObject;

public interface SecListener <T extends SQLlite> {
     void done(T object, boolean success);
}
