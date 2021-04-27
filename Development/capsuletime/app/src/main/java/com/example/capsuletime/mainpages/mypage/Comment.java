package com.example.capsuletime.mainpages.mypage;

import com.example.capsuletime.Commnetwo;

import java.util.List;

public class Comment {
    private String profile;
    private String nick_name;
    private String comment;
    private String day;
    private List<Commnetwo> replies;
    private int viewtype;

    public Comment(String profile, String nick_name, String comment, String day, List<Commnetwo> replies, int viewtype) {
        this.profile = profile;
        this.nick_name = nick_name;
        this.comment = comment;
        this.day = day;
        this.replies = replies;
        this.viewtype = viewtype;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<Commnetwo> getReplies() {
        return replies;
    }

    public void setReplies(List<Commnetwo> replies) {
        this.replies = replies;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void setViewtype(int viewtype) {
        this.viewtype = viewtype;
    }
}
