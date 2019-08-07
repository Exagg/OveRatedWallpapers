package com.example.hrwallpapers.ImageProcessor;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrwallpapers.R;

import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.PhotoFilter;

import static android.support.constraint.motion.MotionScene.TAG;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private FilterSelectedListener filterSelectedListener;
    private RecyclerView recyclerView;
    List <PhotoFilter> mFilterList = new ArrayList<>();
    private Bitmap activeBitmap;
    public int bitmapActualHeight = 200;
    public int bitmapActualWidth = 200;

    public FilterAdapter(FilterSelectedListener listener)
    {
        filterSelectedListener = listener;
        setupFilters();
    }

    public void setActiveBitmap(Bitmap activeBitmap) {
        this.activeBitmap = activeBitmap;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.imageprocess_filter_layout,viewGroup,false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder filterViewHolder, int i) {
        PhotoFilter filter = getFilter(i);
        filterViewHolder.textView.setText(filter.name());

        if(this.activeBitmap != null && filterViewHolder.activeBitmap == null)
        {
            filterViewHolder.setActiveBitmap(this.activeBitmap,filter);
        }

        if(filterViewHolder.filteredBitmap != null) filterViewHolder.imageView.setImageBitmap(filterViewHolder.filteredBitmap);
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    public PhotoFilter getFilter(int posiiton)
    {
        return mFilterList.get(posiiton);
    }


    public class FilterViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        Bitmap activeBitmap;
        private boolean isBitmapScaled = false;
        boolean filterIsDrawed = false;

        Bitmap filteredBitmap;

        FilterView filterCreator;

        private FilterView.OnSaveBitmap bitmapListener = null;

        public FilterViewHolder(@NonNull View view) {
            super(view);
            this.bitmapListener = new FilterView.OnSaveBitmap() {
                @Override
                public void OnBitmapReady(Bitmap bitmap) {
                    Log.i(TAG, "OnBitmapReady: Filtered image converted succesfully");
                }
            };

            this.filterCreator = new FilterView(recyclerView.getContext());

            this.imageView = view.findViewById(R.id.filter_imageview);
            this.textView = view.findViewById(R.id.filter_textview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterSelectedListener.FilterSelected(getFilter(getLayoutPosition()));
                }
            });
            this.filterCreator.setOnSaveBitmapListener(this.bitmapListener);


        }

        public void setActiveBitmap(Bitmap bmp,PhotoFilter filter)
        {
            if(!isBitmapScaled)
            {
                this.activeBitmap = bmp;
                this.activeBitmap = getScaledBitmap(activeBitmap);
                filterCreator.setSourceBitmap(activeBitmap);
                filterCreator.setFilterEffect(filter);
            }
        }


        private Bitmap getScaledBitmap(Bitmap bmp)
        {
            isBitmapScaled = true;
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            float scaleWidth = ((float) bitmapActualWidth) / width;
            float scaleHeight = ((float) bitmapActualHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bmp, 0, 0, width, height, matrix, false);
            return resizedBitmap;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
    private void setupFilters() {
        mFilterList.add(PhotoFilter.NONE);
        mFilterList.add(PhotoFilter.AUTO_FIX);
        mFilterList.add(PhotoFilter.BRIGHTNESS);
        mFilterList.add(PhotoFilter.CONTRAST);
        mFilterList.add(PhotoFilter.DOCUMENTARY);
        mFilterList.add(PhotoFilter.DUE_TONE);
        mFilterList.add(PhotoFilter.FILL_LIGHT);
        mFilterList.add(PhotoFilter.FISH_EYE);
        mFilterList.add(PhotoFilter.GRAIN);
        mFilterList.add(PhotoFilter.GRAY_SCALE);
        mFilterList.add(PhotoFilter.LOMISH);
        mFilterList.add(PhotoFilter.NEGATIVE);
        mFilterList.add(PhotoFilter.POSTERIZE);
        mFilterList.add(PhotoFilter.SATURATE);
        mFilterList.add(PhotoFilter.SEPIA);
        mFilterList.add(PhotoFilter.SHARPEN);
        mFilterList.add(PhotoFilter.TEMPERATURE);
        mFilterList.add( PhotoFilter.TINT);
        mFilterList.add(PhotoFilter.VIGNETTE);
        mFilterList.add(PhotoFilter.CROSS_PROCESS);
        mFilterList.add(PhotoFilter.BLACK_WHITE);
        mFilterList.add(PhotoFilter.FLIP_HORIZONTAL);
        mFilterList.add(PhotoFilter.FLIP_VERTICAL);
        mFilterList.add(PhotoFilter.ROTATE);
    }
}
