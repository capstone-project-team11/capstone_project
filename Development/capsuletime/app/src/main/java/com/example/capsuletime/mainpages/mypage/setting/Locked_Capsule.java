package com.example.capsuletime.mainpages.mypage.setting;

public class Locked_Capsule {
    private String profile;
    private String nick_name;
    private String name;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locked_Capsule(String profile, String nick_name, String name, boolean isSelected) {
        this.profile = profile;
        this.nick_name = nick_name;
        this.name = name;
        this.isSelected = isSelected;
    }
}
