---
title: 快速接入 Android SAAS SDK
---


## 1.集成 SDK

### 方法一：使用jcenter自动集成

在项目的 **/app/build.gradle** 文件中，添加如下行：

```java
dependencies {    ...    implementation 'com.duobeiyun.saassdk:paassdk:2.5.9.0'}
```



### 方法二：手动导入SDK

- 获取最新的 多贝云 SaaS SDK，下载解压
- 将解压后的 **libs** 文件夹下下的 **armeabi-v7a** 复制到你项目的 **/app/src/main/jinLibs/** 目录下
- 将解压后的 *.aar** 复制到项目的 **/app/libs/** 目录中
- 修改项目的 **/app/build.gradle** 文件中的 `dependencies` 块中的内容 `implementation fileTree(dir: 'libs', include: ['*.jar'])` 为 `implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])`
- 修改/app/build.gradle文件内容，在项目的 **/app/build.gradle** 文件中，添加如下行：

```java
android {    ...    defaultConfig {        ...         ndk {            //设置支持的SO库架构,目前SDK支持armv7a架构            abiFilters 'armeabi-v7a'        }    }}
```





## 2.添加对应的权限

在项目的 **/app/src/main/AndroidManifest.xml** 文件中添加相应的权限，具体如下：

```java
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /><uses-permission android:name="android.permission.CAMERA" /><uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /><uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /><uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /><uses-permission android:name="android.permission.INTERNET" /><uses-permission android:name="android.permission.READ_PHONE_STATE" /><uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /><uses-permission android:name="android.permission.RECORD_AUDIO" /><uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" /><uses-permission android:name="android.permission.WAKE_LOCK" /><uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
```



## 3.防止SDK代码混淆

在 **app/proguard-rules.pro** 文件中添加如下行，防止代码混淆：

```java
-keep class com.duobeiyun.**{*;}
-keep class com.duobei.**{*;}
```





## 4.sdk设置，快速进入教室

###  4.0.Android6.0+

Android6.0 以上获取 camera mic 存储相关的动态权限

### 4.1导入相关类

```java
import com.duobeiyun.opengles.GLFrameSurface;
import com.duobeiyun.player.LivePlayer;
import com.duobeiyun.type.LiveMessage;
import com.duobeiyun.type.RoleType;
import com.duobeiyun.type.StatusCode;
import com.duobeiyun.widget.LivePlayerView;
```



### 4.2初始化

```java
try {   
    mSdkPlayer = new LivePlayer(this.mContext, mPPTView);
    }
catch (Exception e) {    e.printStackTrace();}
```



### 4.3设置相关回调，房间信息

```java
mSdkPlayer.setVideoCallback(this);//设置远端视频开启关闭回调
 //统一使用接口设置room信息
mSdkPlayer.setRoomInfoBean(roomInfoBean);
//设置直播基本回调
mSdkPlayer.setLiveMessageCallback(this);
//设置远端老师，学生视频view
mSdkPlayer.setTeacherFrameSurface(mTeachersurface);
mSdkPlayer.setStudentFrameSurface(mStudentSurface);

mSdkPlayer.setDbLocalVideoCallback(this);//本地视频开启与关闭回调
mSdkPlayer.setLocalVideoFrameSurface(mLocalGLSurfaceVideo);//设置本地视频view
//设置进入教室结果码回调
mSdkPlayer.setEnterRoomResultCallback(this);
//调用sdk方法，进入教室，成功收到老师的音视频等相关消息
mSdkPlayer.start();
```



### 4.4可选择性回调

```java
//net域名新版大班上麦过程中相关回调
mSdkPlayer.setWebrtcMicCallback(this);
//收到第一帧远端视频帧
mSdkPlayer.setFirstVideoFrameCallback(this);
//远端学生音频创建与销毁
mSdkPlayer.setNewAddedBaseCallback(this);
//本地音频创建与销毁回调
mSdkPlayer.setMicRequestCallback(this);
//本地学生请求上麦上视频相关回调
mSdkPlayer.setRaiseHandCallback(new RaiseHandCallback() {
            @Override
            public void handSuccess() {
                toast("举手成功");
            }

            @Override
            public void handFail() {
                toast("举手失败");
            }

            @Override
            public void downHand() {
                toast("客户端被取消举手发言");
            }

            @Override
            public void banHand() {
                toast("老师禁止了举手");
            }

            @Override
            public void requestCamera() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAudioVideoPermission()) {
                        pushCamera(StatusCode.OPEN_CAMERA_OK);
                    } else {
                        showPermissionTip();
                    }
                } else {
                    pushCamera(StatusCode.OPEN_CAMERA_OK);
                }
            }
        });


```

### 4.5 多贝相关环境设置

​         可在自定义的Application 中的

```java
public void onCreate()
```

添加 多贝相关环境初始化定义

-   多贝sdk初始化

```
DBYHelper.getInstance().initDBY(Context context, boolean usedDBNetDomain, boolean useHttpsProtocol) 
```

| 参数             | 意义                                    |
| ---------------- | --------------------------------------- |
| context          | Android 上下文环境                      |
| usedDBNetDomain  | 是否使用net环境，机构可以跟技术支持沟通 |
| useHttpsProtocol | 是否使用https协议                       |

-   多贝路径

```
DuobeiYunClient.setSavePath(DuobeiYunClient.savePath);
```

1.   函数说明

​          离线下载资源文件所在位置（demo中包含了下载工具类，但不属于sdk内部逻辑不在维护，用户进入离线课程时，sdk会根据此路径和roomid找到相应的资源文件夹）

### 4.6 更多api信息[可参考]()

### 4.7具体可参考demo，[点击下载]()