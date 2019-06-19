package com.example.hrwallpapers;

public class MenuModel {
    public String name;
    public boolean hasChildren,isGroup,hasImage;
    public int drawableID;
    public queryModel queryModel;

    public MenuModel(String name,boolean isGroup,boolean hasChildren,boolean hasImage,int drawableID,queryModel queryModel)
    {
        this.name = name;
        this.hasChildren = hasChildren;
        this.isGroup = isGroup;
        this.hasImage = hasImage;
        this.drawableID = drawableID;
        this.queryModel = queryModel;
    }

}
