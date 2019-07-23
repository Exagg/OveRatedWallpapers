package com.example.hrwallpapers;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.constraint.motion.MotionScene.TAG;

class HttpGetImagesAsync extends AsyncTask<Object,Integer, List<wallpaperModel>> {

    private onAsyncTaskFisinhed taskFisinhed;

    public void setTaskFisinhed(onAsyncTaskFisinhed listener) { this.taskFisinhed = listener;}

    @Override
    protected List<wallpaperModel> doInBackground(Object... objects) {

        List<wallpaperModel> response= new ArrayList<>();

        if(objects[0].getClass() == String.class)
        {
            String url = (String) objects[0];
            loadImage(url,response);
        }
        else if (objects.length > 1)
        {
            if(objects[0].getClass() == ArrayList.class && objects[1].getClass()== wallpaperModel.class)
            {
            List<String> tagList = (List<String>) objects[0];
            wallpaperModel model = (wallpaperModel) objects[1];
            model.tagsCurrentPage++;
            if(objects.length > 1)
                for (String s : tagList)
                {
                    queryModel queryModel = model.getTagQueryModel(tagList.indexOf(s));
                    response = loadImage(queryModel.getUrl(),response);
                }
            }
        }

        return response;
    }

    private List<wallpaperModel> loadImage(String url,List<wallpaperModel> response)
    {
        try {
            Log.i(TAG, "loadImage: " + url);
            Document doc = Jsoup.connect(url).get();
            Elements elems = doc.select("figure");

            for (int i = 0; i < elems.size() ; i++) {
                String id = elems.get(i).attr("data-wallpaper-id");
                Element figure = elems.get(i);

                String thumbUrl = String.format("https://th.wallhaven.cc/small/%s/%s.jpg",id.substring(0,2),id);
                String originalUrl = String.format("https://w.wallhaven.cc/full/%s/wallhaven-%s.jpg",id.substring(0,2),id);



                wallpaperModel m = new wallpaperModel(thumbUrl,originalUrl,id);


                Elements resElement = doc.getElementsByClass("wall-res");
                Elements pngElement = figure.getElementsByClass("png");
                if(resElement.size() > 0)
                {
                    resElement.get(0);
                    m.resolution = resElement.get(i).text();
                    String width = m.resolution.substring(0,m.resolution.indexOf("x")).replace("x","").trim();
                    String height = m.resolution.substring(m.resolution.indexOf("x")).replace("x","").trim();
                    m.originalWidth = Integer.parseInt(width);
                    m.originalHeight = Integer.parseInt(height);


                }
                if(pngElement.size() > 0)
                {
                    m.originalSrc = m.originalSrc.replace(".jpg",".png");
                    m.isPng = true;
                }

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
