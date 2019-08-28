package com.example.hrwallpapers;

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

import static androidx.constraintlayout.motion.MotionScene.TAG;

class HttpGetImagesAsync extends AsyncTask<Object,Object, List<wallpaperModel>> {

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

            List<wallpaperModel> onePackageList = new ArrayList<>();

            for (int i = 0; i < elems.size() ; i++) {
                String id = elems.get(i).attr("data-wallpaper-id");
                Element figure = elems.get(i);



                wallpaperModel packageModel = new wallpaperModel(id);
                wallpaperModel m = new wallpaperModel(id);


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


    public interface onAsyncTaskFisinhed
    {
        public void taskFinished(List<wallpaperModel> list);
        public void onOneTagLoaded(List<wallpaperModel> list);
    }
}
