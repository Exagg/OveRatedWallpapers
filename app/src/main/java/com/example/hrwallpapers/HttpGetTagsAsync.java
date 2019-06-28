package com.example.hrwallpapers;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

class HttpGetTagsAsync extends AsyncTask<Object,Integer, wallpaperModel> {

    private onAsyncTaskFisinhed taskFisinhed;

    public void setTaskFisinhed(onAsyncTaskFisinhed listener) { this.taskFisinhed = listener;}

    @Override
    protected wallpaperModel doInBackground(Object... objects) {

        List<wallpaperModel> response= new ArrayList<>();

        if(objects.length > 0)
        {
            wallpaperModel model = (wallpaperModel) objects[0];
            try {
                String url = "https://wallhaven.cc/w/" + model.id;
                Document doc = Jsoup.connect(url).get();
                Elements elems = doc.getElementsByClass("tagname");

                Log.i("a", String.valueOf(elems.size()));
                for (int i = 0; i < elems.size() ; i++) {
                    String tag = elems.get(i).text();

                    model.tagList.add(tag);

                }

                Elements resolutionElem = doc.getElementsByClass("showcase-resolution");
                if(resolutionElem.size() > 0)
                {
                    String resolution = resolutionElem.get(0).text();
                    model.resolution = resolution;
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
