package com.duobei.duobeiapp.control;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.duobei.duobeiapp.DBYApplication;
import com.duobei.duobeiapp.db.TasksManagerDBOpenHelper;
import com.duobei.duobeiapp.download.bean.TaskBean;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 * modify in 2017/05/10 by yangge,将数据库的管理从sdk中放到demo中；开发者可以根据自己的需求对离线下载和下载后的资源做处理
 */
public class TasksManagerDBController {


    private SQLiteDatabase db;
    private final TasksManagerDBOpenHelper openHelper;

    public TasksManagerDBController() {
        openHelper = new TasksManagerDBOpenHelper(DBYApplication.context);

    }

    /**
     * @return 返回所有的下载条目
     */
    public List<TaskBean> getAllTasks() {
        db = openHelper.getWritableDatabase();
        final Cursor c = db.rawQuery("SELECT * FROM " + TasksManagerDBOpenHelper.TABLE_NAME, null);

        final List<TaskBean> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }
            do {
                TaskBean model = new TaskBean();
                model.setId(c.getInt(c.getColumnIndex(TaskBean.ID)));
                model.setName(c.getString(c.getColumnIndex(TaskBean.NAME)));
                model.setUrl(c.getString(c.getColumnIndex(TaskBean.URL)));
                model.setPath(c.getString(c.getColumnIndex(TaskBean.PATH)));
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    /**
     * 获取已经下载的课程资源
     *
     * @return
     */
    public List<TaskBean> getDoloadTask() {
        db = openHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TasksManagerDBOpenHelper.TABLE_NAME + " where " + TaskBean.ISLOAD + " = ?", new String[]{"1"});
        final List<TaskBean> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }
            do {
                TaskBean model = new TaskBean();
                model.setId(c.getInt(c.getColumnIndex(TaskBean.ID)));
                model.setName(c.getString(c.getColumnIndex(TaskBean.NAME)));
                model.setUrl(c.getString(c.getColumnIndex(TaskBean.URL)));
                model.setPath(c.getString(c.getColumnIndex(TaskBean.PATH)));
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;

    }

    /**
     * 修改下载的状态，未下载的状态是0，下载的状态是1
     *
     * @param url
     * @return
     */
    public boolean updateCourseState(String url, int isdown) {
        db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TaskBean.ISLOAD, isdown);//key为字段名，value为值  

        int update = db.update(TasksManagerDBOpenHelper.TABLE_NAME, values, TaskBean.URL + "=?", new String[]{url});

        db.close();
        return update == 1;
    }


    /**
     * 添加数据
     *
     * @param url
     * @param path
     * @param name
     * @return
     */
    public TaskBean addTask(final String url, final String path, final String name) {
        db = openHelper.getWritableDatabase();
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }
        final int id = FileDownloadUtils.generateId(url, path);
        TaskBean model = new TaskBean();
        model.setId(id);
        model.setName(name);
        model.setUrl(url);
        model.setPath(path);
        model.setIsdownload(0);

        final boolean succeed = db.insert(TasksManagerDBOpenHelper.TABLE_NAME, null, model.toContentValues()) != -1;
        return succeed ? model : null;
    }

    /**
     * 删除数据
     *
     * @param url
     * @return
     */
    public boolean deleteTask(final String url) {
        db = openHelper.getWritableDatabase();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        db.delete(TasksManagerDBOpenHelper.TABLE_NAME, TaskBean.URL + "=?", new String[]{url});
        if (db != null) {
            db.close();
        }
        return true;
    }
}
