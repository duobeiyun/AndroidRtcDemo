package com.duobei.duobeiapp.utils;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/3/24.
 */

public class CommonUtils {
    public static String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
            + "duobeiyun";

    public static String long2DateString(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 判断可用的存储空间
     */
    public static String getCanUseSize(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            return "0";
        } else {
            File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
            long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
            long totalSpace = sdcard_filedir.getTotalSpace();
            //将一个long类型的文件大小格式化成用户可以看懂的M，G字符串
            String usableSpace_str = Formatter.formatFileSize(context, usableSpace);
            String totalSpace_str = Formatter.formatFileSize(context, totalSpace);
            return usableSpace_str;
        }
    }

    /**
     * 获取文件夹下有多少个文件夹
     */
    public static int getFolderSize(String filePath) {
        int count = 0;

        // 得到该路径文件夹下所有的文件  
        File fileAll = new File(filePath);
        if (fileAll != null) {
            File[] files = fileAll.listFiles();
            if (files != null) {
                // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件  
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }


    /**
     * 获取文件夹的总大小
     *
     * @param f
     * @return
     */
    public static long getFileSize2(File f) {
        long size = 0;
        File[] flist = f.listFiles();
        if (flist != null) {
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize2(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        return size;
    }

    /**
     * 删除文件夹
     *
     * @param f
     */
    public static void deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory())
                        deleteDir(file);
                    file.delete();
                }
            }
            f.delete();
        }
    }

}