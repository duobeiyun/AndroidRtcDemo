package com.duobei.duobeiapp.download.bean;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/25.
 */
public class TaskBean implements Serializable {
    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String URL = "url";
    public final static String PATH = "path";
    public final static String ISLOAD = "isload";

    private int id;
    private String name = "";
    private String url = "";
    private String path = "";
    //新增两个字段
    private int isdownload = 0;//0表示没有下载，1表示下载成功

    public TaskBean() {
    }

    public TaskBean(int id, String name, String url, String path, int isdownload) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.path = path;
        this.isdownload = isdownload;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIsdownload() {
        return isdownload;
    }

    public void setIsdownload(int isdownload) {
        this.isdownload = isdownload;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(NAME, name);
        cv.put(URL, url);
        cv.put(PATH, path);
        cv.put(ISLOAD, isdownload);
        return cv;
    }
}
