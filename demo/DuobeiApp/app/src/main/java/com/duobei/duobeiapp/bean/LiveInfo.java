package com.duobei.duobeiapp.bean;

import java.io.Serializable;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 *      直播相关的信息
 *      注：
 *          此类只是demo做为演示使用，具体的直播业务实体类又开发者根据自己公司业务书写
 **/
public class LiveInfo implements Serializable {

    private String uuid;
    private String roomId;
    private String title;
    private boolean isLiveStart;
    private String liveImage;
    private String nickname;
    private boolean is1vN;

   

    public LiveInfo(String uuid, String roomId, String title, boolean isLiveStart, String liveImage, String nickname,boolean is1vN) {
        this.uuid = uuid;
        this.roomId = roomId;
        this.title = title;
        this.isLiveStart = isLiveStart;
        this.liveImage = liveImage;
        this.nickname = nickname;
        this.is1vN = is1vN;
    }
    public boolean isIs1vN() {
        return is1vN;
    }

    public void setIs1vN(boolean is1vN) {
        this.is1vN = is1vN;
    }
    public boolean isLiveStart() {
        return isLiveStart;
    }

    public void setLiveStart(boolean liveStart) {
        isLiveStart = liveStart;
    }

    public String getLiveImage() {
        return liveImage;
    }

    public void setLiveImage(String liveImage) {
        this.liveImage = liveImage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LiveInfo() {
    }

}
