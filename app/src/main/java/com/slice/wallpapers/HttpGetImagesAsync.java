package com.slice.wallpapers;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

class HttpGetImagesAsync extends AsyncTask<Object,Object, List<wallpaperModel>> {

    private static final String TAG = "HttpGetImages";
    private onAsyncTaskFisinhed taskFisinhed = null;

    public void setTaskFisinhed(onAsyncTaskFisinhed listener) { this.taskFisinhed = listener;}

    @Override
    protected List<wallpaperModel> doInBackground(Object... objects) {

        List<wallpaperModel> response= new ArrayList<>();

        if(objects[0].getClass() == String.class)
        {
            String url = (String) objects[0];
            loadImage(url,response);
        }
        return response;
    }

    private List<wallpaperModel> loadImage(String url,List<wallpaperModel> response)
    {
        try {
            Log.i(TAG, "loadImage: " + url);
            Document doc = Jsoup.connect(url).get();
            Elements elems = doc.select("figure");

            List<wallpaperModel> onePackageList = new ArrayList<>();

            for (int i = 0; i < elems.size() ; i++) {
                String id = elems.get(i).attr("data-wallpaper-id");
                Element figure = elems.get(i);



                wallpaperModel packageModel = new wallpaperModel(id);
                wallpaperModel m = new wallpaperModel(id);


                Elements resElement = doc.getElementsByClass("wall-res");
                String classOfElement = elems.get(i).className();
                Elements pngElement = figure.getElementsByClass("png");
                Elements favoritesElement = figure.getElementsByClass("jsAnchor overlay-anchor wall-favs");
                if(resElement.size() > 0)
                {
                    resElement.get(0);
                    m.resolution = resElement.get(i).text();
                    String width = m.resolution.substring(0,m.resolution.indexOf("x")).replace("x","").trim();
                    String height = m.resolution.substring(m.resolution.indexOf("x")).replace("x","").trim();
                    m.originalWidth = Integer.parseInt(width);
                    m.originalHeight = Integer.parseInt(height);


                }

                if (favoritesElement.size() > 0)
                {
                    int count = Integer.parseInt(favoritesElement.get(0).text());
                    m.setFavoritesCount(count);
                }

                if (classOfElement.contains("sketchy"))
                {
                    m.setIsSketchy(true);
                }

                if (MainActivity.wallpaperInFavorites.contains(id)) m.isFavorite.setValue(true);
                if(pngElement.size() > 0)
                {
                    m.setIsPng(true);
                }

                if(id != "")
                {
                    response.add(m);
                    onePackageList.add(m);
                }
            }
            publishProgress(onePackageList);


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

    @Override
    protected void onProgressUpdate(Object... values) {

        if(values.length > 0)
        {
            if(values[0] instanceof ArrayList)
            {
                List<wallpaperModel> modelList = (List<wallpaperModel>) values[0];
                taskFisinhed.onOneTagLoaded(modelList);
            }
        }
        super.onProgressUpdate(values);
    }

    public onAsyncTaskFisinhed getTaskFinished() {
        return taskFisinhed;
    }


    public interface onAsyncTaskFisinhed
    {
        public void taskFinished(List<wallpaperModel> list);
        public void onOneTagLoaded(List<wallpaperModel> list);
    }
}
