package com.example.hrwallpapers;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageAsync extends AsyncTask<Object,Integer,String> {

    public static final File outputFolder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.DOWNLOAD_FILE_NAME);
    private static final int FILE_ALREADY_CREATED = 1;
    private static final int FILE_DOWNLOADING = 2;
    private static final int FILE_DOWNLOADED = 3;
    private onTaskFinished taskFisinhed;
    private String downloadedPath = "";
    private double percentage = 0;

    public void setTaskFisinhed(onTaskFinished listener) { this.taskFisinhed = listener;}

    @Override
    protected void onPostExecute(String s) {
        taskFisinhed.Finished(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(values[0] == FILE_ALREADY_CREATED)
        {
            MainActivity.showToast(String.format("This wallpaper is already created on %s.",downloadedPath),Toast.LENGTH_SHORT,MainActivity.ma);
            taskFisinhed.Downloading(100);

        }
        if(values[0] == FILE_DOWNLOADED)
        {
            MainActivity.showToast(String.format("This wallpaper is downloaded to %s.", downloadedPath),Toast.LENGTH_SHORT,MainActivity.ma);
            taskFisinhed.Downloading(100);
        }
        if(values[0] == FILE_DOWNLOADING)
        {
            taskFisinhed.Downloading((int) values[1]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Object... objects) {

        if(objects.length > 0)
        {
            if(objects[0] instanceof wallpaperModel)
            {
                wallpaperModel model = (wallpaperModel) objects[0]; // first item must be wallpapermodel
                if(!outputFolder.exists()) outputFolder.mkdirs();
                if(outputFolder.exists())
                {
                    try {
                        URL url = new URL(model.originalSrc);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setDoInput(true);
                        connection.connect();
                        String filename = "HQ_" + model.id + ".jpg";
                        Log.i("Local filename:", "" + filename);
                        File file = new File(outputFolder, filename);


                        if (!file.exists()) {
                            file.createNewFile();

                            FileOutputStream fileOutput = new FileOutputStream(file);
                            InputStream inputStream = connection.getInputStream();
                            int totalSize = connection.getContentLength();
                            int downloadedSize = 0;
                            byte[] buffer = new byte[1024];
                            int bufferLength = 0;
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                fileOutput.write(buffer, 0, bufferLength);
                                downloadedSize += bufferLength;
                                percentage = ((double) (10 * downloadedSize)) / totalSize;
                                percentage = percentage * 10;
                                publishProgress(FILE_DOWNLOADING, (int) percentage);
                            }
                            fileOutput.close();
                            if (downloadedSize == totalSize)
                            {
                                model.setFilePath(file);
                                this.downloadedPath = file.getPath();
                                publishProgress(FILE_DOWNLOADED);
                                return this.downloadedPath;

                            }
                        }
                        else
                        {
                            model.setFilePath(file);
                            this.downloadedPath = file.getPath();
                            publishProgress(FILE_ALREADY_CREATED);
                            return this.downloadedPath;
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    return null;
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
        public void Downloading(int percentage);
        public void Finished(String imagePath);
    }

}
