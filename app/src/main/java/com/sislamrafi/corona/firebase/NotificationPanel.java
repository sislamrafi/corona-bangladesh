package com.sislamrafi.corona.firebase;

public class NotificationPanel {
    private String Title;
    private String body;
    private int toast;

    public NotificationPanel() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getToast() {
        return toast;
    }

    public void setToast(int toast) {
        this.toast = toast;
    }
}
