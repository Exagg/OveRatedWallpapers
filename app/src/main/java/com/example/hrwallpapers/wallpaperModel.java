package com.example.hrwallpapers;

import java.io.Serializable;
import java.util.ArrayList;

public class wallpaperModel {

    String thumbSrc,originalSrc,id;
    booleanListeners isFavorite;
    ArrayList<String> tagList;
    public String resolution;
    public int tagsCurrentPage = 0;
    public int originalWidth = 0;
    public int originalHeight = 0;
    boolean isPng=false;


    public wallpaperModel(String thumbSrc,String originalSrc,String id)
    {
        this.thumbSrc = thumbSrc;
        this.originalSrc = originalSrc;
        this.id = id;
        isFavorite = new booleanListeners();
        this.tagList = new ArrayList<>();
    }

    public queryModel getTagQueryModel(int tagPosition)
    {
        if(tagList.size() > tagPosition)
        {
            String tag = tagList.get(tagPosition);

            queryModel model= new queryModel(true,true,true,true,true,true,0,0,0,0,0,"","desc",tag,"random");
            model.setActivePage(tagsCurrentPage);
            return model;
        }
        else return null;
    }
}
