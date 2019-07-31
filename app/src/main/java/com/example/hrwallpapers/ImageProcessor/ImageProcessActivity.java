package com.example.hrwallpapers.ImageProcessor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.hrwallpapers.CircleProgressBar;
import com.example.hrwallpapers.DownloadImageAsync;
import com.example.hrwallpapers.R;
import com.example.hrwallpapers.wallpaperModel;
import com.google.gson.Gson;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageProcessActivity extends AppCompatActivity
        implements FilterSelectedListener,
        ToolSelectedListener{

    private static final String TAG = "ImagePROCESS";
    private View backButton;
    private View saveButton;
    private PhotoEditor photoEditor;
    private PhotoEditorView editorView;
    private wallpaperModel activeModel;
    private Bitmap selectedBitmap;
    private DownloadImageAsync downloadImageAsync= new DownloadImageAsync();
    private CircleProgressBar loadingBar;
    private View loadingContainer;
    private Context context;

    private RecyclerView toolRecyclerView;
    private RecyclerView filterRecyclerView;

    private FilterAdapter filterAdapter = new FilterAdapter(this);
    private EditToolsAdapter toolsAdapter = new EditToolsAdapter(this);


    private boolean filterIsVisible = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_image_process);
        if(this.getIntent().hasExtra("wallpaperModel"))
        {

            String modelData = getIntent().getStringExtra("wallpaperModel");
            activeModel = new Gson().fromJson(modelData, wallpaperModel.class);
        }

        if(activeModel == null)
        {
            this.finishActivity(RESULT_CANCELED); // wallpaper model bulunamazsa activity kapatılacaktır.
        }

        backButton = this.findViewById(R.id.process_back_button);
        saveButton = this.findViewById(R.id.process_save_button);
        editorView =this.findViewById(R.id.process_editor);
        loadingBar = this.findViewById(R.id.process_loading_bar);
        loadingContainer = this.findViewById(R.id.process_loading_area);
        toolRecyclerView = this.findViewById(R.id.process_tool_recyclerview);
        filterRecyclerView = this.findViewById(R.id.process_filter_recyclerview);

        toolRecyclerView.setAdapter(toolsAdapter);
        LinearLayoutManager toolLayoutManager = new LinearLayoutManager(this);
        toolLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        toolRecyclerView.setLayoutManager(toolLayoutManager);


        filterRecyclerView.setAdapter(filterAdapter);
        LinearLayoutManager filterLayoutManager = new LinearLayoutManager(this);
        filterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterRecyclerView.setLayoutManager(filterLayoutManager);


        photoEditor = new PhotoEditor.Builder(this,editorView)
                .setPinchTextScalable(true).build();

        if(activeModel.getFilePath() != null)
        {
            selectedBitmap = BitmapFactory.decodeFile(activeModel.getFilePath());
            editorView.getSource().setImageBitmap(selectedBitmap);
            Log.i(TAG, "onCreate: This bitmap is loaded on " + activeModel.getFilePath());
        }

        else
        {
            if(downloadImageAsync.getStatus() == AsyncTask.Status.FINISHED)downloadImageAsync = new DownloadImageAsync();

            if(downloadImageAsync.getStatus() != AsyncTask.Status.RUNNING)
            {
                downloadImageAsync.setTaskFisinhed(new DownloadImageAsync.onTaskFinished() {
                    @Override
                    public void Finished(String imagePath){
                        loadingContainer.setVisibility(View.GONE);

                        selectedBitmap = BitmapFactory.decodeFile(activeModel.getFilePath());
                        editorView.getSource().setImageBitmap(selectedBitmap);
                        Log.i(TAG, "onCreate: This bitmap is loaded after downloaded to " + activeModel.getFilePath());
                    }

                    @Override
                    public void Downloading(int percentage) {
                        loadingBar.setProgressWithAnimation(percentage);

                        Log.i(TAG, "Downloading: " + percentage);
                    }
                });
                downloadImageAsync.execute(activeModel);
            }
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.RIGHT);
            slide.setDuration(200);
            slide.setInterpolator(new AccelerateDecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }

    @Override
    public void FilterSelected(PhotoFilter filter) {

    }

    @Override
    public void onToolSelected(ToolModel toolModel) {
        switch (toolModel.type)
        {
            case FILTER:
                showFilter(true);

        }
    }



    private void showFilter(boolean filterIsVisible)
    {
        this.filterIsVisible = filterIsVisible;

        if(this.filterIsVisible) filterRecyclerView.setVisibility(View.VISIBLE);
    }
}
