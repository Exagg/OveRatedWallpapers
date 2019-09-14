package com.example.hrwallpapers;

import androidx.annotation.NonNull;

public class FolderModel {

    private int folderID;
    private String folderPath;
    private String folderName;
    public FolderModel(@NonNull int folderID,@NonNull String folderPath,@NonNull String folderName)
    {
        this.folderID = folderID;
        this.folderPath = folderPath;
        this.folderName = folderName;
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
}
