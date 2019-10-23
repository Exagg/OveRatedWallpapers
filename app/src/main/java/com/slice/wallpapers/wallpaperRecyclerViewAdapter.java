package com.slice.wallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

import static android.view.View.INVISIBLE;
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
        else return null;
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

        if (hasNativeAds)
        {
            for (int i = 0; i<this.viewModelList.size(); i++)
            {
                if (i % NATIVE_AD_GAP == 0 && i != 0)
                {
                    this.viewModelList.add(i-1,new wallpaperNativeAdModel());
                }
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
            ((wallpaperViewHolder) holder).model.loading = false;
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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                MainActivity.ma.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.loadImageAsLQ(holder.wallpaperImage,holder.circleProgressBar,requestOptions,holder.model);
                    }
                });

            }
        });
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
        FrameLayout mainView;
        View lockedView;

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
            this.mainView = (FrameLayout) itemView;
            this.model = mModel;
            this.queryModel =queryModel;
            this.currentAdapter = currentAdapter;
            wallpaperImage = itemView.findViewById(R.id.wallpaper_imageview);
            lockedView = itemView.findViewById(R.id.wallpaper_list_locked);

            if (this.model.getFavoritesCount() > wallpaperModel.LIMIT_OF_LOCK)
            {
                this.mainView.setForeground(this.mainView.getContext().getResources().getDrawable(R.drawable.frame_borders_sketchy));
                lockedView.setVisibility(View.VISIBLE);
            }
            if(fragment != null)
            {
                this.fragment = fragment;
                fragment.setProgressBar(circleProgressBar);

            }
            this.context = context;

            this.circleProgressBar = circleProgressBar;
            okHttpClient = new OkHttpClient();
        }

        public void setEventForModel()
        {
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
        UnifiedNativeAdView mUnifiedNativeAdView;
        List<ImageView> starList = new ArrayList<>();
        Button mCallToAction;
        ImageView mIcon;
        TextView mHeadLine;
        TextView mBody;
        View mItemView;


        public wallpaperNativeAdHolder(@NonNull final View itemView) {
            super(itemView);
            this.mItemView = itemView;
            itemView.setLayoutParams(new ViewGroup.LayoutParams(0,0));

            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("BA1DA98E60920E7B67AEA47F7BA77E46").build();

            mUnifiedNativeAdView = (UnifiedNativeAdView) itemView;
            AdLoader adLoader = new AdLoader.Builder(wallpaperRecyclerViewAdapter.this.context,wallpaperRecyclerViewAdapter.this.context.getResources().getString(R.string.native_ad_id))
                    .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                        @Override
                        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                            if (mItemView.getLayoutParams() instanceof RecyclerView.LayoutParams)
                            {
                                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) mItemView.getLayoutParams();
                                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            }
                            Log.i(TAG, "onUnifiedNativeAdLoaded: native ad is loaded");

                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - Body:" + unifiedNativeAd.getBody());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - HeadLine:" + unifiedNativeAd.getHeadline());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - Advertiser:" + unifiedNativeAd.getAdvertiser());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - CallToAction:" + unifiedNativeAd.getCallToAction());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - MediationAdapterClassName:" + unifiedNativeAd.getMediationAdapterClassName());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - Price:" + unifiedNativeAd.getPrice());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - Store:" + unifiedNativeAd.getStore());
                            Log.i(TAG, "onUnifiedNativeAdLoaded: Native Ad Test - StarRating:" + unifiedNativeAd.getStarRating());

                            mUnifiedNativeAdView.setHeadlineView(mUnifiedNativeAdView.findViewById(R.id.ad_headline));
                            mUnifiedNativeAdView.setBodyView(mUnifiedNativeAdView.findViewById(R.id.ad_body));
                            mUnifiedNativeAdView.setCallToActionView(mUnifiedNativeAdView.findViewById(R.id.ad_call_to_action));
                            mUnifiedNativeAdView.setIconView(mUnifiedNativeAdView.findViewById(R.id.ad_app_icon));
                            mUnifiedNativeAdView.setPriceView(mUnifiedNativeAdView.findViewById(R.id.ad_price));
                            mUnifiedNativeAdView.setStarRatingView(mUnifiedNativeAdView.findViewById(R.id.ad_stars));
                            mUnifiedNativeAdView.setStoreView(mUnifiedNativeAdView.findViewById(R.id.ad_store));
                            mUnifiedNativeAdView.setAdvertiserView(mUnifiedNativeAdView.findViewById(R.id.ad_advertiser));
                            mUnifiedNativeAdView.setMediaView(mUnifiedNativeAdView.findViewById(R.id.ad_media));
                            mUnifiedNativeAdView.setNativeAd(unifiedNativeAd);

                            // The headline is guaranteed to be in every UnifiedNativeAd.
                            ((TextView) mUnifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());

                            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                            // check before trying to display them.
                            if (unifiedNativeAd.getBody() == null) {
                                mUnifiedNativeAdView.getBodyView().setVisibility(View.INVISIBLE);
                            } else {
                                mUnifiedNativeAdView.getBodyView().setVisibility(View.VISIBLE);
                                ((TextView) mUnifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
                            }

                            if (unifiedNativeAd.getCallToAction() == null) {
                                mUnifiedNativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
                            } else {
                                mUnifiedNativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
                                ((Button) mUnifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
                            }


                            if (unifiedNativeAd.getIcon() == null) {
                                mUnifiedNativeAdView.getIconView().setVisibility(View.GONE);
                            } else {
                                ((ImageView) mUnifiedNativeAdView.getIconView()).setImageDrawable(
                                        unifiedNativeAd.getIcon().getDrawable());
                                mUnifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);
                            }


                            if (unifiedNativeAd.getPrice() == null) {
                                mUnifiedNativeAdView.getPriceView().setVisibility(View.INVISIBLE);
                            } else {
                                mUnifiedNativeAdView.getPriceView().setVisibility(View.VISIBLE);
                                ((TextView) mUnifiedNativeAdView.getPriceView()).setText(unifiedNativeAd.getPrice());
                            }

                            if (unifiedNativeAd.getStore() == null) {
                                mUnifiedNativeAdView.getStoreView().setVisibility(View.INVISIBLE);
                            } else {
                                mUnifiedNativeAdView.getStoreView().setVisibility(View.VISIBLE);
                                ((TextView) mUnifiedNativeAdView.getStoreView()).setText(unifiedNativeAd.getStore());
                            }

                            if (unifiedNativeAd.getStarRating() == null) {
                                mUnifiedNativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
                            } else {
                                ((RatingBar) mUnifiedNativeAdView.getStarRatingView())
                                        .setRating(unifiedNativeAd.getStarRating().floatValue());
                                mUnifiedNativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
                            }

                            if (unifiedNativeAd.getAdvertiser() == null) {
                                mUnifiedNativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
                            } else {
                                ((TextView) mUnifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
                                mUnifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
                            }

                            if (unifiedNativeAd.getMediaContent() != null)
                            {
                                mUnifiedNativeAdView.getMediaView().setMediaContent(unifiedNativeAd.getMediaContent());
                            }
                            else mUnifiedNativeAdView.getMediaView().setVisibility(INVISIBLE);
                            mUnifiedNativeAdView.setNativeAd(unifiedNativeAd);
                        }
                    })
                    .build();

            adLoader.loadAd(adRequest);
        }
    }
    public class wallpaperNativeAdModel
    {
        wallpaperNativeAdModel()
        {

        }
    }

}
