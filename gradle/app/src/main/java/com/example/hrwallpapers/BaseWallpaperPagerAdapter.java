package com.example.hrwallpapers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static androidx.constraintlayout.motion.MotionScene.TAG;


public class BaseWallpaperPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<wallpaperModel> wallpaperList;
    private ViewPager viewPager;
    private View lastActiveView;
    public BaseWallpaperPagerAdapter(Context context, List<wallpaperModel> wallpaperList, ViewPager viewPager)
    {
        this.wallpaperList = wallpaperList;
        this.mContext = context;
        this.viewPager = viewPager;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull final Object object) {
        final ImageView im = container.findViewById(R.id.base_wallpaper_main_image);
        final CircleProgressBar circleProgressBar = container.findViewById(R.id.base_wallpaper_circleProgressBar);

        ProgressAppGlideModule.forget(getWallpaperModel(position).originalSrc);
        Glide.with(mContext).clear(im); // OOM handler it must be in the detached!! dont delete
        circleProgressBar.setProgress(0);
        container.removeView((View)object);

    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View currentView = (View) object;
        if(currentView != null) {
            ImageView im = currentView.findViewById(R.id.base_wallpaper_main_image);
            CircleProgressBar circleProgressBar = currentView.findViewById(R.id.base_wallpaper_circleProgressBar);
            wallpaperModel model = getWallpaperModel(position);
            if (lastActiveView != object) {
                if (model != null) {
                    container.setTag("container" + position);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.priority(Priority.HIGH);
                    MainActivity.LoadImageFromURL(im, model.originalSrc, circleProgressBar, requestOptions, model);
                    Log.i(TAG, "setPrimaryItem: " + model.id + " load event is called");
                }
            }
        }
        lastActiveView = currentView;
        super.setPrimaryItem(container, position, object);
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


    public View getLastActiveView()
    {
        return this.lastActiveView;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View layout = layoutInflater.inflate(R.layout.base_wallpaper_viewpager,container,false);
        ImageView im = layout.findViewById(R.id.base_wallpaper_main_image);
        final CircleProgressBar circleProgressBar = layout.findViewById(R.id.base_wallpaper_circleProgressBar);

        final wallpaperModel model = getWallpaperModel(position);

        if(model != null)
        {
            circleProgressBar.setProgress(0);

            RequestOptions requestOptions = new RequestOptions();
            if (position == 0) requestOptions.priority(Priority.HIGH);
            else requestOptions.priority(Priority.NORMAL);
            MainActivity.LoadImageFromURL(im,model.originalSrc,circleProgressBar,requestOptions,model);
        }
        container.addView(layout);
        return layout;
    }

    public void addListToList(List<wallpaperModel> models)
    {
        this.wallpaperList.addAll(models);
    }
}
