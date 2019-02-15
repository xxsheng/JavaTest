package com.warm.share.model;

import com.warm.share.entity.User;

public class ArticleUser {

    private Long id;

    private String title;

    private Long releaseTime;

    private String content;

    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "ArticleUser{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseTime=" + releaseTime +
                ", content='" + content + '\'' +
                ", user=" + user +
                '}';
    }
}
