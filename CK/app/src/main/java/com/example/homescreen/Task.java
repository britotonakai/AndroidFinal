package com.example.homescreen;

public class Task {
    private String id;
    private int status;
    private String task;

    public Task(String id, int status, String task) {
        this.id = id;
        this.status = status;
        this.task = task;
    }

    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
