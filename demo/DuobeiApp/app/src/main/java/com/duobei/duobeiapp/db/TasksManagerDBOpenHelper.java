package com.duobei.duobeiapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duobeiyun.third.download.bean.TaskBean;

/**
 * Created by Administrator on 2016/10/24.
 */
public class TasksManagerDBOpenHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "tasksmanager.db";
    public final static int DATABASE_VERSION =1;
    public final static String TABLE_NAME = "tasksmanger";


    public TasksManagerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + String.format(
                "("
                        + "%s INTEGER PRIMARY KEY, " // id, download id
                        + "%s VARCHAR, " // name
                        + "%s VARCHAR, " // url
                        + "%s VARCHAR, " // path
                        + "%s INTEGER  "//isLoad
                        + ")"
                , TaskBean.ID
                , TaskBean.NAME
                , TaskBean.URL
                , TaskBean.PATH
                , TaskBean.ISLOAD

        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && newVersion == 2) {
            db.delete(TABLE_NAME, null, null);
        }
    }
}
