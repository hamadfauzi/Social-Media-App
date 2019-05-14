package com.example.socialmediaclone;

public class Message {

    private String date,from,message,time,type;

    public Message()
    {

    }
    public Message(String date, String from, String message,String time,String type)
    {
        this.date = date;
        this.message = message;
        this.from = from;
        this.time = time;
        this.type = type;

    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }
}
