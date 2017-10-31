package com.mainacreations.zareen;

import java.io.Serializable;

/**
 * Created by Thaher on 30-10-2017.
 */

public class Video_Info implements Serializable {
    String VideoName;
    String Code;
    String ChannelName;
    String Time;
    String Views;
    String ThumbNailUrl;

    public Video_Info() {
    }

    public String getThumbNailUrl() {
        return ThumbNailUrl;
    }

    public void setThumbNailUrl(String thumbNailUrl) {
        ThumbNailUrl = thumbNailUrl;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getViews() {
        return Views;
    }

    public void setViews(String views) {
        Views = views;
    }
}
