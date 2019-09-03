package com.example.hrwallpapers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class BottomDownloadDialog extends BottomSheetDialogFragment implements View.OnClickListener,DownloadImageAsync.onTaskFinished,CircleProgressBar.onProgressBarLoaded {

    private static final String TAG = "BottomDownloadDiaglog";
    private wallpaperModel activeModel;
    private Bitmap activeBitmap;
    private BottomDownloadDialogType dialogType;
    private DownloadImageAsync downloadImageAsync = new DownloadImageAsync();
    private CircleProgressBar circleProgressBar;
    private ContentResolver contentResolver;

    public BottomDownloadDialog(ContentResolver contentResolver)
    {
        this.contentResolver = contentResolver;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

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
        View HqView = view.findViewById(R.id.download_dialog_high_quality_container);
        View Aqview = view.findViewById(R.id.download_dialog_actual_quality_container);
        circleProgressBar = view.findViewById(R.id.download_dialog_circlebar);

        HqView.setOnClickListener(this);
        Aqview.setOnClickListener(this);

        downloadImageAsync.setTaskFisinhed(this);
        circleProgressBar.setOnLoaded(this);

        MainActivity.checkPermissions(view.getContext(),getActivity(),1);
        return  view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.download_dialog_high_quality_container:
            {
                if(this.activeModel != null && dialogType != null)
                {
                    if(!MainActivity.isFileExists(activeModel.HQFileName))
                    {
                        circleProgressBar.setVisibility(View.VISIBLE);
                        downloadWallpaper();
                    }
                    else
                    {
                        doEvent(MainActivity.findExistFie(activeModel.HQFileName));
                        dismiss();
                    }
                }
                break;
            }
            case R.id.download_dialog_actual_quality_container:
            {
                if(this.activeBitmap != null && this.activeModel != null && dialogType != null)
                {
                    File file = saveAs(activeBitmap);
                    doEvent(file);
                    dismiss();
                }
                break;
            }
        }
    }

    private void doEvent(File file)
    {
        switch (dialogType)
        {
            case SHARE:
                share(file);
                break;
            case SETASWALLPAPER:
                setAs(file);
                break;
            case DOWNLOAD:
                break;
        }
    }

    public void downloadWallpaper()
    {
        if(this.activeModel != null)
        {
            if(downloadImageAsync.getStatus() == AsyncTask.Status.FINISHED) downloadImageAsync = new DownloadImageAsync();

            if(downloadImageAsync.getStatus() != AsyncTask.Status.RUNNING)
            {
                downloadImageAsync.setTaskFisinhed(this);
                downloadImageAsync.execute(this.activeModel);
            }
        }
    }

    private void share(File file)
    {
        Uri bitmapUri = Uri.parse(file.getAbsolutePath());
        Log.i(TAG, "share: " + bitmapUri);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +getContext().getPackageName());
        shareIntent.setType("image/*");
        getActivity().startActivity(Intent.createChooser(shareIntent,"Share"));
    }

    public void share(File file,Activity activity)
    {
        Uri bitmapUri = Uri.parse(file.getAbsolutePath());
        Log.i(TAG, "share: " + bitmapUri);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +MainActivity.ma.getPackageName());
        shareIntent.setType("image/*");
        activity.startActivity(Intent.createChooser(shareIntent,"Share"));
    }

    private void setAs(File file)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String bitmapPath = MediaStore.Images.Media.insertImage(contentResolver,bitmap,file.getName(),"null");
        Uri bitmapUri =Uri.parse(bitmapPath);
        Intent setAsIntent = new Intent(Intent.ACTION_ATTACH_DATA);
        setAsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        setAsIntent.setDataAndType(bitmapUri,"image/*");
        setAsIntent.putExtra("mimeType","image/*");
        setAsIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(Intent.createChooser(setAsIntent,"Set As:"));
    }

    public void setAs(File file, Activity activity)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        String bitmapPath = MediaStore.Images.Media.insertImage(contentResolver,bitmap,file.getName(),"null");
        Uri bitmapUri =Uri.parse(bitmapPath);
        Intent setAsIntent = new Intent(Intent.ACTION_ATTACH_DATA);
        setAsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        setAsIntent.setDataAndType(bitmapUri,"image/*");
        setAsIntent.putExtra("mimeType","image/*");
        setAsIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(setAsIntent,"Set As:"));
    }

    public File saveAs(Bitmap bitmap)
    {
        File outputFolder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.DOWNLOAD_FILE_NAME);

        FileOutputStream fileOutputStream = null;

        String filename = activeModel.LQFileName;
        File file = new File(outputFolder, filename);
        if (!MainActivity.isFileExists(activeModel.HQFileName))
        {
            file = new File(outputFolder,filename);
        }
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
                MainActivity.showToast(String.format("This wallpaper is already save to %s",file.getPath()), Toast.LENGTH_SHORT,getContext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
            }

            return file;
        }

    }

    @Override
    public void Downloading(int percentage) {
        circleProgressBar.setProgressWithAnimation(percentage);
    }

    @Override
    public void Finished(String imagePath) {
        activeBitmap = BitmapFactory.decodeFile(imagePath);
        activeModel.setFilePath(imagePath);

        if(this.activeBitmap != null)
        {
            progressBarLoaded(circleProgressBar);
        }
    }

    @Override
    public void progressBarLoaded(View view) {

        if (activeBitmap != null && circleProgressBar.getProgress() >= 100f)
        {
            MainActivity.showToast("This wallpaper is downloaded to " + activeModel.getFilePath(),Toast.LENGTH_SHORT,MainActivity.ma);
            File file = new File(activeModel.getFilePath());
            doEvent(file);
            dismiss();
        }
    }

    public enum BottomDownloadDialogType
    {
        SHARE,
        SETASWALLPAPER,
        DOWNLOAD
    }
}
