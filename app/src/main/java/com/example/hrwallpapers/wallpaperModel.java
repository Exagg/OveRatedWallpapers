package com.example.hrwallpapers;

import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static android.support.constraint.motion.MotionScene.TAG;

public class wallpaperModel {

    String thumbSrc,originalSrc,id;
    booleanListeners isFavorite;
    ArrayList<String> tagList;
    public String resolution;
    public int tagsCurrentPage = 0;
    public int originalWidth = 0;
    public int originalHeight = 0;
    boolean isPng=false;
    private String filePath = null;


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

            queryModel model= new queryModel(true,true,true,true,true,true,0,0,0,0,0,"","desc",tag,"random",null);
            model.setActivePage(tagsCurrentPage);
            return model;
        }
        else return null;
    }

    public void setFilePath(File file)
    {
        Log.i(TAG, "setFilePath: " + file.getPath());
        this.filePath = file.getPath();
    }

    public void setFilePath(String filePath)
    {
        Log.i(TAG, "setFilePath: " + filePath);
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return this.filePath;
    }
}
