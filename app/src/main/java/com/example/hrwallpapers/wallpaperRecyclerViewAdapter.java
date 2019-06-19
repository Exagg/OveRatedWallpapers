package com.example.hrwallpapers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;
import java.util.Timer;

import info.androidhive.fontawesome.FontDrawable;
import info.androidhive.fontawesome.FontTextView;

import static android.support.constraint.motion.MotionScene.TAG;

class wallpaperRecyclerViewAdapter extends RecyclerView.Adapter<wallpaperRecyclerViewAdapter.wallpaperViewHolder> {


    List<wallpaperModel> modelList;
    public wallpaperRecyclerViewAdapter(List<wallpaperModel> modelList)
    {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public wallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        wallpaperModel model = modelList.get(i);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallpaper_list_model,null);

        Log.i(TAG, "onCreateViewHolder: Position : " + i);
        return new wallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull wallpaperViewHolder holder, int i) {
        wallpaperModel model = modelList.get(i);
        holder.setEventForModel(model);
        MainActivity.LoadImageFromURL(holder.wallpaperImage,model.thumbSrc);
    }

    @Override
    public int getItemCount() {
        return this.modelList.size();
    }

    class wallpaperViewHolder extends  RecyclerView.ViewHolder
    {
        ImageView wallpaperImage;
        LinearLayout wallpaperIconsContainer,touchAreaContainer;
        FrameLayout wallpaperBaseContainer;
        ImageView likeImageView,tagImageView,downloadImageView;
        wallpaperModel model;
        boolean areaIsEnabled = false;

        boolean isTouchAreaVisible = false;

        boolean isLikeRaised = false;
        boolean isDownloadRaised = false;
        boolean isTagRaised= false;

        private int hoverSize = 100;
        private int standSize = 70;

        public wallpaperViewHolder(View itemView)
        {
            super(itemView);

            wallpaperBaseContainer = itemView.findViewById(R.id.wallpaper_base_container);
            wallpaperIconsContainer = itemView.findViewById(R.id.wallpaper_icons_container);
            wallpaperImage = itemView.findViewById(R.id.wallpaper_imageview);
            likeImageView = itemView.findViewById(R.id.wallpaper_like_button);
            tagImageView = itemView.findViewById(R.id.wallpaper_tags_button);
            downloadImageView = itemView.findViewById(R.id.wallpaper_download_button);

            touchAreaContainer = itemView.findViewById(R.id.wallpaper_touch_container);

            setIconToImageView(likeImageView,itemView.getContext(),R.string.fa_heart,false,false,standSize);
            setIconToImageView(tagImageView,itemView.getContext(),R.string.fa_tag_solid,true,false,standSize);
            setIconToImageView(downloadImageView,itemView.getContext(),R.string.fa_download_solid,true,false,standSize);

        }

        public void setEventForModel(wallpaperModel wallpaperModel)
        {
            model = wallpaperModel;

            model.isFavorite.setListener(new booleanListeners.ChangeListener() {
                @Override
                public void onChange() {

                    Log.i(TAG, "onChange: " + model.isFavorite.isTrue());
                    if (model.isFavorite.isTrue()) setIconToImageView(likeImageView,itemView.getContext(),R.string.fa_heart,true,false,standSize);
                    else setIconToImageView(likeImageView,itemView.getContext(),R.string.fa_heart,false,false,standSize);

                    Log.i(TAG, "wallpaperViewHolder: URL :" + model.thumbSrc);
                }
            });

            touchAreaContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i("Long Click" ,"true");
                    areaIsEnabled = true;
                    setVisibilityOfContainers(0);
                    return false;
                }
            });

            touchAreaContainer.setOnTouchListener(new View.OnTouchListener() {
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
                        if(event.getAction() == MotionEvent.ACTION_DOWN) checkIfElemGotHover(event.getRawX(),event.getRawY());
                        else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) checkIfElemGotHover(0,0);
                    }
                    return false;
                }

            });

        }

        private void setIconToImageView(ImageView imageView, Context context,int resource ,boolean isSolid,boolean isBrand,int size)
        {
            FontDrawable drawable = new FontDrawable(context,resource,isSolid,isBrand);
            drawable.setTextSize(MainActivity.setPxToDP(size,itemView.getContext()));
            imageView.setImageDrawable(drawable);
        }
        private void checkIfElemGotHover(float x,float y)
        {

            //Çok fazla ardışık kontrol var iyileştirme yapılacak.

            int[] downloadImagePos = new int[2];
            int[] tagImagePos = new int[2];
            int[] likeImagePos = new int[2];


            downloadImageView.getLocationOnScreen(downloadImagePos);
            tagImageView.getLocationOnScreen(tagImagePos);
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
            if((tagImagePos[0] < x && tagImagePos[0] + tagImageView.getWidth() > x) &&
                    (tagImagePos[1] < y && tagImagePos[1] + tagImageView.getHeight() > y))
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
                wallpaperIconsContainer.setVisibility(View.INVISIBLE);

                if(isDownloadRaised || isLikeRaised || isTagRaised)
                {
                    if (isDownloadRaised)
                    {
                        Log.i(TAG, "setVisibilityOfContainers: Download Raised");
                    }
                    else if (isLikeRaised)
                    {
                        if(model.isFavorite.isTrue())
                        {
                            model.isFavorite.setValue(false);
                            Log.i(TAG, "setVisibilityOfContainers: Unliked");
                        }
                        else
                        {
                            model.isFavorite.setValue(true);
                            Log.i(TAG, "setVisibilityOfContainers: This pic is liked");
                        }
                    }
                    else if (isTagRaised)
                    {
                        Log.i(TAG, "setVisibilityOfContainers: Tag Raised");
                    }
                }
            }
            else if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)
            {

                areaIsEnabled = true;
                isTouchAreaVisible = true;
                wallpaperIconsContainer.setVisibility(View.VISIBLE);
            }


        }
    }
}
