# 新版大班默认ui文档

## 1.必要权限

1. ```java
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
      
   ```

***注意***Android api 23之后相关权限需要动态获取，否则影响sdk的正常运行

***格外注意***：如果用户使用了默认的sdk ui节目但是没有获得相应的权限则会影响sdk的相关表现

## 2.默认ui界面入口

可参考demo中关于WebrtcEntranceActivity的介绍，分别给与了直播，在线回放，离线回放的入口demo和参数示例