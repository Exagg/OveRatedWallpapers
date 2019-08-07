package com.example.hrwallpapers.ImageProcessor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ja.burhanrashid52.photoeditor.PhotoFilter;

class FilterView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = "ImageFilterView";
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private GLToolBox glToolBox = new GLToolBox();
    private TextureRenderer mTexRenderer;
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    private PhotoFilter mCurrentEffect;
    private Bitmap mSourceBitmap;
    private boolean isEffectApplied = false;

    public OnSaveBitmap mOnSaveBitmap = null;

    public FilterView(Context context) {
        super(context);
        init();
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mTexRenderer = new TextureRenderer(glToolBox);


        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    void setSourceBitmap(Bitmap sourceBitmap) {
        mSourceBitmap = sourceBitmap;
        mInitialized = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    public void setOnSaveBitmapListener(OnSaveBitmap listener)
    {
        this.mOnSaveBitmap = listener;
        requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (mCurrentEffect != PhotoFilter.NONE) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
        }
        renderResult();


        if (mOnSaveBitmap != null) {
            final Bitmap mFilterBitmap = BitmapConverter.createBitmapFromGLSurface(this, gl);
            Log.e(TAG, "onDrawFrame: " + mFilterBitmap);
            if (mOnSaveBitmap != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mOnSaveBitmap.OnBitmapReady(mFilterBitmap);
                    }
                });
            }
        }
    }

    void setFilterEffect(PhotoFilter effect) {
        mCurrentEffect = effect;
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        if (mSourceBitmap != null) {
            mImageWidth = mSourceBitmap.getWidth();
            mImageHeight = mSourceBitmap.getHeight();
            mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

            // Upload to texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mSourceBitmap, 0);

            // Set texture parameters
            mTexRenderer.glToolBox.initTexParams();
        }
    }

    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }

        // Initialize the correct effect based on the selected menu/action item
        switch (mCurrentEffect) {

            case AUTO_FIX:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX);
                mEffect.setParameter("scale", 0.5f);
                break;
            case BLACK_WHITE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BLACKWHITE);
                mEffect.setParameter("black", .1f);
                mEffect.setParameter("white", .7f);
                break;
            case BRIGHTNESS:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
                mEffect.setParameter("brightness", 2.0f);
                break;
            case CONTRAST:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
                mEffect.setParameter("contrast", 1.4f);
                break;
            case CROSS_PROCESS:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
                break;
            case DOCUMENTARY:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
                break;
            case DUE_TONE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DUOTONE);
                mEffect.setParameter("first_color", Color.YELLOW);
                mEffect.setParameter("second_color", Color.DKGRAY);
                break;
            case FILL_LIGHT:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
                mEffect.setParameter("strength", .8f);
                break;
            case FISH_EYE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FISHEYE);
                mEffect.setParameter("scale", .5f);
                break;
            case FLIP_HORIZONTAL:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
                mEffect.setParameter("horizontal", true);
                break;
            case FLIP_VERTICAL:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
                mEffect.setParameter("vertical", true);
                break;
            case GRAIN:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAIN);
                mEffect.setParameter("strength", 1.0f);
                break;
            case GRAY_SCALE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
                break;
            case LOMISH:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);
                break;
            case NEGATIVE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE);
                break;
            case NONE:
                break;
            case POSTERIZE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_POSTERIZE);
                break;
            case ROTATE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
                mEffect.setParameter("angle", 180);
                break;
            case SATURATE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
                mEffect.setParameter("scale", .5f);
                break;
            case SEPIA:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SEPIA);
                break;
            case SHARPEN:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN);
                break;
            case TEMPERATURE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
                mEffect.setParameter("scale", .9f);
                break;
            case TINT:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TINT);
                mEffect.setParameter("tint", Color.MAGENTA);
                break;
            case VIGNETTE:
                mEffect = effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE);
                mEffect.setParameter("scale", .5f);
                break;
        }
    }

    private void applyEffect() {
        try{
            if(!isEffectApplied)mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);

            isEffectApplied = true;
        }
        catch (Exception ex)
        {
            Log.i(TAG, "applyEffect: Error Values" + mTextures[0] + " - " + mImageWidth + " - " + mImageHeight + " - " + mTextures[1]);
            ex.printStackTrace();
        }
    }

    private void renderResult() {
        if (mCurrentEffect != PhotoFilter.NONE) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        } else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }

    public interface OnSaveBitmap
    {
        void OnBitmapReady(Bitmap bitmap);
    }

}
