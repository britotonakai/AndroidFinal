package com.example.homescreen;

public class Note {
    String noteID,noteTitle, noteContent, noteDateTime, noteStatus, notePin, noteLock, notePassword;

    public Note(String noteID,String noteTitle, String noteContent,String noteDateTime){
        this.noteID = noteID;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteDateTime = noteDateTime;
    }

    public Note(String noteID,String noteTitle, String noteContent,String noteDateTime, String noteDelete){
        this.noteID = noteID;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteDateTime = noteDateTime;
        this.noteStatus = noteDelete;
    }
    public Note(String noteID,String noteTitle, String noteContent,String noteDateTime, String notePin, String noteLock, String notePassword){
        this.noteID = noteID;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteDateTime = noteDateTime;
        this.notePin = notePin;
        this.noteLock = noteLock;
        this.notePassword = notePassword;
    }
    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDateTime() {
        return noteDateTime;
    }

    public void setNoteDateTime(String noteDateTime) {
        this.noteDateTime = noteDateTime;
    }

    public String getNoteStatus() {
        return noteStatus;
    }

    public void setNoteStatus(String noteStatus) {
        this.noteStatus = noteStatus;
    }

    public String getNotePin() {
        return notePin;
    }

    public void setNotePin(String notePin) {
        this.notePin = notePin;
    }

    public String getNoteLock() {
        return noteLock;
    }

    public void setNoteLock(String noteLock) {
        this.noteLock = noteLock;
    }

    public String getNotePassword() {
        return notePassword;
    }

    public void setNotePassword(String notePassword) {
        this.notePassword = notePassword;
    }
}

