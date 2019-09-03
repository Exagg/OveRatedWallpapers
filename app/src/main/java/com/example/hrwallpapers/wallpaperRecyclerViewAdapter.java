package com.example.hrwallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import okhttp3.OkHttpClient;

import static androidx.constraintlayout.widget.Constraints.TAG;

class wallpaperRecyclerViewAdapter extends RecyclerView.Adapter<wallpaperRecyclerViewAdapter.wallpaperViewHolder> {


    List<wallpaperModel> modelList;
    FrameLayout fragmentHolder;
    wallpaperPopupFragment popupFragment;
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

    public wallpaperRecyclerViewAdapter(List<wallpaperModel> modelList, FrameLayout fragmentHolderLayout, wallpaperPopupFragment popupFragment,View v,Context context,queryModel queryModel,RecyclerView recyclerView)
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

        loadImage(holder);
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

    public void updateAdapter(List<wallpaperModel> modelList)
    {
        this.modelList = modelList;
        this.notifyDataSetChanged();
    }

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
        loadImage(holder);
        super.onViewAttachedToWindow(holder);
    }

    private void loadImage(@NonNull wallpaperViewHolder holder)
    {
        if(popupFragment != null)
        {
            holder.setEventForModel();
        }
        MainActivity.loadImageAsLQ(holder.wallpaperImage,holder.circleProgressBar,requestOptions,holder.model);
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
        wallpaperModel model;
        CircleProgressBar circleProgressBar;
        wallpaperPopupFragment fragment;
        Context context;
        View mainView;

        private OkHttpClient okHttpClient;

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

        public wallpaperViewHolder(View itemView, CircleProgressBar circleProgressBar, wallpaperPopupFragment fragment, Context context, wallpaperModel mModel,queryModel queryModel,wallpaperRecyclerViewAdapter currentAdapter)
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
                fragment.setProgressBar(circleProgressBar);

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
            model.isFavorite.setListener(new booleanListeners.ChangeListener() {
                @Override
                public void onChange() {

                    if (model.isFavorite.isTrue())
                    {
                        MainActivity.setIconToImageView(fragment.likeImageView,context,R.string.fa_heart,true,false);
                    }
                    else
                    {
                        MainActivity.setIconToImageView(fragment.likeImageView,context,R.string.fa_heart,false,false);
                    }

                }
            });

            wallpaperImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //set on cache get screenshot for fastest set blur background
                    Bitmap bitmap = getScreenShot(mainContentView);
                    fragment.setActiveModel(model);

                    fragment.setBlurBackground(bitmap);
                    fragment.setMainView(mainView);
                    loadImage(wallpaperViewHolder.this);
                    fragment.setVisibilityOfContainers(0);
                    return false;
                }
            });

            wallpaperImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i(TAG, "onTouch: " + fragment.isResumed());
                    if(fragment.areaIsEnabled) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            fragment.setVisibilityOfContainers(event.getAction());
                        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                            fragment.setVisibilityOfContainers(event.getAction());
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            fragment.setVisibilityOfContainers(event.getAction());
                            fragment.checkIfElemGotHover(event.getRawX(),event.getRawY());
                        }
                        else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            fragment.setVisibilityOfContainers(event.getAction());
                            fragment.checkIfElemGotHover(event.getRawX(), event.getRawY());
                        }
                    }
                    else
                    {
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            fragment.checkIfElemGotHover(event.getRawX(),event.getRawY());

                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        {
                            fragment.checkIfElemGotHover(0,0);


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

            screenShot = screenShot.createScaledBitmap(screenShot,screenShot.getWidth() / 2,screenShot.getHeight()/2,false);
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
    }
}
