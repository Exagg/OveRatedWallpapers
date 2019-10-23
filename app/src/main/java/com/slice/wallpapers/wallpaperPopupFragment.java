package com.slice.wallpapers;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class wallpaperPopupFragment extends Fragment  {

    private BottomDownloadDialog bottomDownloadDialog;
    private static final String TAG = "WallpaperPopupFragment";
    View rootVieW;
    public ImageView fragmentBaseImageView;
    public View wallpaperBaseContainer;
    public ImageView likeImageView;
    public ImageView shareImageView;
    public ImageView downloadImageView;
    public ViewGroup fragmentHolder;
    public View mainView;

    private wallpaperModel activeModel;

    boolean areaIsEnabled = false;

    boolean isTouchAreaVisible = false;

    boolean isLikeRaised = false;
    boolean isDownloadRaised = false;
    boolean isShareRaised = false;
    boolean isShareVibrated = false;
    boolean isDownloadVibrated = false;
    boolean isLikeVibrated = false;
    private ImageView backgroundBlur;

    private RequestOptions requestOptions = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .priority(Priority.IMMEDIATE)
            .centerInside();
    private CircleProgressBar circleProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootVieW = inflater.inflate(R.layout.wallpaper_popup_fragment,container,false);

        likeImageView = rootVieW.findViewById(R.id.wallpaper_like_button);
        shareImageView = rootVieW.findViewById(R.id.wallpaper_share_button);
        downloadImageView = rootVieW.findViewById(R.id.wallpaper_download_button);
        fragmentBaseImageView = rootVieW.findViewById(R.id.wallpaper_popup_base_image);
        backgroundBlur = rootVieW.findViewById(R.id.fragment_blur_background);
        fragmentHolder = container;

        bottomDownloadDialog = new BottomDownloadDialog(getContext().getContentResolver());

        return rootVieW;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void setActiveModel(wallpaperModel activeModel) {
        this.activeModel = activeModel;


        MainActivity.loadImageAsHQ(fragmentBaseImageView,circleProgressBar,requestOptions,activeModel);
    }

    public void setMainView(View view)
    {
        this.mainView = view;
    }

    public void checkIfElemGotHover(float x, float y)
    {

        //Çok fazla ardışık kontrol var iyileştirme yapılacak.

        int[] downloadImagePos = new int[2];
        int[] shareImagePos = new int[2];
        int[] likeImagePos = new int[2];


        downloadImageView.getLocationOnScreen(downloadImagePos);
        shareImageView.getLocationOnScreen(shareImagePos);
        likeImageView.getLocationOnScreen(likeImagePos);


        if((downloadImagePos[0] < x && downloadImagePos[0] + downloadImageView.getWidth() > x) &&
                (downloadImagePos[1] < y && downloadImagePos[1] + downloadImageView.getHeight() > y))
        {
            isDownloadRaised = true;
        }
        else {
            isDownloadRaised = false;
            isDownloadVibrated = false;
        }
        if((shareImagePos[0] < x && shareImagePos[0] + shareImageView.getWidth() > x) &&
                (shareImagePos[1] < y && shareImagePos[1] + shareImageView.getHeight() > y))
        {
            isShareRaised = true;
        }
        else
        {
            isShareRaised = false;
            isShareVibrated = false;
        }
        if((likeImagePos[0] < x && likeImagePos[0] + likeImageView.getWidth() > x)&&
                (likeImagePos[1] < y && likeImagePos[1] + likeImageView.getHeight() > y))
        {
            isLikeRaised = true;
        }
        else
        {
            isLikeRaised = false;
            isLikeVibrated = false;
        }

        if (isLikeRaised|| isShareRaised||isDownloadRaised)
        {
            boolean handled = false;
            if (isLikeRaised && isLikeVibrated) handled = true; isLikeVibrated = true;
            if (isShareRaised && isShareVibrated) handled = true; isShareVibrated = true;
            if (isDownloadRaised && isDownloadVibrated) handled = true; isDownloadVibrated = true;
            if (MainActivity.vibrator.hasVibrator() && !handled)
            {
                if (Build.VERSION.SDK_INT >= 26) {
                    MainActivity.vibrator.vibrate(VibrationEffect.createOneShot(75, -1));
                } else {
                    MainActivity.vibrator.vibrate(75);
                }
            }
        }
    }


    public void setVisibilityOfContainers(int action)
    {
        if(this.getView() != null)
        {

            if (this.mainView != null)
            {
                this.mainView.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
            {
                areaIsEnabled = false;
                isTouchAreaVisible = false;

                if (fragmentHolder!= null)
                {
                    fragmentHolder.setVisibility(View.INVISIBLE);
                }
                if(isDownloadRaised || isLikeRaised || isShareRaised)
                {
                    if (isDownloadRaised)
                    {
                        bottomDownloadDialog.setDialogType(BottomDownloadDialog.BottomDownloadDialogType.DOWNLOAD);
                        bottomDownloadDialog.setActiveModel(this.activeModel);
                        bottomDownloadDialog.show(getChildFragmentManager(),"Download");
                    }
                    else if (isLikeRaised)
                    {
                        MainActivity.likeWallpaper(activeModel,likeImageView);
                    }
                    else if (isShareRaised)
                    {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.fragmentBaseImageView.getDrawable();
                        bottomDownloadDialog.setActiveBitmap(bitmapDrawable.getBitmap());
                        bottomDownloadDialog.setActiveModel(this.activeModel);
                        bottomDownloadDialog.setDialogType(BottomDownloadDialog.BottomDownloadDialogType.SHARE);
                        bottomDownloadDialog.show(getChildFragmentManager(),"Share");
                    }
                }
            }
            else if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)
            {
                areaIsEnabled = true;
                isTouchAreaVisible = true;
                if (fragmentHolder!= null)
                {
                    fragmentHolder.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void setBlurBackground(Bitmap bitmap) {
        if (this.getView() != null)
        {
            Glide.with(getContext())
                    .load(bitmap)
                    .fitCenter()
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(10,8)))
                    .into(backgroundBlur);

        }
    }

    public void setProgressBar(CircleProgressBar circleProgressBar) {
        this.circleProgressBar = circleProgressBar;
    }
}