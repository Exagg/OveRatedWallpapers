package com.example.hrwallpapers;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.Serializable;

import static android.support.constraint.motion.MotionScene.TAG;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class GlideImageLoader {

    private ImageView mImageView;
    private CircleProgressBar mProgressBar;

    public GlideImageLoader(ImageView imageView, CircleProgressBar progressBar) {
        mImageView = imageView;
        mProgressBar = progressBar;

        onConnecting();
    }

    public void load(final String url, final RequestOptions options,final wallpaperModel wallpaperModel) {
        if (url == null || options == null) return;


        //set Listener & start
        ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                if (mProgressBar != null) {
                    mProgressBar.setProgressWithAnimation((float) (100 * bytesRead / expectedLength));
                }
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });
        //Get Image
        Glide.with(mImageView.getContext())
                .load(url)
                .transition(withCrossFade())
                .apply(options.skipMemoryCache(true))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ProgressAppGlideModule.forget(url);
                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (url.equals(wallpaperModel.thumbSrc)) {
                                    wallpaperModel.thumbSrc = url.replace(".jpg", ".png");
                                    getPngImage(wallpaperModel.thumbSrc, options);

                                } else if (url.equals(wallpaperModel.originalSrc)) {
                                    wallpaperModel.originalSrc = url.replace(".jpg", ".png");
                                    getPngImage(wallpaperModel.originalSrc, options);
                                }
                            }
                        });
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(mProgressBar.getProgress() == (float)0.0)
                        {
                            mProgressBar.setProgressWithAnimation((float) 100); // trigger the loaded event when the image cames from cache
                        }
                        ProgressAppGlideModule.forget(url);
                        return false;
                    }
                })
                .into(mImageView);

    }


    private void getPngImage(final String url, RequestOptions options)
    {
        onConnecting();

        ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                if (mProgressBar != null) {
                    mProgressBar.setProgressWithAnimation((float) (100 * bytesRead / expectedLength));
                }
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });

        Glide.with(mImageView.getContext())
                .load(url)
                .transition(withCrossFade())
                .apply(options.skipMemoryCache(true))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        ProgressAppGlideModule.forget(url);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ProgressAppGlideModule.forget(url);
                        if(mProgressBar.getProgress() == (float)0.0)
                        {
                            mProgressBar.setProgressWithAnimation((float) 100); // trigger the loaded event when the image cames from cache
                        }
                        return false;
                    }
                })
                .into(mImageView);

    }

    private void onConnecting() {
        if (mProgressBar != null)
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }
}