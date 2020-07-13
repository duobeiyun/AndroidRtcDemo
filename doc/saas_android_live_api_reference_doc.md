---
title:  Android 直播 API 参考文档
---

## 主要使用类

- LivePlayer接口类包含应用程序调用的主要方法
- LivePlayerView 直播内置辅助类，如果需要使用展示ppt，划线设置为可见，否则隐藏即可

## 主要使用接口

LiveMessageCallback 直播相关消息回调接口

MicRequestCallback 本地音频开始推送，关闭接口

DBLocalVideoCallback 本地视频打开关闭接口

EnterRoomResultCallback 进入教室结果回调接口

WebrtcMicCallback 上麦过程相关消息接口

VideoCallback 接受视频打开关闭接口

ChatMsgRefreshCallback 新的聊天信息回调接口（推荐使用此种方式）



## LivePlayer使用方式

- #### 创建实例

```java
    public LivePlayer(Context context, LivePlayerView plivePlayerView) 
```

参数

| 参数           | 说明                                 |
| -------------- | ------------------------------------ |
| context        | 安卓活动 (Android Activity) 的上下文 |
| LivePlayerView | 内置辅助类                           |
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

3.设置本地视频

```Java
public void setLocalVideoFrameSurface(GLFrameSurface surface)
```

参数

| 参数    | 说明     |
| ------- | -------- |
| surface | 本地视频 |



- [x] 必须设置回调

-   视频打开关闭回调

```java
public void setVideoCallback(VideoCallback callback) 
```

| 参数     | 说明                                                   |
| -------- | ------------------------------------------------------ |
| callback | 直播某一个视频打开关闭接口，对视频view进行显示隐藏控制 |

-   直播相关消息接口回调

```java
public void setLiveMessageCallback(LiveMessageCallback callback)
```

| 参数     | 说明                 |
| -------- | -------------------- |
| callback | 直播相关消息接口回调 |

-   直播本地视频开关回调

```java
 public void setDbLocalVideoCallback(DBLocalVideoCallback dbLocalVideoCallback)
```

| 参数                 | 说明                                           |
| -------------------- | ---------------------------------------------- |
| dbLocalVideoCallback | 直播本地视频打开关系接口回调，控制view隐藏显示 |





## 必备回调接口介绍

- LiveMessageCallback 

| 回调函数                                        | 函数介绍                                    | 参数说明                                                     |
| ----------------------------------------------- | ------------------------------------------- | ------------------------------------------------------------ |
| void handleContent(ArrayList list)              | 以集合形式返回聊天信息                      | 聊天消息集合                                                 |
| void kickoff()                                  | 直播同一账号在不同地方登录，导致前者被踢    |                                                              |
| void pptMessage(int currentpage, int totalpage) | 收到ppt相关函数，（新版大班可以忽略此参数） | currentpage 当前页数   ，totalpage ppt总页数                 |
| void connected()                                | 进入教室成功                                |                                                              |
| void connectFail(String s)                      | 进入教室失败                                | 失败原因                                                     |
| void handleClearChat();                         | 清除聊天信息                                | 注意如果不注册ChatMsgRefreshCallback，聊天消息接收会走handleContent回调，自己需要在此回调中情况聊天消息，否则会有消息重复 |
| void statusCode(int code, String msg);          | 直播相关状态码回调                          | code错误码，msg 错误信息，具体错误码，末尾有介绍             |
| void loading();                                 | 进入教室的时候调用                          |                                                              |
| void handleContent(ChatBean chatBean)           | 以单条形式返回聊天信息                      | 注意如果不注册ChatMsgRefreshCallback，聊天消息接收会走handleContent回调，自己需要在此回调中情况聊天消息，否则会有消息重复 |
| void handleAnnounceMessage(String message)      | 接受到公告信息                              | 公告内容                                                     |
| void networkNotConnected()                      | 网络未连接                                  |                                                              |
| void voteStart(int type);                       | 投票开始                                    | 10  单选 AB两个选项 ；  11  单选 ABC三个选项  ；12  单选 ABCD四个选项；   13  单选 ABCDE五个选项；    14  单选 ABCDEF六个选项；            30  判断 √或× |
| void voteEnd()                                  | 投票结束                                    |                                                              |
| void voteClose()                                | 投票关闭                                    |                                                              |
| void voteInfo(JSONObject jsonObject)            | 投票数据                                    | 如果是两个选项的单选，则只需取得A,B选项值，三个选项的单选，取A,B,C的值，以此类推*                   如果是判断，则需A，B的值，A表示选择√的人数，B表示选择×的人数*                   {"A":0,"B":1,"C":0,"D":0,"E":0} |
| void teacherOnline()                            | 老师在线                                    |                                                              |
| void onlineUserCount(int countnumber)           | 在线人数                                    | countnumber 个数                                             |



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



- DBLocalVideoCallback 本地视频开关回调

| 回调函数                | 函数介绍         |
| ----------------------- | ---------------- |
| void showLocalVideo();  | 展示本地视频窗口 |
| void closeLocalVideo(); | 关闭本地视频窗口 |









## 可选择回调接口介绍

- FirstVideoFrameCallback 接收到视频第一帧

| 函数名                                            | 函数介绍       | 参数说明                                               |
| ------------------------------------------------- | -------------- | ------------------------------------------------------ |
| void receiveVideoFirstFrame(int type, int handle) | 收到视频第一帧 | type 角色类型，参考RoleType；handle 老师视频角色索引值 |



- BaseCallbackNewAdded 远端音频相关回调

| 函数名                                      | 函数介绍             | 参数说明          |
| ------------------------------------------- | -------------------- | ----------------- |
| void notifyUISdk2CreateAudioPcm(String uid) | 收到远端其他学生上麦 | uid： 上麦学生uid |
| void notifyUISdk2DeletAudioPcm(String uid)  | 收到远端其他学生下麦 | uid： 下麦学生uid |



- ###### RaiseHandCallback 举手回调（**com 域名学生上台上麦接口**）

| 函数名             | 函数介绍               | 参数说明 |
| ------------------ | ---------------------- | -------- |
| void handSuccess() | 举手成功               |          |
| void handFail()    | 举手失败               |          |
| void downHand();   | 老师取消当前学生的举手 |          |
| void banHand()     | 老师禁止举手           |          |
| requestCamera      | 用户允许打开摄像头     |          |



- MicRequestCallback 本地音频打开关闭回调

| 函数名                | 函数介绍     |
| --------------------- | ------------ |
| void startSendMic（） | 推送本地音频 |
| void closeMic         | 关闭本地音频 |





- ChatMsgRefreshCallback 新的聊天信息接口

   **概述**

​       使用此接口为新的聊天回调，每次回调中为所有的聊天信息，如果注册此接口则LiveMessageCallback 中关于聊天回调不会生效，推荐使用此种方式

​      如果需要区分老师，学生，助教信息可使用LivePlayer：：getChatModule()获取聊天module，可以分别得到各自聊天信息

  

| 函数名                                          | 函数介绍     | 参数说明   |
| ----------------------------------------------- | ------------ | ---------- |
| void refreshChatMsg(ArrayList allMsg) | 聊天消息接口 | 所有的消息 |

- WebrtcVoteCallback 投票答题接口补充（**此接口只在新版大班中生效**）

​    **概述**

​    新版大班中老师点击答题重置后被调用，作用：清空当前答题结果，学生重新答题

| 函数名       | 函数介绍                   | 参数说明 |
| ------------ | -------------------------- | -------- |
| void reset() | 答题重置，清空当前答题结果 |          |

-   ###### QACallback 问答消息回调（**此接口只在net中有效**）

   **概述**

   在新版大班中老师引用并且回答了学生疑问后或删除某一条问答的被触发回调。

| 函数名                         | 函数介绍     | 参数说明     |
| ------------------------------ | ------------ | ------------ |
| void addQa(List<QABean> beans) | 增加答疑     | 答疑数据     |
| void removeQa(String id)       | 删除一条答疑 | 被删除答疑id |

- WebrtcVoteCallback投票ui重置（**此接口只在net中有效**）

  **概述**

  在新版大班中老师重置了答题

| 函数名               | 函数介绍 | 参数说明     |
| -------------------- | -------- | ------------ |
| void reset(int type) | 重置答题 | 当前答题类型 |



- ###### WebrtcInteractionCallback（**此接口只在net中有效**）

  **概述**

  学生与老师上台上麦交互接口回调

| 函数名                                                       | 函数介绍                                 | 参数说明                                                     |
| :----------------------------------------------------------- | ---------------------------------------- | ------------------------------------------------------------ |
| void receiveInteractionInvite(int type, String fromUserRole, String fromUsername) | 接受到来自老师或助教得上台或上麦邀请     | type ：0 视频，1 音频；fromUserRole：邀请者角色fromUsername：邀请人昵称 |
| void startPushLocalAudio()                                   | 开始推送本地音频                         |                                                              |
| void stopPushLocalAudio()                                    | 停止推送本地音频                         |                                                              |
| void onVideoSateChange(WebrtcInteractionBean bean)           | 当前上台或上麦列表人数发生变化获取的通知 | [bean](#WebrtcInteractionBean)：当前上台和上麦列表中的人数   |
| void dealInteractionResult(int type, InteractionResultBean bean) | 主动发起交互的结果处理                   | type：请求类型动作      [InteractionResultBean](#InteractionResultBean)：请求处理结果 |
| void hasJoinedInteraction(int type)                          | 重复加入列表通知                         | type:0 视频 1 音频                                           |



void dealInteractionResult(int type, InteractionResultBean bean) 参数详细接受：

type：

```java
LiveMessage.WEBRTC_REQ_Interaction  
```

请求加入交互列表

```java
LiveMessage.WEBRTC_Abort_Interaction
```

请求离开交互列表

```java
LiveMessage.WEBRTC_Accept_Interaction
```

请求接受邀请交互列表

```java
LiveMessage.WEBRTC_Quit_Interaction
```

请求退出停止当前的音视频交互过程

bean 当前上台上麦列表：

 





## 生命周期基本控制

- 开始

```java
public void start() 
```

- 说明

设置必备的回调函数后，调用此函数开启直播，可能会抛出异常：

​        1.相关必备回调或参数未设置

​        2.未在主线程调用此函数



- 暂停

```java
public void pause()
```

- 说明

设置必备的回调函数后，调用此函数暂停直播，会走相关回调停掉音视频，可在进入后台的时候调用



- 恢复

```java
public void recovery() 
```

- 说明

设置必备的回调函数后，调用此函数回复直播，会走相关回调停掉音视频，可在从后台回复到进入前台的时候调用

即

 

- 退出教室停止sdk

```java
public void stopApi() 
```

- 说明

在Android 离开教室时调用，此方法为异步方法，需要等待在回调LiveMessageCallback ：：statusCode中，StatusCode.STOPSUCCESS即可调用Android finish方法离开教室，否则会造成教室状态混乱



- 销毁

```java
public void release() 
```

- 说明

在Android onDestroy 回调中调用销毁sdk

## SDK 主要使用方法补充介绍

### 1.聊天相关介绍

####   1.1移动端发送聊天

```java
public int sendMessage(String msg) 
```

- 参数说明：msg 聊天具体内容

- 返回值说明：

   

  | 返回值 | 含义介绍                   |
  | ------ | -------------------------- |
  | 0      | 发送成功                   |
  | -1     | 聊天内容为空               |
  | -2     | 当前教室已经开启全体禁言   |
  | -3     | 个人被禁言                 |
  | -4     | 发送聊天消息太过频繁       |
  | -5     | 当前聊天内容超过字数长度限 |

### 2.本地音视频

####        2.1   关闭本地视频        

```java
public int closeLocalAudioVideo() 
```

-   概述：移动端主动关闭本地个人的音视频

-   返回值介绍：-1 触发sdk函数频率设置

​                                0 成功使用

### 2.2 学生与老师建立起音视频交互逻辑

####     2.2.1 com域名

 主动举手

```Java
public int raiseHand()
```



-   概述：移动端主动申请上台或上麦，相关方法将在[RaiseHandCallback](#RaiseHandCallback 举手回调（**com 域名学生上台上麦接口**）) 被触发,然后等待被老师同意上台或上麦，老师只要同意即可进行上台上麦

-    返回值介绍：-1 触发sdk函数频率设置

​                                 0 成功使用

####     2.2.2 新版大班net域名

```java
public void getWebrtcInteractionCameraList() 
```

- 概述：主动获取当前上视频列表
- 用途：当用户首次进入房间的时候调用，获取当前房间上台和视频列表

####     

```java
public void getWebrtcInteractionAudioList() 
```

- 概述：主动获取当前上麦列表
- 用途：当用户首次进入房间的时候调用，获取当前房间上麦列表

####     

```java
public void requestWebrtcJoinInteractionQueue(int type) 
```

- 概述：主动发起请求进入交互队列
- 用途：用户可以主动请求加入上台上麦列表，这样老师端可以看到在列表的学生，方便老师操作学生上台或上麦，会触发[WebrtcInteractionCallback](#WebrtcInteractionCallback（**此接口只在net中有效**）)回调
- 参数: 0 视频  1音频

####     

```java
public void leaveWebrtcInteractionQueue(int type) 
```

- 概述：主动发起请求离开交互队列
- 用途：用户可以主动请求离开上台上麦列表，会触发[WebrtcInteractionCallback](#WebrtcInteractionCallback（**此接口只在net中有效**）)回调
- 参数: 0 视频  1音频

####     

```java
public void acceptWebrtcInteraction(int type) 
```

- 概述：用户选择接受来自老师或助教的邀请
- 用途：当接受到邀请后，调用此方法则可以进行上台或上麦与老师进行音视频交互，会触发[WebrtcInteractionCallback](#WebrtcInteractionCallback（**此接口只在net中有效**）)回调
- 参数: 0 视频  1音频

####     

```java
public void refuseWebrtcInteraction(int type) 
```

- 概述：用户选择拒绝来自老师或助教的邀请
- 用途：学生端根据自身情况，选择是否接受老师的邀请，会触发[WebrtcInteractionCallback](#WebrtcInteractionCallback（**此接口只在net中有效**）)回调
- 参数: 0 视频  1音频

####     

```java
public void closeLocalAudioVideo() 
```

- 概述：用户在已经上台或上麦的过程中，主动断开与老师的音视频交互，会触发[WebrtcInteractionCallback](#WebrtcInteractionCallback（**此接口只在net中有效**）)回调



### 3.答题

####    3.1选择答题选项并作答

```Java
public void vote(final int an)
```



-   概述：当老师端发起答题后，移动端可以答题

-   参数介绍：选择问答的答案

### 4.只接受视频流

####     4.1选择只接受音频流

```java
public int openVideoRec(boolean isopen) 
```



-   概述：移动端主动申请只接受音频，可以节约流量
-   参数介绍：isopen true接受视频；false 只接受音频

-    返回值介绍：-1 触发sdk函数频率设置

​                                 0 成功使用

### 5.主动获取当前qa

####     

```java
public int startPullQA()
```



-   概述：移动端主动获取当前教室的问答内容
-   qa：问题和回答
-   qa用途：教室内可能会有很多学生进行聊天提问，老师可以进行针对某一条进行回答。会触发回调[QACallback](#QACallback 问答消息回调（**此接口只在net中有效**）) 问答消息回调

### 6.获取教室当前配置（只有net环境有效）

老师在创建课程的时候允许自定义一些基本配置，（包括不限于是否开启问答，是否设置跑马灯，是否进行发言聊天过滤，是否设置聊天徽章，是否有自定义tab页；）客户端在进入教室的时候要主动拉取。

（webrtc_*为默认的sdk自带界面，方便用户进行接口的验证与使用，可找相应对接人员提供源代码）

参考BaseSDKVideoActivity.requestConfigureWhenEnterroomSuccess()函数



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

| 具体错误码                   | 含义介绍                                  |
| ---------------------------- | ----------------------------------------- |
| FAILCAMERA                   | 没有相机权限                              |
| FAILMIC                      | 音频播放权限                              |
| APPS_CONNECT_FATAL           | 网络情况非常差，可退出教室                |
| TEACHER_ONLINE               | 老师上线                                  |
| MEMBER_CHAT_DENY             | 个人被禁言                                |
| STOPSUCCESS                  | 退出教室成功，调用sdk stopApi返回的结果码 |
| LiveMessage.START_DBY_RESULT | 进入教室结果码                            |
| TEACHER_HANDED_FINISH_CLASS  | 老师主动下课                              |
| TEACHER_ONLINE               | 老师上线                                  |
| TEACHER_OFFLINE              | 老师下线                                  |
| SEND_MESSAGE_FREQ            | 聊天发送频开太快                          |
| ALL_MEMBER_CHAT_DENY         | 全体禁言                                  |
| MEMBER_CHAT_DENY             | 个人禁言                                  |
| FAILCAMERA                   | 本地摄像头打开失败                        |
| FAILMIC                      | 本地麦克风打开失败                        |
| CLOSE_AUDIO_VIDEO_RESULT     | 关闭本地音视频结果                        |
| URL_PROTOCAL_UNLEGAL         | ur设置协议不符要求                        |
|                              |                                           |



## 部分bean介绍

### 新版大班net域名上台上麦参考信息

#### InteractionResultBean

客户端主动发起与老师端交互结果处理

| boolean success; | 此次请求结果是否成功 |
| ---------------- | -------------------- |
| String reason;   | 失败原因             |

#### WebrtcInteractionBean  

上台上麦列表

####  

| String type             | 0 视频  1音频           |
| ----------------------- | ----------------------- |
| ArrayList<Bean> joined  | 正在上台的学生列表      |
| ArrayList<Bean> inqueue | /在申请状态中的学生列表 |

WebrtcInteractionBean.Bean

| String userId        | 被邀请人列表id                                               |
| :------------------- | ------------------------------------------------------------ |
| String userRole      | 被邀请人角色                                                 |
| String userName      | 被邀请人昵称                                                 |
| String fromUserId    | 发起邀请人id（只有老师或助教主动邀请的学生才存在，否则为空） |
| String fromUserName  | 发起邀请昵称（只有老师或助教主动邀请的学生才存在，否则为空） |
| String fromUserRole; | 发起邀请角色（只有老师或助教主动邀请的学生才存在，否则为空） |



















