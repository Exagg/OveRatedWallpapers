package com.example.hrwallpapers;

public class wallpaperModel {

    String thumbSrc,originalSrc,id;
    booleanListeners isFavorite;

    public wallpaperModel(String thumbSrc,String originalSrc,String id)
    {
        this.thumbSrc = thumbSrc;
        this.originalSrc = originalSrc;
        this.id = id;
        isFavorite = new booleanListeners();
    }
}
