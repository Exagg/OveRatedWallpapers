package com.example.hrwallpapers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BottomDownloadDialog extends BottomSheetDialogFragment implements View.OnClickListener,DownloadImageAsync.onTaskFinished {

    private wallpaperModel activeModel;
    private Bitmap activeBitmap;
    private BottomDownloadDialogType dialogType;
    private DownloadImageAsync downloadImageAsync = new DownloadImageAsync();




    public void setDialogType(BottomDownloadDialogType dialogType) {
        this.dialogType = dialogType;
    }

    public void setActiveModel(wallpaperModel activeModel) {
        this.activeModel = activeModel;
    }

    public void setActiveBitmap(Bitmap activeBitmap) {
        this.activeBitmap = activeBitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_save_layout,container,false);
        if(view instanceof LinearLayout)
        {
            for (int index = 0; index< ((LinearLayout) view).getChildCount(); index++){
                View childView = ((LinearLayout) view).getChildAt(index);
                childView.setOnClickListener(this);
            }
        }
        downloadImageAsync.setTaskFisinhed(this);
        return  view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.download_dialog_high_quality_container:
            {
                if(this.activeBitmap != null && this.activeModel != null && dialogType != null)
                {
                    downloadWallpaper();
                }
                break;
            }
            case R.id.download_dialog_actual_quality_container:
            {
                if(this.activeBitmap != null && this.activeModel != null && dialogType != null)
                {
                    doEvent();
                    dismiss();
                }
                break;
            }
        }
    }

    private void doEvent()
    {
        switch (dialogType)
        {
            case SHARE:
                share(saveAs(activeBitmap));
                break;
            case SETASWALLPAPER:
                setAs(saveAs(activeBitmap));
                break;
        }
    }

    private void downloadWallpaper()
    {
        if(this.activeModel != null)
        {
            if(downloadImageAsync.getStatus() == AsyncTask.Status.FINISHED)downloadImageAsync = new DownloadImageAsync();

            if(downloadImageAsync.getStatus() != AsyncTask.Status.RUNNING)
            {
                downloadImageAsync.setTaskFisinhed(this);
                downloadImageAsync.execute(this.activeModel);
            }
        }
    }


    private void share(File file)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String bitmapPath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),bitmap,"test",null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +getContext().getPackageName());
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent,"Share"));
    }

    private void setAs(File file)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String bitmapPath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),bitmap,"test","null");
        Uri bitmapUri =Uri.parse(bitmapPath);
        Intent setAsIntent = new Intent(Intent.ACTION_ATTACH_DATA);
        setAsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        setAsIntent.setDataAndType(bitmapUri,"image/*");
        setAsIntent.putExtra("mimeType","image/*");
        setAsIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(Intent.createChooser(setAsIntent,"Set As:"));
    }

    private File saveAs(Bitmap bitmap)
    {
        File outputFolder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.DOWNLOAD_FILE_NAME);

        FileOutputStream fileOutputStream = null;

        String filename = "LQ_" + activeModel.id + (activeModel.isPng ? ".png" : ".jpg");
        File file = new File(outputFolder, filename);
        try {
            if (!file.exists()) {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                activeModel.setFilePath(file);
                MainActivity.showToast(String.format("This wallpaper is saved to %s",file.getPath()), Toast.LENGTH_SHORT,getContext());
            }
            else
            {
                if(activeModel.getFilePath() == null) activeModel.setFilePath(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }
    }

    @Override
    public void Downloading(int percentage) {
    }

    @Override
    public void Finished(String imagePath) {

        activeBitmap = BitmapFactory.decodeFile(imagePath);
        doEvent();
        dismiss();
    }

    public enum BottomDownloadDialogType
    {
        SHARE,
        SETASWALLPAPER
    }
}
