package com.slice.wallpapers;

import androidx.annotation.NonNull;

public class FolderModel {

    private int folderID;
    private String folderPath;
    private String folderName;
    private boolean isActive;
    public FolderModel(@NonNull int folderID,@NonNull String folderPath,@NonNull String folderName,boolean isActive)
    {
        this.folderID = folderID;
        this.folderPath = folderPath;
        this.folderName = folderName;
        this.isActive = isActive;
    }

    public int getFolderID() {
        return folderID;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public boolean isActive() {
        return isActive;
    }
}
