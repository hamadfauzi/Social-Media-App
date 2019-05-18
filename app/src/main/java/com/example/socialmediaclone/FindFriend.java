package com.example.socialmediaclone;

public class FindFriend {

    String profileimage,fullname,status;

    public FindFriend()
    {

    }
    public FindFriend(String profileimage,String fullname,String status)
    {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.status = status;

    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setImage(String profileimage) {
        this.profileimage = profileimage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullname() {
        return fullname;
    }

    public String getImage() {
        return profileimage;
    }

    public String getStatus() {
        return status;
    }
}
