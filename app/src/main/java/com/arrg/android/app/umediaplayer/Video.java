package com.arrg.android.app.umediaplayer;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Video implements Serializable {

    private Boolean isPlaying;
    private Bitmap photoAlbum;
    private String artistName;
    private String nameOfTheSong;
    private String pathOfFile;
    private String displayName;
    private Integer duration;

    public Video() {
        this.isPlaying = false;
    }

    public Boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(Boolean playing) {
        isPlaying = playing;
    }

    public Bitmap getPhotoAlbum() {
        return photoAlbum;
    }

    public void setPhotoAlbum(Bitmap photoAlbum) {
        this.photoAlbum = photoAlbum;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getNameOfTheSong() {
        return nameOfTheSong;
    }

    public void setNameOfTheSong(String nameOfTheSong) {
        this.nameOfTheSong = nameOfTheSong;
    }

    public String getPathOfFile() {
        return pathOfFile;
    }

    public void setPathOfFile(String pathOfFile) {
        this.pathOfFile = pathOfFile;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
