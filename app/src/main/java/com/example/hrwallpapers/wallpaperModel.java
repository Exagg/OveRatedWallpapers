package com.example.hrwallpapers;

import java.io.Serializable;

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
