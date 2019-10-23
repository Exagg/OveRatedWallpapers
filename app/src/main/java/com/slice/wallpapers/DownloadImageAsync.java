package com.slice.wallpapers;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DownloadImageAsync extends AsyncTask<Object,Integer,String> {

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
            taskFisinhed.Downloading(100);

        }
        if(values[0] == FILE_DOWNLOADED)
        {
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
                if(!MainActivity.downloadFolder.exists()) MainActivity.downloadFolder.mkdirs();
                try {
                    URL url = new URL(model.originalSrc);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    TrustManager[] trustAllCerts = new TrustManager[]{
                            new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                    return myTrustedAnchors;
                                }

                                @Override
                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                @Override
                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };

                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new SecureRandom());
                    connection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    connection.setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    connection.connect();
                    String filename = model.HQFileName;
                    File file = new File(MainActivity.downloadFolder, filename);

                    if (!file.exists()) {

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
                            model.setImageFile(file);
                            this.downloadedPath = file.getPath();
                            publishProgress(FILE_DOWNLOADED);
                            return this.downloadedPath;

                        }
                    }
                    else
                    {
                        model.setImageFile(file);
                        this.downloadedPath = file.getPath();
                        publishProgress(FILE_ALREADY_CREATED);
                        return this.downloadedPath;
                    }

                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("MalformatUrlException in downloadImageAsync task. Message :" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("IO Exception in downloadImageAsync task. Message :" +e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                throw new ClassCastException("Object only contains wallpaperModel");
            }
        }
        else
        {
            throw new IndexOutOfBoundsException("Objects length must be greater than 0");
        }
        return "";
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
