package com.example.hrwallpapers.ImageProcessor;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrwallpapers.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private FilterSelectedListener filterSelectedListener;
    List<Pair<String, PhotoFilter>> mFilterList = new ArrayList<>();

    public FilterAdapter(FilterSelectedListener listener)
    {
        filterSelectedListener = listener;
        setupFilters();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.imageprocess_filter_layout,viewGroup,false);
        return new FilterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder filterViewHolder, int i) {
        Pair<String,PhotoFilter> pair = getPair(i);
        filterViewHolder.textView.setText(pair.second.name().replace("-",""));
        filterViewHolder.imageView.setImageBitmap(getBitmapFromAsset(filterViewHolder.itemView.getContext(),pair.first));
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    public Pair<String,PhotoFilter> getPair(int posiiton)
    {
        return mFilterList.get(posiiton);
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public FilterViewHolder(@NonNull View view) {
            super(view);

            this.imageView = view.findViewById(R.id.filter_imageview);
            this.textView = view.findViewById(R.id.filter_textview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterSelectedListener.FilterSelected(getPair(getLayoutPosition()).second);
                }
            });
        }

    }


    private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
            return BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupFilters() {
        mFilterList.add(new Pair<>("filters/original.jpg", PhotoFilter.NONE));
        mFilterList.add(new Pair<>("filters/auto_fix.png", PhotoFilter.AUTO_FIX));
        mFilterList.add(new Pair<>("filters/brightness.png", PhotoFilter.BRIGHTNESS));
        mFilterList.add(new Pair<>("filters/contrast.png", PhotoFilter.CONTRAST));
        mFilterList.add(new Pair<>("filters/documentary.png", PhotoFilter.DOCUMENTARY));
        mFilterList.add(new Pair<>("filters/dual_tone.png", PhotoFilter.DUE_TONE));
        mFilterList.add(new Pair<>("filters/fill_light.png", PhotoFilter.FILL_LIGHT));
        mFilterList.add(new Pair<>("filters/fish_eye.png", PhotoFilter.FISH_EYE));
        mFilterList.add(new Pair<>("filters/grain.png", PhotoFilter.GRAIN));
        mFilterList.add(new Pair<>("filters/gray_scale.png", PhotoFilter.GRAY_SCALE));
        mFilterList.add(new Pair<>("filters/lomish.png", PhotoFilter.LOMISH));
        mFilterList.add(new Pair<>("filters/negative.png", PhotoFilter.NEGATIVE));
        mFilterList.add(new Pair<>("filters/posterize.png", PhotoFilter.POSTERIZE));
        mFilterList.add(new Pair<>("filters/saturate.png", PhotoFilter.SATURATE));
        mFilterList.add(new Pair<>("filters/sepia.png", PhotoFilter.SEPIA));
        mFilterList.add(new Pair<>("filters/sharpen.png", PhotoFilter.SHARPEN));
        mFilterList.add(new Pair<>("filters/temprature.png", PhotoFilter.TEMPERATURE));
        mFilterList.add(new Pair<>("filters/tint.png", PhotoFilter.TINT));
        mFilterList.add(new Pair<>("filters/vignette.png", PhotoFilter.VIGNETTE));
        mFilterList.add(new Pair<>("filters/cross_process.png", PhotoFilter.CROSS_PROCESS));
        mFilterList.add(new Pair<>("filters/b_n_w.png", PhotoFilter.BLACK_WHITE));
        mFilterList.add(new Pair<>("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL));
        mFilterList.add(new Pair<>("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL));
        mFilterList.add(new Pair<>("filters/rotate.png", PhotoFilter.ROTATE));
    }
}
