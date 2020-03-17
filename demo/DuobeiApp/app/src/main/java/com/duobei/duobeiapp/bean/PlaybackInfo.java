package com.duobei.duobeiapp.bean;

import java.io.Serializable;

/***
 ***Create DuobeiApp by yangge at 2017/5/15 
 **/
public class PlaybackInfo implements Serializable {
    private String uuid;
    private String roomId;
    private String title;
    private String time;
    private boolean iscandown;

   

    private boolean isNativePlayback;

    public PlaybackInfo(String uuid, String roomId, String title, String time, boolean iscandown, String play_imageurl,boolean isNativePlayback) {
        this.uuid = uuid;
        this.roomId = roomId;
        this.title = title;
        this.time = time;
        this.iscandown = iscandown;
        this.play_imageurl = play_imageurl;
        this.isNativePlayback = isNativePlayback;
    }
    public boolean isNativePlayback() {
        return isNativePlayback;
    }

    public void setNativePlayback(boolean nativePlayback) {
        isNativePlayback = nativePlayback;
    }
    private String play_imageurl;

    public String getPlay_imageurl() {
        return play_imageurl;
    }

    public void setPlay_imageurl(String play_imageurl) {
        this.play_imageurl = play_imageurl;
    }



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean iscandown() {
        return iscandown;
    }

    public void setIscandown(boolean iscandown) {
        this.iscandown = iscandown;
    }

    public PlaybackInfo() {
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
