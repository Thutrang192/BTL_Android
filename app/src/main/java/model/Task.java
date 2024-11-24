package model;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private String name;
    private String date;
    private Boolean completed;
    private Integer hour;
    private Integer minute;
    private String idUser;

    // Constructor, getters, and setters


    public Task() {
    }

    public Task(String id, String name, String date, Boolean completed, Integer hour, Integer minute) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.completed = completed;
        this.hour = hour;
        this.minute = minute;
    }

    public Task(String id, String name, String date, Boolean completed, Integer hour, Integer minute, String idUser) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.completed = completed;
        this.hour = hour;
        this.minute = minute;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
