package com.example.hrwallpapers;

public class capturedImages {

    public String name;
    public String imageID;

    capturedImages(String name,String imageID)
    {
        this.name = name;
        this.imageID = imageID;
    }
    public String getName()
    {
        return name;
    }
    public String getImageID()
    {
        return imageID;
    }
    public void setImageID(String imageID)
    {
        this.imageID = imageID;
    }
    public void setName (String name)
    {
        this.name = name;
    }
}
