package com.igorpavinich.messenger;

/**
 * Created by Igor Pavinich on 30.11.2017.
 */

public class Dialog {
    private String lastMessage;
    private String date;
    private String name;
    private String surname;

    public Dialog(String name, String surname, String lastMessage, String date) {
        this.lastMessage = lastMessage;
        this.date = date;
        this.name = name;
        this.surname = surname;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
