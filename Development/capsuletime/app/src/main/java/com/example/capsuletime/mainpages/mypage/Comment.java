package com.example.capsuletime.mainpages.mypage;

import com.example.capsuletime.Commnetwo;

import java.util.List;

public class Comment {
    private String profile;
    private String nick_name;
    private String comment;
    private List<Commnetwo> replies;
    private int viewtype;

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

    public Comment(String profile, String nick_name, String comment, List<Commnetwo> replies, int viewtype) {
        this.profile = profile;
        this.nick_name = nick_name;
        this.comment = comment;
        this.replies = replies;
        this.viewtype = viewtype;
    }
}
