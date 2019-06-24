package com.example.hrwallpapers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class BaseWallpaperPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<wallpaperModel> wallpaperList;
    public BaseWallpaperPagerAdapter(Context context, List<wallpaperModel> wallpaperList)
    {
        this.wallpaperList = wallpaperList;
        this.mContext = context;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        wallpaperModel model = (wallpaperModel)wallpaperList.get(position);
        return super.getPageTitle(position);
    }


    public wallpaperModel getWallpaperModel(int position)
    {
        wallpaperModel model = (wallpaperModel)wallpaperList.get(position);
        return model;
    }

    @Override
    public int getCount() {
        return wallpaperList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View layout = layoutInflater.inflate(R.layout.base_wallpaper_viewpager,container,false);
        ImageView im = layout.findViewById(R.id.base_wallpaper_main_image);
        CircleProgressBar circleProgressBar = layout.findViewById(R.id.base_wallpaper_circleProgressBar);

        wallpaperModel model = getWallpaperModel(position);

        if(model != null)
        {
            MainActivity.LoadImageFromURL(im,model.originalSrc,circleProgressBar,new RequestOptions(),model);
        }

        container.addView(layout);
        return layout;
    }
}
