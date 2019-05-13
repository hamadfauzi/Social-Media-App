package com.example.socialmediaclone;

public class FindFriend {

    String image,fullname,status;

    public FindFriend()
    {

    }
    public FindFriend(String image,String fullname,String status)
    {
        this.image = image;
        this.fullname = fullname;
        this.status = status;

    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullname() {
        return fullname;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }
}
