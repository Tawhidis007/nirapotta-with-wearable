package com.fsociety.nirapotta_v3.Model;

public class Sender {
    private Data data;
    private Notification notification;
    private String to;

    public Sender(String to, Notification notification, Data data) {
        this.data = data;
        this.setNotification(notification);
        this.setTo(to);
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

