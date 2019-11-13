package com.notpad.hihimeow.utils;

public class Matches {
    private String meowID;
    private String meowName;
    private String meowImageProfile;
    private String lastMessage;

    public Matches(String meowID, String meowName, String meowImageProfilem, String lastMessage) {
        this.meowID = meowID;
        this.meowName = meowName;
        this.meowImageProfile = meowImageProfilem;
        this.lastMessage = lastMessage;
    }



    public String getMeowID() {
        return meowID;
    }

    public void setMeowID(String meowID) {
        this.meowID = meowID;
    }

    public String getMeowName() {
        return meowName;
    }

    public void setMeowName(String meowName) {
        this.meowName = meowName;
    }


    public String getMeowImageProfile() {
        return meowImageProfile;
    }

    public void setMeowImageProfile(String meowImageProfile) {
        this.meowImageProfile = meowImageProfile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
