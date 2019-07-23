package com.example.hrwallpapers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageAsync extends AsyncTask<Object,Integer,String> {

    private onTaskFinished taskFisinhed;

    public void setTaskFisinhed(onTaskFinished listener) { this.taskFisinhed = listener;}

    @Override
    protected void onPostExecute(String s) {
        taskFisinhed.Finished(s);
    }

    @Override
    protected String doInBackground(Object... objects) {

        if(objects.length > 0)
        {
            if(objects[0] instanceof wallpaperModel)
            {
                wallpaperModel model = (wallpaperModel) objects[0]; // first item must be wallpapermodel
                try {
                    URL url = new URL(model.originalSrc);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    File SDCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
                    String filename="HQ_" + model.id + (model.isPng ? ".png" : ".jpg");
                    Log.i("Local filename:",""+filename);
                    File file = new File(SDCardRoot,filename);
                    if(file.createNewFile())
                    {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = connection.getInputStream();
                    int totalSize = connection.getContentLength();
                    int downloadedSize = 0;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ( (bufferLength = inputStream.read(buffer)) > 0 )
                    {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
                    }
                    fileOutput.close();
                    if(downloadedSize==totalSize) return file.getPath();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public interface onTaskFinished
    {
        public void Finished(String imagePath);
    }
}
