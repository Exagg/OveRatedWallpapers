package com.example.hrwallpapers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.RecoverySystem;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Timer;

import info.androidhive.fontawesome.FontDrawable;
import info.androidhive.fontawesome.FontTextView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import static android.support.constraint.motion.MotionScene.TAG;

class wallpaperRecyclerViewAdapter extends RecyclerView.Adapter<wallpaperRecyclerViewAdapter.wallpaperViewHolder> {


    List<wallpaperModel> modelList;
    FrameLayout fragmentHolder;
    Fragment popupFragment;
    View mainContentView;
    CircleProgressBar progressBar;
    private int currentViewPosition;

    private RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .centerCrop();

    public wallpaperRecyclerViewAdapter(List<wallpaperModel> modelList, FrameLayout fragmentHolderLayout, Fragment popupFragment,View v)
    {
        this.modelList = modelList;
        this.fragmentHolder = fragmentHolderLayout;
        this.popupFragment = popupFragment;
        mainContentView = v;
    }

    public int getCurrentViewPosition() {
        return currentViewPosition;
    }

    @NonNull
    @Override
    public wallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        wallpaperModel model = modelList.get(i);
        View itemView = LayoutInflater.from(mainContentView.getContext()).inflate(R.layout.wallpaper_list_model,null);
        CircleProgressBar progressBar = itemView.findViewById(R.id.wallpaper_list_progressbar);
        return new wallpaperViewHolder(itemView,progressBar,popupFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull wallpaperViewHolder holder, int i) {
        wallpaperModel model = modelList.get(i);
        holder.setEventForModel(model);
        this.currentViewPosition = i;
        MainActivity.LoadImageFromURL(holder.wallpaperImage,model.thumbSrc,holder.circleProgressBar,requestOptions,model);
    }

    @Override
    public int getItemCount() {
        return this.modelList.size();
    }

    public void setModelList(List<wallpaperModel> list)
    {
        this.modelList = list;
    }

    public class wallpaperViewHolder extends RecyclerView.ViewHolder
    {
        ImageView wallpaperImage;
        FrameLayout wallpaperBaseContainer;
        ImageView likeImageView,shareImageView,downloadImageView,fragmentBaseImageView;
        wallpaperModel model;
        CircleProgressBar circleProgressBar;
        Fragment fragment;

        private OkHttpClient okHttpClient;

        boolean areaIsEnabled = false;

        boolean isTouchAreaVisible = false;

        boolean isLikeRaised = false;
        boolean isDownloadRaised = false;
        boolean isTagRaised= false;

        private int hoverSize = 100;
        private int standSize = 70;

        private RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .fitCenter();

        public wallpaperViewHolder(View itemView,CircleProgressBar circleProgressBar,Fragment fragment)
        {
            super(itemView);
            this.fragment = fragment;
            wallpaperBaseContainer = itemView.findViewById(R.id.wallpaper_base_container);
            wallpaperImage = itemView.findViewById(R.id.wallpaper_imageview);
            likeImageView = fragment.getView().findViewById(R.id.wallpaper_like_button);
            shareImageView = fragment.getView().findViewById(R.id.wallpaper_share_button);
            downloadImageView = fragment.getView().findViewById(R.id.wallpaper_download_button);
            fragmentBaseImageView = fragment.getView().findViewById(R.id.wallpaper_popup_base_image);

            this.circleProgressBar = circleProgressBar;


            MainActivity.setIconToImageView(shareImageView,itemView.getContext(),R.string.fa_share_solid,true,false,standSize);
            MainActivity.setIconToImageView(downloadImageView,itemView.getContext(),R.string.fa_download_solid,true,false,standSize);

            okHttpClient = new OkHttpClient();

            this.circleProgressBar.setOnLoaded(new onProgressBarLoaded() {
                @Override
                public void progressBarLoaded(View view) {
                    Log.i(TAG, "progressBarLoaded: loaded");
                    wallpaperImage.setVisibility(View.VISIBLE);
                }
            });
        }

        public void setEventForModel(wallpaperModel wallpaperModel)
        {
            model = wallpaperModel;
            setLikeImageView();

            model.isFavorite.setListener(new booleanListeners.ChangeListener() {
                @Override
                public void onChange() {

                    if (model.isFavorite.isTrue())
                    {
                        MainActivity.setIconToImageView(likeImageView,itemView.getContext(),R.string.fa_heart,true,false,standSize);
                    }
                    else
                    {
                        MainActivity.setIconToImageView(likeImageView,itemView.getContext(),R.string.fa_heart,false,false,standSize);
                    }

                }
            });

            wallpaperImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    areaIsEnabled = true;


                    //Blur area will open. Check this images attrs(yet like)
                    setLikeImageView();

                    Log.i(TAG, "onLongClick: " + model.id + " - " + model.isFavorite.isTrue() + " - " + likeImageView.getId());
                    //set on cache get screenshot for fastest set blur background
                    Bitmap bitmap = getScreenShot(mainContentView);

                    BitmapDrawable dr = new BitmapDrawable(MainActivity.mainContentView.getResources(),bitmap);
                    ImageView backgroundBlur = fragment.getView().findViewById(R.id.fragment_blur_background);

                    Glide.with(fragment.getContext())
                            .load(bitmap)
                            .fitCenter()
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(10,8)))
                            .into(backgroundBlur);



                    Glide.with(fragment.getContext())
                            .load(model.originalSrc)
                            .thumbnail(Glide.with(fragment.getContext()).load(model.thumbSrc).apply(requestOptions))
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade(50))
                            .into(fragmentBaseImageView);

                    setVisibilityOfContainers(0);
                    return false;
                }
            });

            wallpaperImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(areaIsEnabled) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            setVisibilityOfContainers(event.getAction());
                        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                            setVisibilityOfContainers(event.getAction());
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            setVisibilityOfContainers(event.getAction());
                            checkIfElemGotHover(event.getRawX(),event.getRawY());
                        }
                        else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            setVisibilityOfContainers(event.getAction());
                            checkIfElemGotHover(event.getRawX(), event.getRawY());
                        }
                    }
                    else
                    {
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            checkIfElemGotHover(event.getRawX(),event.getRawY());

                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        {
                            checkIfElemGotHover(0,0);


                            if(event.getAction() == MotionEvent.ACTION_UP)
                            {
                                MainActivity.ma.showFullScreenActivity(model,MainActivity.ma, BaseWallpaperActivity.class); //Tek dokunuş yapıldı.
                            }
                        }
                    }
                    return false;
                }

            });

        }

        private Bitmap getScreenShot(View view)
        {

            view.setDrawingCacheEnabled(true);
            Bitmap screenShot =  Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            return screenShot;
        }


        private void checkIfElemGotHover(float x,float y)
        {

            //Çok fazla ardışık kontrol var iyileştirme yapılacak.

            int[] downloadImagePos = new int[2];
            int[] tagImagePos = new int[2];
            int[] likeImagePos = new int[2];


            downloadImageView.getLocationOnScreen(downloadImagePos);
            shareImageView.getLocationOnScreen(tagImagePos);
            likeImageView.getLocationOnScreen(likeImagePos);


            if((downloadImagePos[0] < x && downloadImagePos[0] + downloadImageView.getWidth() > x) &&
                    (downloadImagePos[1] < y && downloadImagePos[1] + downloadImageView.getHeight() > y))
            {
                isDownloadRaised = true;
            }
            else {
               if(isDownloadRaised)
               {
                isDownloadRaised = false;
               }
            }
            if((tagImagePos[0] < x && tagImagePos[0] + shareImageView.getWidth() > x) &&
                    (tagImagePos[1] < y && tagImagePos[1] + shareImageView.getHeight() > y))
            {
                isTagRaised = true;
            }
            else
            {
                if (isTagRaised)
                {
                    isTagRaised = false;
                }
            }
            if((likeImagePos[0] < x && likeImagePos[0] + likeImageView.getWidth() > x)&&
                    (likeImagePos[1] < y && likeImagePos[1] + likeImageView.getHeight() > y))
            {
                isLikeRaised = true;
            }
            else
            {
                if (isLikeRaised)
                {
                    isLikeRaised = false;
                }
            }
        }

        private void setVisibilityOfContainers(int action)
        {
            itemView.getParent().requestDisallowInterceptTouchEvent(true);
            if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
            {
                areaIsEnabled = false;
                isTouchAreaVisible = false;
                fragmentHolder.setVisibility(View.GONE);

                if(isDownloadRaised || isLikeRaised || isTagRaised)
                {
                    if (isDownloadRaised)
                    {
                    }
                    else if (isLikeRaised)
                    {
                        if(model.isFavorite.isTrue())
                        {
                            model.isFavorite.setValue(false);
                        }
                        else
                        {
                            model.isFavorite.setValue(true);
                        }
                    }
                    else if (isTagRaised)
                    {
                    }
                }
            }
            else if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)
            {

                areaIsEnabled = true;
                isTouchAreaVisible = true;
                fragmentHolder.setVisibility(View.VISIBLE);
            }


        }


        private void setLikeImageView()
        {
            if(model.isFavorite.isTrue())
            {
                MainActivity.setIconToImageView(likeImageView,mainContentView.getContext(),R.string.fa_heart,true,false,standSize);
            }
            else
            {
                MainActivity.setIconToImageView(likeImageView,mainContentView.getContext(),R.string.fa_heart,false,false,standSize);
            }
        }
    }
}
