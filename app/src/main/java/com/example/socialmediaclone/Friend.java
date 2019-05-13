package com.example.socialmediaclone;

public class Friend {

    private String date,image,fullname;

    public Friend()
    {

    }
    public Friend(String date,String image,String fullname)
    {
        this.date = date;
        this.fullname = fullname;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getFullname() {
        return fullname;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
