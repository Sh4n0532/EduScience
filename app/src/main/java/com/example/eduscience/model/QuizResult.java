package com.example.eduscience.model;

public class QuizResult {
    String id, userId, lessonId;
    int mark;

    public QuizResult() {
    }

    public QuizResult(String userId, String lessonId, int mark) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.mark = mark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
