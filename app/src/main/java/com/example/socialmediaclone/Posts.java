package com.example.socialmediaclone;

public class Posts {
    public String uid,time,date,postimage,description,profileimage,fullname;

    public Posts(){

    }

    public Posts(String uid,String time,String date,String postimage,String description,String profileimage,String fullname)
    {
        this.date = date;
        this.uid = uid;
        this.time = time;
        this.postimage = postimage;
        this.description = description;
        this.profileimage = profileimage;
        this.fullname = fullname;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPostimage() {
        return postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public String getTime() {
        return time;
    }

    public String getUid() {
        return uid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
