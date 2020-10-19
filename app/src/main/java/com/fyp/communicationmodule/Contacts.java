package com.fyp.communicationmodule;

public class Contacts {
    public String fullName, userID, profileImage;

    public Contacts()
    {

    }

    public Contacts(String fullName, String userID, String profileImage) {
        this.fullName = fullName;
        this.userID = userID;
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
