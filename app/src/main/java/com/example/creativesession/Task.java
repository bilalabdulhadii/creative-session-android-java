package com.example.creativesession;

public class Task {
    private int id;
    private String title;
    private String createdAt;
    private boolean isCompleted;
    private int completedSessions;
    private int desiredSessions;

    public Task(String title, int desiredSessions) {
        this.title = title;
        this.desiredSessions = desiredSessions;
    }

    public Task(String title, String createdAt, boolean isCompleted, int completedSessions, int desiredSessions) {
        this.title = title;
        this.createdAt = createdAt;
        this.isCompleted = isCompleted;
        this.completedSessions = completedSessions;
        this.desiredSessions = desiredSessions;
    }

    public Task(int id, String title, String createdAt, boolean isCompleted, int completedSessions, int desiredSessions) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.isCompleted = isCompleted;
        this.completedSessions = completedSessions;
        this.desiredSessions = desiredSessions;
    }

    // Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isCompleted() { return isCompleted; }

    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getCompletedSessions() { return completedSessions; }

    public void setCompletedSessions(int completedCount) { this.completedSessions = completedCount; }

    public int getDesiredSessions() { return desiredSessions; }

    public void setDesiredSessions(int desiredSessions) { this.desiredSessions = desiredSessions; }
}
