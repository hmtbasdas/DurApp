package com.hmtbasdas.durapp.Models;

import java.io.Serializable;

public class Post implements Serializable {
    private String userID, postID, date, content, replyAdminID, replyAdminContent;
    private boolean visibility, confirmation, isRead;

    public Post(){}

    public Post(String userID, String postID, String date, String content, String replyAdminID, String replyAdminContent, boolean visibility, boolean confirmation, boolean isRead) {
        this.userID = userID;
        this.postID = postID;
        this.date = date;
        this.content = content;
        this.replyAdminID = replyAdminID;
        this.replyAdminContent = replyAdminContent;
        this.visibility = visibility;
        this.confirmation = confirmation;
        this.isRead = isRead;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyAdminID() {
        return replyAdminID;
    }

    public void setReplyAdminID(String replyAdminID) {
        this.replyAdminID = replyAdminID;
    }

    public String getReplyAdminContent() {
        return replyAdminContent;
    }

    public void setReplyAdminContent(String replyAdminContent) {
        this.replyAdminContent = replyAdminContent;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }
}
