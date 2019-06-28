package com.example.hrwallpapers;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

class HttpGetImagesAsync extends AsyncTask<Object,Integer, List<wallpaperModel>> {

    private onAsyncTaskFisinhed taskFisinhed;

    public void setTaskFisinhed(onAsyncTaskFisinhed listener) { this.taskFisinhed = listener;}

    @Override
    protected List<wallpaperModel> doInBackground(Object... objects) {

        List<wallpaperModel> response= new ArrayList<>();

        String url = (String) objects[0];
        try {

            Document doc = Jsoup.connect(url).get();
            Elements elems = doc.select("figure");

            Log.i("a", String.valueOf(elems.size()));
            for (int i = 0; i < elems.size() ; i++) {
                String id = elems.get(i).attr("data-wallpaper-id");
                String thumbUrl = String.format("https://th.wallhaven.cc/small/%s/%s.jpg",id.substring(0,2),id);
                String originalUrl = String.format("https://w.wallhaven.cc/full/%s/wallhaven-%s.jpg",id.substring(0,2),id);

                wallpaperModel m = new wallpaperModel(thumbUrl,originalUrl,id);
                if(id != "") response.add(m);
            }

            } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(List<wallpaperModel> collection) {
        taskFisinhed.taskFinished(collection);
    }


    public interface onAsyncTaskFisinhed
    {
        public void taskFinished(List<wallpaperModel> list);
    }
}
