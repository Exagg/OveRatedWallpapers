package com.example.hrwallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.OkHttpClient;

import static android.support.constraint.motion.MotionScene.TAG;

class wallpaperRecyclerViewAdapter extends RecyclerView.Adapter<wallpaperRecyclerViewAdapter.wallpaperViewHolder> {


    List<wallpaperModel> modelList;
    FrameLayout fragmentHolder;
    Fragment popupFragment;
    View mainContentView;
    CircleProgressBar progressBar;
    Context context;
    RecyclerView recyclerView;
    queryModel queryModel;
    private static final int OUTOFRANGE = 10;
    private boolean isLocked = false;
    private int currentViewPosition;
    private int clickedItemPosition = 0;

    private RequestOptions requestOptions = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .priority(Priority.NORMAL)
            .centerCrop();

    public wallpaperRecyclerViewAdapter(List<wallpaperModel> modelList, FrameLayout fragmentHolderLayout, Fragment popupFragment,View v,Context context,queryModel queryModel,RecyclerView recyclerView)
    {
        this.modelList = modelList;
        this.fragmentHolder = fragmentHolderLayout;
        this.popupFragment = popupFragment;
        this.mainContentView = v;
        this.context = context;
        this.queryModel = queryModel;
        this.recyclerView = recyclerView;
        this.recyclerView.setItemViewCacheSize(0);
    }

    public int getCurrentViewPosition() {
        return currentViewPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public wallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.wallpaper_list_model,null);
        CircleProgressBar progressBar = itemView.findViewById(R.id.wallpaper_list_progressbar);

        wallpaperModel model = modelList.get(i);
        wallpaperViewHolder holder = new wallpaperViewHolder(itemView,progressBar,popupFragment,this.context,model,this.queryModel,this);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull wallpaperViewHolder holder, int i) {

        this.currentViewPosition = i;
        if(holder.indexOf == 0) holder.indexOf = i;

        if(popupFragment != null)
        {
            holder.setEventForModel();
            MainActivity.LoadImageFromURL(holder.wallpaperImage,holder.model.thumbSrc,holder.circleProgressBar,requestOptions,holder.model);
        }
        else
        {
            MainActivity.LoadImageFromURL(holder.wallpaperImage,holder.model.thumbSrc,holder.circleProgressBar,requestOptions,holder.model,context);
        }
        Glide.with(this.context.getApplicationContext()).resumeRequests();

    }


    @Override
    public int getItemCount() {
        return this.modelList.size();
    }


    public void clearModels()
    {
        if(this.recyclerView != null)
        {
            this.recyclerView.removeAllViews();
            this.recyclerView.invalidate();
            this.modelList.clear();
            this.recyclerView.setAdapter(this);
            this.notifyDataSetChanged();
            Log.i(TAG, "clearModels: models is cleared");
        }
        else
        {
            Log.i(TAG, "clearModels: models cant cleared because recyclerview is null");
        }
    }

    public List<wallpaperModel> getModelList(){return this.modelList; }

    public int getClickedItemPosition() { return clickedItemPosition;}
    @Override
    public void onViewDetachedFromWindow(@NonNull wallpaperViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Glide.with(this.context.getApplicationContext()).pauseAllRequests();
        Glide.with(this.context.getApplicationContext()).clear(holder.wallpaperImage); // OOM handler it must be in the detached!! dont delete
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeAllViewsInLayout();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull wallpaperViewHolder holder) {
        if(popupFragment != null)
        {
            holder.setEventForModel();
            MainActivity.LoadImageFromURL(holder.wallpaperImage,holder.model.thumbSrc,holder.circleProgressBar,requestOptions,holder.model);
        }
        else
        {
            MainActivity.LoadImageFromURL(holder.wallpaperImage,holder.model.thumbSrc,holder.circleProgressBar,requestOptions,holder.model,context);
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setModelList(List<wallpaperModel> list)
    {
        this.modelList = list;
    }

    public void addModelListToList(List<wallpaperModel> list)
    {
        int lastIndex = this.modelList.size();
        this.modelList.addAll(list);
        this.notifyItemRangeInserted(lastIndex,this.modelList.size() - 1);
    }

    public void addModelToList(wallpaperModel model)
    {
        this.modelList.add(model);
    }

    public class wallpaperViewHolder extends RecyclerView.ViewHolder
    {
        ImageView wallpaperImage;
        FrameLayout wallpaperBaseContainer;
        ImageView likeImageView,shareImageView,downloadImageView,fragmentBaseImageView;
        wallpaperModel model;
        CircleProgressBar circleProgressBar;
        Fragment fragment;
        Context context;
        View mainView;

        private OkHttpClient okHttpClient;

        boolean areaIsEnabled = false;

        boolean isTouchAreaVisible = false;

        boolean isLikeRaised = false;
        boolean isDownloadRaised = false;
        boolean isTagRaised= false;

        private int hoverSize = 100;
        private int standSize = 70;
        private int indexOf;
        private queryModel queryModel;
        private wallpaperRecyclerViewAdapter currentAdapter;

        private RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .priority(Priority.HIGH)
                .fitCenter();

        public wallpaperViewHolder(View itemView, CircleProgressBar circleProgressBar, Fragment fragment, Context context, wallpaperModel mModel,queryModel queryModel,wallpaperRecyclerViewAdapter currentAdapter)
        {
            super(itemView);
            this.mainView = itemView;
            this.model = mModel;
            this.queryModel =queryModel;
            this.currentAdapter = currentAdapter;
            wallpaperImage = itemView.findViewById(R.id.wallpaper_imageview);


            if(fragment != null)
            {
                this.fragment = fragment;
                wallpaperBaseContainer = itemView.findViewById(R.id.wallpaper_base_container);
                likeImageView = fragment.getView().findViewById(R.id.wallpaper_like_button);
                shareImageView = fragment.getView().findViewById(R.id.wallpaper_share_button);
                downloadImageView = fragment.getView().findViewById(R.id.wallpaper_download_button);
                fragmentBaseImageView = fragment.getView().findViewById(R.id.wallpaper_popup_base_image);


                MainActivity.setIconToImageView(shareImageView,context,R.string.fa_share_solid,true,false,standSize);
                MainActivity.setIconToImageView(downloadImageView,context,R.string.fa_download_solid,true,false,standSize);
            }
            else
            {
                circleProgressBar.setVisibility(View.INVISIBLE);
            }
            this.context = context;

            this.circleProgressBar = circleProgressBar;
            okHttpClient = new OkHttpClient();

            this.circleProgressBar.setOnLoaded(new CircleProgressBar.onProgressBarLoaded() {
                @Override
                public void progressBarLoaded(View view) {
                    wallpaperImage.setVisibility(View.VISIBLE);
                }
            });
        }

        public void setEventForModel()
        {
            setLikeImageView();

            model.isFavorite.setListener(new booleanListeners.ChangeListener() {
                @Override
                public void onChange() {

                    if (model.isFavorite.isTrue())
                    {
                        MainActivity.setIconToImageView(likeImageView,context,R.string.fa_heart,true,false,standSize);
                    }
                    else
                    {
                        MainActivity.setIconToImageView(likeImageView,context,R.string.fa_heart,false,false,standSize);
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

                    Glide.with(context)
                            .load(bitmap)
                            .fitCenter()
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(10,8)))
                            .into(backgroundBlur);



                    Glide.with(context)
                            .load(model.originalSrc)
                            .thumbnail(Glide.with(context).load(model.thumbSrc).apply(requestOptions))
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
                                //Start new full screen for the selected wallpaper
                                clickedItemPosition = modelList.indexOf(model);
                                startNewActivity();

                                Log.i(TAG, "onTouch: " + model.id);
                            }
                        }
                    }
                    return false;
                }

            });

        }

        public int indexOf()
        {
            return indexOf;
        }

        private Bitmap getScreenShot(View view)
        {

            view.setDrawingCacheEnabled(true);
            Bitmap screenShot =  Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            return screenShot;
        }

        private void startNewActivity()
        {
            MainActivity.ma.showFullScreenActivity(model,MainActivity.ma, BaseWallpaperActivity.class,modelList,queryModel);
            if(currentAdapter != null)
            {
                recyclerView.setAdapter(null);
            }
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
           if(this.fragment != null)
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
        }


        private void setLikeImageView()
        {
            if(this.fragment != null)
            {
                if(model.isFavorite.isTrue())
                {
                    MainActivity.setIconToImageView(likeImageView,context,R.string.fa_heart,true,false,standSize);
                }
                else
                {
                    MainActivity.setIconToImageView(likeImageView,context,R.string.fa_heart,false,false,standSize);
                }
            }
        }


    }
}
