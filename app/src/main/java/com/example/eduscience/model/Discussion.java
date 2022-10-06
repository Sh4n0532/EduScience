package com.example.eduscience.model;

public class Discussion {
    String id, content, imgUrl, userId, createdOn;
    Boolean approve;

    public Discussion() {
    }

    public Discussion(String content, String imgUrl, String userId, String createdOn, Boolean approve) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.userId = userId;
        this.createdOn = createdOn;
        this.approve = approve;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getApprove() { return approve; }

    public void setApprove(Boolean approve) { this.approve = approve; }
}

