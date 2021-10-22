package com.hmtbasdas.durapp.Models;

public class Notice {

    String date, content, link;
    Boolean status;

    public Notice(){}

    public Notice(String date, String content, String link, Boolean status) {
        this.date = date;
        this.content = content;
        this.link = link;
        this.status = status;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
