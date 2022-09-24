package com.example.eduscience.model;

public class Lesson {
    String id, imgUrl, name;

    public Lesson() {}

    public Lesson(String imgUrl, String name) {
        this.imgUrl = imgUrl;
        this.name = name;
    }

    public void setId(String id) { this.id = id; }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() { return  id; }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }
}
