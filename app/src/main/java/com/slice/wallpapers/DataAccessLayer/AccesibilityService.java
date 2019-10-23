package com.slice.wallpapers.DataAccessLayer;

import android.os.AsyncTask;
import android.util.Log;

import com.slice.wallpapers.MainActivity;
import com.slice.wallpapers.wallpaperModel;

import org.jetbrains.annotations.Contract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AccesibilityService {
    private static final String TAG = "Service";

    private static int SERVICE_STATE = 0;
    private static int DOWNLOAD_SERVICE_STATE = 0;

    public static final int SERVICE_IS_UNACCESIBLE = 2;
    public static final int SERVICE_IS_ACCESIBLE = 3;
    public static final int SERVICE_IS_PENDING = 0;
    public static final int SERVICE_IS_PROCESSING = 1;

    public static final int DOWNLOAD_SERVICE_IS_UNACCESIBLE = 12;
    public static final int DOWNLOAD_SERVICE_IS_ACCESIBLE = 13;
    public static final int DOWNLOAD_SERVICE_IS_PENDING = 10 ;
    public static final int DOWNLOAD_SERVICE_IS_PROCESSING = 11;

    private static CheckDownloadService checkDownloadService = new CheckDownloadService();
    private static WallpaperServiceListener wallpaperServiceListener = new WallpaperServiceListener() {

        @Override
        public void Succesful(boolean isSuccesful) {
            if(isSuccesful) DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_ACCESIBLE;
            else DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_UNACCESIBLE;

        }

        @Override
        public void Downloading(int percentage) {
        }

        @Override
        public void Accesibility(boolean isAccesible) {
            if(isAccesible) SERVICE_STATE = SERVICE_IS_ACCESIBLE;
            else SERVICE_STATE = SERVICE_IS_UNACCESIBLE;
        }
    };

    public AccesibilityService()
    {
        SERVICE_STATE = SERVICE_IS_PENDING;
        DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_PENDING;

    }

    public static void run() {
        if (checkDownloadService == null || checkDownloadService.getStatus() == AsyncTask.Status.FINISHED) checkDownloadService = new CheckDownloadService();

        if (checkDownloadService.getStatus() != AsyncTask.Status.RUNNING)
        {

            SERVICE_STATE = SERVICE_IS_PENDING;
            DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_PENDING;

            checkDownloadService.setDownloadListener(wallpaperServiceListener);
            checkDownloadService.execute();
        }
    }

    @Contract(pure = true)
    public static int getServiceState() {
        return SERVICE_STATE;
    }

    @Contract(pure = true)
    public static int getDownloadServiceState() {
        return DOWNLOAD_SERVICE_STATE;
    }


    private static class CheckDownloadService extends AsyncTask<Object,Integer,Boolean>
    {
        private String url = "https://wallhaven.cc/";
        private WallpaperServiceListener downloadListener;

        @Override
        protected Boolean doInBackground(Object... objects) {
            DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_PROCESSING;
            SERVICE_STATE = SERVICE_IS_PROCESSING;
            try {
                Document document = Jsoup.connect(url).get();
                Elements exampleImage = document.select("div.feat-row > span:nth-child(1) > a > img");

                String thumbImageUrl = exampleImage.get(0).attr("src");
                String wallpaperID = thumbImageUrl.substring(thumbImageUrl.lastIndexOf("/") + 1).replace(".jpg","").replace(".png","");


                wallpaperModel model = new wallpaperModel(wallpaperID);

                URL jpgUrl = new URL(model.originalSrc);

                HttpsURLConnection connection = (HttpsURLConnection) jpgUrl.openConnection();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    Log.i(TAG, "doInBackground: Jpg url is available");
                    publishProgress(0);
                    SERVICE_STATE = SERVICE_IS_ACCESIBLE;
                    publishProgress(0);

                }
                else
                {
                    Log.i(TAG, "doInBackground: Jpg url is unavailable trying png");
                    model.setIsPng(true);
                    URL pngUrl = new URL(model.originalSrc);
                    connection = (HttpsURLConnection) pngUrl.openConnection();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        Log.i(TAG, "doInBackground: Png url is available.");
                        publishProgress(0);
                        SERVICE_STATE = SERVICE_IS_ACCESIBLE;
                        publishProgress(0);
                    }
                    else
                    {
                        SERVICE_STATE = SERVICE_IS_UNACCESIBLE;

                    }
                }


                String filename = model.HQFileName;
                File file = new File(MainActivity.downloadFolder, filename);
                if (file.exists()) file.delete();

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = connection.getInputStream();
                int totalSize = connection.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;

                    int percentage = (int) (((double) (10 * downloadedSize)) / totalSize);
                    percentage = percentage * 10;
                    if (percentage < 100 && percentage > 0) publishProgress(percentage);

                }
                fileOutput.close();
                if (downloadedSize == totalSize)
                {
                    model.setImageFile(file);
                    publishProgress(100);
                    file.delete(); // download is succesfull. then we delete the file.
                    DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_ACCESIBLE;
                    return true;
                }

            } catch (IOException e) {
                DOWNLOAD_SERVICE_STATE = DOWNLOAD_SERVICE_IS_UNACCESIBLE;
                SERVICE_STATE = SERVICE_IS_UNACCESIBLE;
                e.printStackTrace();
                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean o) {
            super.onPostExecute(o);

            if (downloadListener != null)
            {
                downloadListener.Succesful(o);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length > 0)
            {
                if (values[0] instanceof Integer)
                {
                    int value = (int) values[0];

                    if (downloadListener != null) downloadListener.Downloading(value);
                    if (value == 0) downloadListener.Accesibility(true);
                }
            }

        }

        public void setDownloadListener(WallpaperServiceListener downloadListener) {
            this.downloadListener = downloadListener;
        }
    }


}

interface WallpaperServiceListener
{
    void Succesful(boolean isSuccesful);
    void Downloading(int percentage);
    void Accesibility(boolean isAccesible);
}