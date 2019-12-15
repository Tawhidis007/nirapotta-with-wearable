package com.fsociety.nirapotta_v3.Model;

public class Notification {
    private String click_action;
    private String sound;
    private String title;
    private String body;

    public Notification(String click_action, String sound, String title, String body) {
        this.click_action = click_action;
        this.sound = sound;
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }
}
