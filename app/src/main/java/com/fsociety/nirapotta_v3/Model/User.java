package com.fsociety.nirapotta_v3.Model;

public class User {

    private String email, password, name, phone, subscription, subscriberID;

    public User() {

    }

    public User(String email, String password, String name, String phone, String subscription, String subscriberID) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.subscription = subscription;
        this.subscriberID = subscriberID;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getSubscriberID() {
        return subscriberID;
    }

    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }
}
