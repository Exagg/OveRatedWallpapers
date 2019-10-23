package com.slice.wallpapers;

import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenModel {
    ImageView icon;
    TextView detailTextview;
    TextView headerTextView;
    CustomRotateAnimation customRotateAnimation;
    SplashScreenTypes splashScreenTypes;


    public SplashScreenModel(TextView headerTextView,ImageView icon, TextView detailTextview,SplashScreenTypes splashScreenTypes)
    {
        this.icon = icon;
        this.detailTextview = detailTextview;
        this.headerTextView = headerTextView;
        this.splashScreenTypes = splashScreenTypes;
        this.customRotateAnimation = new CustomRotateAnimation(this.icon,100,false,0,0,false);
    }


    public ImageView getIcon() {
        return icon;
    }

    public CustomRotateAnimation getCustomRotateAnimation() {
        return customRotateAnimation;
    }

    public TextView getDetailTextview() {
        return detailTextview;
    }

    public SplashScreenTypes getSplashScreenTypes() {
        return splashScreenTypes;
    }

    public TextView getHeaderTextView() {
        return headerTextView;
    }

    public enum SplashScreenTypes
    {
        SERVER_STATE,DOWNLOAD_STATE
    }

}
