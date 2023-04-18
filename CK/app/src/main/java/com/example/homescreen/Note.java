package com.example.homescreen;

public class Note {
    String noteTitle, noteContent, noteDateTime;

    public Note(String noteTitle, String noteContent,String noteDateTime){
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteDateTime = noteDateTime;
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
}

