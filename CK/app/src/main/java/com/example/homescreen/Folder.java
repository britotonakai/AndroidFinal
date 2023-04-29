package com.example.homescreen;

public class Folder {
    String folderID, folderName, folderDay;

    public Folder(String folderID, String folderName, String folderDay){
        this.folderID = folderID;
        this.folderName = folderName;
        this.folderDay = folderDay;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderDay() {
        return folderDay;
    }

    public void setFolderDay(String folderDay) {
        this.folderDay = folderDay;
    }
}
