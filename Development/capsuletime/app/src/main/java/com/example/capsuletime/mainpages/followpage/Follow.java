package com.example.capsuletime.mainpages.followpage;

public class Follow {
    private String profile;
    private String nick_name;
    private String name;
    private int status_follow;

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

    public int getStatus_follow() {
        return status_follow;
    }

    public void setStatus_follow(int status_follow) {
        this.status_follow = status_follow;
    }

    public Follow(String profile, String nick_name, String name, int status_follow) {
        this.profile = profile;
        this.nick_name = nick_name;
        this.name = name;
        this.status_follow = status_follow;
    }
}
