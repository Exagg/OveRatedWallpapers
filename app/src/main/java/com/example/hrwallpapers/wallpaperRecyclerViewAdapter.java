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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static androidx.constraintlayout.widget.Constraints.TAG;

class wallpaperRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<wallpaperModel> modelList;
    List<Object>viewModelList = new ArrayList<>();
    FrameLayout fragmentHolder;
    wallpaperPopupFragment popupFragment;
    View mainContentView;
    CircleProgressBar progressBar;
    Context context;
    RecyclerView recyclerView;
    queryModel queryModel;
    private static final int OUTOFRANGE = 10;
    private static final int NATIVE_AD_GAP = 13;
    private boolean isLocked = false;
    private int currentViewPosition;
    private int clickedItemPosition = 0;
    private boolean hasNativeAds = true;

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
        updateViewModelList();
    }

    public void setHasNativeAds(boolean hasNativeAds) {
        this.hasNativeAds = hasNativeAds;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Object object = getModel(i);

        if (i == 12)
        {
            Log.i(TAG, "onCreateViewHolder: ");
        }

        if (object instanceof wallpaperNativeAdModel)
        {
            //Create native ad item every interval count item
            View adView = LayoutInflater.from(context).inflate(R.layout.native_ad_layout,null);
            wallpaperNativeAdHolder holder = new wallpaperNativeAdHolder(adView);
            return holder;
        }
        else if (object instanceof wallpaperModel)
        {
            //Create wallpaper item
            View itemView = LayoutInflater.from(context).inflate(R.layout.wallpaper_list_model,null);
            CircleProgressBar progressBar = itemView.findViewById(R.id.wallpaper_list_progressbar);

            wallpaperModel model = (wallpaperModel) getModel(i);
            wallpaperViewHolder holder = new wallpaperViewHolder(itemView,progressBar,popupFragment,this.context,model,this.queryModel,this);

            return holder;
        }
        else {
            return null;
        }
    }

    public Object getModel(int position)
    {
        if (this.viewModelList.size() != 0) return this.viewModelList.get(position);
        else return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof wallpaperViewHolder)
        {
            wallpaperViewHolder viewHolder = (wallpaperViewHolder) holder;
            this.currentViewPosition = i;
            if(viewHolder.indexOf == 0) viewHolder.indexOf = i;

            loadImage(viewHolder);
            Glide.with(this.context.getApplicationContext()).resumeRequests();
        }
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

    public void updateViewModelList()
    {
        this.viewModelList = new ArrayList<>();
        this.viewModelList.addAll(this.modelList);
        for (int i = 0; i<this.viewModelList.size(); i++)
        {
            if (i % NATIVE_AD_GAP == 0 && i != 0)
            {
                this.viewModelList.add(i-1,new wallpaperNativeAdModel());
            }
        }
    }

    public List<wallpaperModel> getModelList(){return this.modelList; }

    public void updateAdapter(List<wallpaperModel> modelList)
    {
        this.modelList = modelList;
        updateViewModelList();
        this.notifyDataSetChanged();
    }

    public int getClickedItemPosition() { return clickedItemPosition;}
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof wallpaperViewHolder)
        {
            Glide.with(this.context.getApplicationContext()).pauseAllRequests();
            Glide.with(this.context.getApplicationContext()).clear(((wallpaperViewHolder) holder).wallpaperImage); // OOM handler it must be in the detached!! dont delete
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeAllViewsInLayout();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof wallpaperViewHolder)
        {
            loadImage((wallpaperViewHolder) holder);
        }

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

        if (this.hasNativeAds)
        {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager)
            {
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (wallpaperRecyclerViewAdapter.this.getModel(position) instanceof wallpaperNativeAdModel) return 2;
                        else return 1;
                    }
                });
            }
        }
    }

    public void setModelList(List<wallpaperModel> list)
    {
        this.modelList = list;
        updateViewModelList();
    }

    public void addModelListToList(List<wallpaperModel> list)
    {
        int lastIndex = this.modelList.size();
        this.modelList.addAll(list);
        updateViewModelList();
        this.notifyItemRangeInserted(lastIndex,this.modelList.size() - 1);
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

    public class wallpaperNativeAdHolder extends RecyclerView.ViewHolder
    {
        NativeExpressAdView nativeExpressAdView;

        public wallpaperNativeAdHolder(@NonNull final View itemView) {
            super(itemView);

            this.nativeExpressAdView = itemView.findViewById(R.id.native_ad);
            MobileAds.initialize(MainActivity.ma,MainActivity.ma.getResources().getString(R.string.app_ad_id));

            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            this.nativeExpressAdView.loadAd(adRequest);
        }
    }
    public class wallpaperNativeAdModel
    {
        wallpaperNativeAdModel()
        {

        }
    }

}
