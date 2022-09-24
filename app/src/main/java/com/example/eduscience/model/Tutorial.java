package com.example.eduscience.model;

public class Tutorial {
    String id, content, imgUrl, modelUrl, lessonId;

    public Tutorial() {}

    public Tutorial(String content, String imgUrl, String modelUrl, String lessonId) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.modelUrl = modelUrl;
        this.lessonId = lessonId;
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

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }
}
