package com.duobei.duobeiapp;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.duobeiyun.common.DBYHelper;
import com.duobeiyun.util.DuobeiYunClient;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class DBYApplication extends Application {

    public static Context context;


    /**
     *是否使用net域名，新版大班使用net域名，其他课程为com域名
     * 用户可以根据自身需求进行设置
     * 需要动态设置可以参考方法
     *    DBYHelper.getInstance().switchDomain(changeDomain);
     */
    private boolean useNetDomain =false;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化多贝云sdk
        this.context = this;
        DBYHelper.getInstance().initDBY(this,useNetDomain,true);
        Fresco.initialize(this);
        //初始化下载
        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() { // is not has to provide.
                    @Override
                    public OkHttpClient customMake() {
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        builder.connectTimeout(15_000, TimeUnit.MILLISECONDS);
                        builder.proxy(Proxy.NO_PROXY);
                        return builder.build();
                    }
                }, 2);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("Tag", "set path success");
            //此处可设置默认存放位置
            /**
             * 注意：
             *       如果下载的地址不使用默认的DuobeiYunClient.savePath（默认的地址会在sdCard根目录下创建duobeiyun文件夹）
             *       请调用DuobeiYunClient.setSavePath("你的下载文件的存放目录");
             */
            DuobeiYunClient.setSavePath(DuobeiYunClient.savePath);
            FileDownloadUtils.setDefaultSaveRootPath(DuobeiYunClient.savePath);
            if (!new File(DuobeiYunClient.savePath).exists()) {
                new File(DuobeiYunClient.savePath).mkdir();
            }
        }
        
    }
}
