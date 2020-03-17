package com.duobei.duobeiapp.bean;

import java.io.Serializable;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class OfflinePlaybackInfo implements Serializable {
    private String uuid;
    private String roomId;
    private String title;

    public OfflinePlaybackInfo() {
    }

    public OfflinePlaybackInfo(String uuid, String roomId, String title) {
    
        this.uuid = uuid;
        this.roomId = roomId;
        this.title = title;
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
}
