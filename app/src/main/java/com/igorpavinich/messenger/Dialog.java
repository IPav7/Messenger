package com.igorpavinich.messenger;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Igor Pavinich on 30.11.2017.
 */

public class Dialog {
    private String second;
    private String name;
    private Bitmap picture;

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String lastMessage;
    private Date date;
    private boolean unread;

    public Dialog() {
    }

    public Dialog(String second, String lastMessage, Date date, boolean unread) {
        this.second = second;
        this.lastMessage = lastMessage;
        this.date = date;
        this.unread = unread;
    }

    @Override
    public String toString() {
        return "Dialog{" +
                "second='" + second + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", date=" + date +
                ", unread=" + unread +
                '}';
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}
