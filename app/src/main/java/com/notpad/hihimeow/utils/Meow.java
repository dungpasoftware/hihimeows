package com.notpad.hihimeow.utils;

public class Meow {
    private String meowID;
    private String name;
    private String profileImageUrl;

    public Meow() {
    }

    public Meow(String meowID, String name, String profileImageUrl) {
        this.meowID = meowID;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getMeowID() {
        return meowID;
    }

    public void setMeowID(String meowID) {
        this.meowID = meowID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


}
