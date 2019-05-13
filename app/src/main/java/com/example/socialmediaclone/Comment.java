package com.example.socialmediaclone;

public class Comment {

    public String comment,date,time,username;

    public Comment(){

    }
    public Comment(String comment,String date, String time , String username){
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username = username;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }
}
