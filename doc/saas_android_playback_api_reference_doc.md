---
title:  Android 在线回放 API 参考文档
---

## 主要使用类

- PlaybackPlayer接口类包含应用程序调用的主要方法
- PlaybackPlayerView在线回放内置辅助类，如果需要使用展示ppt，划线设置为可见，否则隐藏即可

## 主要使用接口

PlaybackMessageCallback 直播相关消息回调接口

VideoCallback 接受视频打开关闭接口

ChatMsgRefreshCallback 新的聊天信息回调接口（推荐使用此种方式）



## PlaybackPlayer使用方式

- #### 创建实例

```java
 public PlaybackPlayer(Context context, PlaybackPlayerView playbackPlayerView) 
```

参数

| 参数               | 说明                                 |
| ------------------ | ------------------------------------ |
| context            | 安卓活动 (Android Activity) 的上下文 |
| PlaybackPlayerView | 内置辅助类                           |
- #### 设置房间相关信息

```java
public void setRoomInfoBean(RoomInfoBean roomInfoBean) 
```

参数

| 参数         | 说明           |
| ------------ | -------------- |
| roomInfoBean | 房间信息集成类 |

- #### 设置视频view

1. 设置远端老师视频

```Java
public void setTeacherFrameSurface(GLFrameSurface surface)
```

参数

| 参数    | 说明         |
| ------- | ------------ |
| surface | 远端老师视频 |



2. 设置远端学生视频

```Java
public void setStudentFrameSurface(GLFrameSurface surface)
```

参数

| 参数    | 说明         |
| ------- | ------------ |
| surface | 远端学生视频 |



- [x] 必须设置回调

-   视频打开关闭回调

```java
public void setVideoCallback(VideoCallback callback) 
```

| 参数     | 说明                                                   |
| -------- | ------------------------------------------------------ |
| callback | 直播某一个视频打开关闭接口，对视频view进行显示隐藏控制 |

-   在线直播相关消息接口回调

```java
public void setPlaybackMessageCallback(PlaybackMessageCallback cb)
```

| 参数     | 说明                     |
| -------- | ------------------------ |
| callback | 在线直播相关消息接口回调 |

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



## 必备回调接口介绍

- PlaybackMessageCallback

| 回调函数                                        | 函数介绍                                    | 参数说明                                                     |
| ----------------------------------------------- | ------------------------------------------- | ------------------------------------------------------------ |
| void handleContent(ArrayList list)              | 以集合形式返回聊天信息                      | 聊天消息集合                                                 |
| void kickoff()                                  | 直播同一账号在不同地方登录，导致前者被踢    |                                                              |
| void pptMessage(int currentpage, int totalpage) | 收到ppt相关函数，（新版大班可以忽略此参数） | currentpage 当前页数   ，totalpage ppt总页数                 |
| void connected()                                | 进入教室成功                                |                                                              |
| void connectFail(String s)                      | 进入教室失败                                | 失败原因                                                     |
| void handleClearChat();                         | 清除聊天信息                                | 注意如果不注册ChatMsgRefreshCallback，聊天消息接收会走handleContent回调，自己需要在此回调中情况聊天消息，否则会有消息重复 |
| void statusCode(int code, String msg);          | 直播相关状态码回调                          | code错误码，msg 错误信息，具体错误码，末尾有介绍             |
| void loadStart();                               | 开始加载                                    | 触发调节包括不限于：进教室，线路切换，快进                   |
| void handleContent(ChatBean chatBean)           | 以单条形式返回聊天信息                      | 注意如果不注册ChatMsgRefreshCallback，聊天消息接收会走handleContent回调，自己需要在此回调中情况聊天消息，否则会有消息重复 |
| void networkNotConnected()                      | 网络未连接                                  |                                                              |
| void getTotalTime(String totalTime)             | 本节课程总时长                              | totalTime 播放的总时间                                       |
| void currentTime()                              | 当前播放时间点                              | 用来更新当前进度条                                           |
| void playPuase(boolean isplay)                  | 播放状态回调                                | isplay 当前课程是否播放                                      |
| void playFinish()                               | 播放完成                                    |                                                              |



- EnterRoomResultCallback 进入教室结果回调

| 回调函数                             | 函数介绍                            |
| ------------------------------------ | ----------------------------------- |
| void enterRoomSuccess()              | 成功进入教室                        |
| void enterRoomFailed(int failedCode) | 进入教室失败，failedCode 失败错误码 |



- VideoCallback 远端视频开关回调

| 回调函数                  | 函数介绍 | 参数说明          |
| ------------------------- | -------- | ----------------- |
| void showvideo(int role)  | 打开视频 | role 参考RoleType |
| void hidenVideo(int role) | 关闭视频 | role 参考RoleType |







## 可选择回调接口介绍



- FirstVideoFrameCallback 接收到视频第一帧

| 函数名                                            | 函数介绍       | 参数说明                                               |
| ------------------------------------------------- | -------------- | ------------------------------------------------------ |
| void receiveVideoFirstFrame(int type, int handle) | 收到视频第一帧 | type 角色类型，参考RoleType；handle 老师视频角色索引值 |



- BaseCallbackNewAdded 远端音频相关回调

| 函数名                                      | 函数介绍             | 参数说明          |
| ------------------------------------------- | -------------------- | ----------------- |
| void notifyUISdk2CreateAudioPcm(String uid) | 收到远端其他学生上麦 | uid： 上麦学生uid |
| void notifyUISdk2DeletAudioPcm(String uid)  | 收到远端其他学生下麦 | uid： 下麦学生ui  |



- ChatMsgRefreshCallback 新的聊天信息接口

   **概述**

       使用此接口为新的聊天回调，每次回调中为所有的聊天信息，如果注册此接口则LiveMessageCallback 中关于聊天回调不会生效，推荐使用此种方式

      如果需要区分老师，学生，助教信息可使用LivePlayer：：getChatModule()获取聊天module，可以分别得到各自聊天信息

  

| 函数名                                | 函数介绍     | 参数说明   |
| ------------------------------------- | ------------ | ---------- |
| void refreshChatMsg(ArrayList allMsg) | 聊天消息接口 | 所有的消息 |



## 生命周期基本控制

- 开始

```java
public int startPlayback() 
```

   返回值

| 返回值 | 含义                 |
| ------ | -------------------- |
| -2     | 当前没有网络         |
| -3     | 填入进入教室信息为空 |
| -4     | 填入进入教室信息无效 |
| 0      | 成功                 |



- 暂停

```java
public void pause()
```

​      说明

设置必备的回调函数后，调用此函数暂停直播，会走相关回调停掉音视频，可在进入后台的时候调用



- 回复

```java
public void recovery() 
```

​     说明

设置必备的回调函数后，调用此函数回复直播，会走相关回调停掉音视频，可在从后台回复到进入前台的时候调用

即

 

-  处于前台时暂停回复

```java
public void play(boolean isplay)
```

 

函数说明：

​        与pause，recovery对应不同，前面为退到后台在Android系统回调函数中进行调用；而play则为前台的暂停也回复，例如：设置一个按钮，点击进行播放控制，详细可参考demo



参数说明：

​     isplay  true 播放；false 暂停



- 退出教室停止sdk

```java
public int stopPlayback() 
```

​     `说明`

在Android 离开教室时调用，此方法为异步方法，需要等待在回调LiveMessageCallback ：：statusCode中，StatusCode.STOPSUCCESS即可调用Android finish方法离开教室，否则会造成教室状态混乱

  



- 销毁

```java
public void release() 
```

​     `说明`

​       在Android onDestroy 回调中调用销毁sdk



-  倍速设置

```Java
public float setSpeedPlay(float pspeed) 
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

  

## 线路切换

1. 功能介绍 ：

​               当网络卡断或音视频质量不佳的时候可以选择进行线路切换

​    2.线路切换用到的函数介绍

| 函数名                                      | 函数意义         | 返回值意义                                                   | 参数意义                                                     |
| ------------------------------------------- | ---------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| int getCanChangeUrlCounts()                 | 返回总共线路条数 | 0成功                                                        | 无                                                           |
| int changePlaybackURL(int playbackURLIndex) | 切换到某条线路   | -1 触发sdk频率限制，（默认为1秒，可设置）此次操作不执行，0 函数执行成功 | playbackURLIndex ，切线到指定线路索引值，数值必须在线路总数范围内，否则无效，可参考demo |







## RoomInfoBean房间信息集成类

- 1.邀请码生成方式

```java
RoomInfoBean.RoomInfoTypeInviteCode(String nickname, boolean roomType1vN, String inviteCode)
```

| 参数        | 说明                                              |
| ----------- | ------------------------------------------------- |
| nickname    | 用户昵称                                          |
| roomType1vN | 房间类型，true 1对多，false 1对1 ，大班必须为true |
| inviteCode  | 用户邀请码                                        |

生成RoomInfoBean

```java
public RoomInfoBean generateRoomInfoBean() 
```

返回

返回RoomInfoBean 实例





- 2.URL进入房间方式

```java
RoomInfoBean.RoomInfoTypeURL(String nickname, boolean roomType1vN, String enterRoomUrl) 
```

| 参数         | 说明                                                         |
| ------------ | ------------------------------------------------------------ |
| nickname     | 用户昵称                                                     |
| roomType1vN  | 房间类型，true 1对多，false 1对1 ，大班必须为true            |
| enterRoomUrl | 用户进入教室连接，生成方式推荐[从服务器获取](http://docs.duobeiyun.com/java#%E7%94%9F%E6%88%90%E8%8E%B7%E5%8F%96authinfo%E4%BF%A1%E6%81%AF%E7%9A%84url%E5%B0%86%E8%BF%94%E5%9B%9E%E5%80%BC%E4%BC%A0%E7%BB%99app%E7%9A%84sdk%E4%BD%BF%E7%94%A8)（注意url有时间有效期，失效后会进入房间失败） |

生成RoomInfoBean

```java
public RoomInfoBean generateRoomInfoBean() 
```

返回

返回RoomInfoBean 实例



## 错误码介绍

消息错误码StatusCode

| 具体错误码                  | 含义介绍                                  |
| --------------------------- | ----------------------------------------- |
| APPS_CONNECT_FATAL          | 网络情况非常差，可退出教室                |
| PB_LOCALPATH_ERR            | 文件下载失败建议切换线路                  |
| PLAYBACK_DATA_EMPTY_CONTENT | 文件下载失败建议切换线路                  |
| PLAYBACK_DATA_OPEN_FAIL     | 文件下载失败建议切换线路                  |
| STOPSUCCESS                 | 退出教室成功，调用sdk stopApi返回的结果码 |
| NO_SUPPORT_ROOM_TYPE        | 不支持的教室类型                          |