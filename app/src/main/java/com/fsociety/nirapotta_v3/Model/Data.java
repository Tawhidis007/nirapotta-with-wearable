package com.fsociety.nirapotta_v3.Model;

public class Data {
    private String title;
    private String detail;
    private String med;
    private String harrass;
    private String emergency;


    public Data(String title, String detail, String med, String harrass, String emergency) {
        this.title = title;
        this.detail = detail;
        this.med = med;
        this.harrass = harrass;
        this.emergency = emergency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
