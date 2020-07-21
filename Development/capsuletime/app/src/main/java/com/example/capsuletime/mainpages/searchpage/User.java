package com.example.capsuletime.mainpages.searchpage;

public class User {
    private String user_id;
    private String profile;
    private String nick_name;
    private String name;
    private String hashtag;
    private String text;
    private int viewtype;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }

    public User(String user_id, String profile, String nick_name, String name, String hashtag, int viewtype) {
        this.user_id = user_id;
        this.profile = profile;
        this.nick_name = nick_name;
        this.name = name;
        this.hashtag = hashtag;
        this.viewtype = viewtype;
    }
}
