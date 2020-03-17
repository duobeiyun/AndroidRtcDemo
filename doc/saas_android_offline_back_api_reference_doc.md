---
title:  Android 离线回放 API 参考文档
---



## 1.主要使用类

- OfflinePlayback接口类包含应用程序调用的主要方法
- OfflinePlayerView离线回放内置辅助类，如果需要使用展示ppt，划线设置为可见，否则隐藏即可

## 2.主要使用接口

PlaybackMessageCallback 直播相关消息回调接口

VideoCallback 接受视频打开关闭接口



## 3.OfflinePlayback使用方式

- #### 创建实例

```java
 public OfflinePlayback(Context context, OfflinePlayerView playbackPlayerView) 
```

参数

| 参数              | 说明                                 |
| ----------------- | ------------------------------------ |
| context           | 安卓活动 (Android Activity) 的上下文 |
| OfflinePlayerView | 内置辅助类                           |


- 设置解密密钥

```java
public void init(String key) 
```

   `参数`

​       key 此为机构解密密钥，可以技术支持人员获取



- #### 设置视频view

1. 设置远端老师视频

```Java
public void setVideoVideoContainer(PlayerView surface)
```

参数

| 参数    | 说明         |
| ------- | ------------ |
| surface | 远端老师视频 |



2. 设置远端学生视频

```Java
public void setStudentVideoContainer(PlayerView studentVideoPlayer)
```

参数

| 参数               | 说明         |
| ------------------ | ------------ |
| studentVideoPlayer | 远端学生视频 |



- [x] 必须设置回调




```java
public void setCallback(String roomId, OfflinePlaybackMessageCallback callback) 
```

| 参数                           | 意义                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| roomId                         | 课程romid，注意非视频课程直接传入roomid即可；视频课程则需要拼接roomid_v( eg: roomid 为jze177f75e1f85476f8aed9d0513c578fa，拼接后则为jze177f75e1f85476f8aed9d0513c578fa_v) |
| OfflinePlaybackMessageCallback | 离线教室相关回调                                             |



- 设置进度条（进度条可以只设置一个，为方便用户做横竖屏处理提供俩个接口）

```java
public void setSeekBar(SeekBar bar)
```

| 参数 | 说明                                 |
| ---- | ------------------------------------ |
| bar  | 设置进度条，用来更新课程当前播放进度 |

- 设置横屏进度条

```java
public void setLanSeekBar(SeekBar bar)
```

| 参数 | 说明                                 |
| ---- | ------------------------------------ |
| bar  | 设置进度条，用来更新课程当前播放进度 |

-  设置视频开关通知回调

```java
public void setVideoCallback(VideoCallback videoCallback) 
```

 `参数`

​       videoCallback 视频开关通知



## 4.必备回调接口介绍



- PlaybackMessageCallback

| 回调函数                                                     | 函数介绍               | 参数说明                                                     |
| ------------------------------------------------------------ | ---------------------- | ------------------------------------------------------------ |
| void handleContent(ArrayList list)                           | 以集合形式返回聊天信息 | 聊天消息集合；注意seek后返回的是0到当前时间点的所有聊天集合，对接房需要自行处理聊天去重问题 |
| void handleErrorMessage(Exception exception)                 | 资源检测错误           | 错误类型                                                     |
| void loadStart();                                            | 开始加载               | 触发调节包括不限于：进教室，线路切换，快进                   |
| void handleContent(ChatBean chatBean)                        | 以单条形式返回聊天信息 |                                                              |
| void loadFinish()                                            | 加载结束               |                                                              |
| void playFinish（totalTime)                                  | 本节课程总时长         | totalTime 播放的总时间                                       |
| void currentTime()                                           | 当前播放时间点         | 用来更新当前进度条                                           |
| void statusCode(int code)                                    | 状态码回调             |                                                              |
| void initPPT(String duration, String totalPage, String currentPage) | 初始化ppt              | duration 课程总时长；totalPage ppt总页数；currentPage当前页数 |
| void exit();                                                 | 退出                   |                                                              |



- VideoCallback 远端视频开关回调

| 回调函数                  | 函数介绍 | 参数说明          |
| ------------------------- | -------- | ----------------- |
| void showvideo(int role)  | 打开视频 | role 参考RoleType |
| void hidenVideo(int role) | 关闭视频 | role 参考RoleType |



## 5.生命周期基本控制

- 开始，也包括暂停后的恢复

```java
public int play() 
```

   返回值

| 返回值 | 含义       |
| ------ | ---------- |
| -1     | 参数有问题 |
| 0      | 成功       |



- 暂停

```java
public void pause()
```

​      说明

设置必备的回调函数后，调用此函数暂停直播，会走相关回调停掉音视频，可在进入后台的时候调用



- 销毁

```java
public void destroy() 
```

​     `说明`

​       在Android onDestroy 回调中调用销毁sdk



-  倍速设置

```Java
public float setPlaybackSpeed(float pspeed) 
```

   `函数返回值：`

​           -1 触发sdk频率限制，此次函数不执行，（频率默认为1秒，可更改）

​           大于0 的数值代表函数执行成功

  `参数说明`

​          pspeed 支持区间为1.0-2.0之间，< 1.0默认为1.0 倍速；>2.0倍速默认为2倍速



- 设置进度

```java
public void setProgress(String pprogress) 
```

 `函数意义：`

开发者可设置到某个时间点，用用来做快进支持

`参数`

快进到某个时间点，举例：

00：10：23 ，10分23秒



## 6.错误码介绍

消息错误码StatusCode

| 具体错误码     | 含义介绍         |
| -------------- | ---------------- |
| PARAMS_INVALID | 设置教室参数异常 |
| ALL_OK         | 进入教室正常     |

## 7.工具类



```java
DuobeiYunClient
```

- 设置资源存储路径，默认为duobeiyun

```java
public static void setSavePath(String path) {    savePath = path;}
```



- 删除某节课资源

```java
public static boolean delete(String roomId)
```

   `参数说明：`

​       课程romid，注意非视频课程直接传入roomid即可；视频课程则需要拼接roomid_v( eg: roomid 为jze177f75e1f85476f8aed9d0513c578fa，拼接后则为jze177f75e1f85476f8aed9d0513c578fa_v，sdk会内部自行拼接完整路径为setSavePath中path+roomid)



- 判断某节课是否为视频课

```java
/**
 * 通过接口返回这节课是否包含视频
 *
 * @param roomId        原始roomId 
 * @param iOfflineSource 接口得到包含视频课roomIds 
 *                       返回离线roomid是否包含视频 *                     
                      roomids[0] 音频 *                    
                      roomids[1] 视频 */
public static void getVideoOrAudioRoomId(final String roomId, final IOfflineSource iOfflineSource)
```



 **接口介绍  IOfflineSource**

-  getVideoOrAudioRoomId 获取音频或着视频roomId

```java
   /**
     * 返回离线roomid是否包含视频
     * roomids[0] 音频
     * roomids[1] 视频
     *
     * @param roomids
     * @isVideoRoom 是否是视频教室
     */
    void getVideoOrAudioRoomId(String roomids[], boolean isVideoRoom);
```









