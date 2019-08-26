package com.example.hrwallpapers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;

class HttpGetTagsAsync extends AsyncTask<Object,Integer, wallpaperModel> {

    private onAsyncTaskFisinhed taskFisinhed;

    public void setTaskFisinhed(onAsyncTaskFisinhed listener) { this.taskFisinhed = listener;}

    @Override
    protected wallpaperModel doInBackground(Object... objects) {

        if(objects.length > 0)
        {
            wallpaperModel model = (wallpaperModel) objects[0];
            try {
                String url = "https://wallhaven.cc/w/" + model.id;
                Document doc = Jsoup.connect(url).get();
                Elements elems = doc.getElementsByClass("tagname");

                for (int i = 0; i < elems.size() ; i++) {
                    String tag = elems.get(i).text();
                    model.tagList.add(tag);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return model;
        }
        else return null;
    }

    @Override
    protected void onPostExecute(wallpaperModel model) {
        taskFisinhed.taskFinished(model);
    }


    public interface onAsyncTaskFisinhed
    {
        public void taskFinished(wallpaperModel model);
    }
}
