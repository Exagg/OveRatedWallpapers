package com.slice.wallpapers;

public class MenuModel {
    public String name;
    public boolean hasChildren,isGroup,hasImage;
    public int drawableID,colorID;
    public queryModel queryModel;

    public MenuModel(String name,boolean isGroup,boolean hasChildren,boolean hasImage,int drawableID,queryModel queryModel)
    {
        this.name = name;
        this.hasChildren = hasChildren;
        this.isGroup = isGroup;
        this.hasImage = hasImage;
        this.drawableID = drawableID;
        this.queryModel = queryModel;
        this.colorID = MainActivity.ma.getResources().getColor(R.color.gray);
    }


    public MenuModel(String name,boolean isGroup,boolean hasChildren,boolean hasImage,int drawableID,queryModel queryModel,int colorID)
    {
        this.name = name;
        this.hasChildren = hasChildren;
        this.isGroup = isGroup;
        this.hasImage = hasImage;
        this.drawableID = drawableID;
        this.queryModel = queryModel;
        this.colorID = colorID;
    }

}
