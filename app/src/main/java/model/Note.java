package model;

import java.io.Serializable;

public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private String date;
    private String theme;
    private String password;

    public Note() {
    }

    public Note(String id, String title, String content, String date, String theme, String password) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.theme = theme;
        this.password = password;
    }


    public Note(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Note(String id, String title, String date, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
