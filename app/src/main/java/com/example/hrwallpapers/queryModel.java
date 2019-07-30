package com.example.hrwallpapers;

import android.util.Log;

import static android.support.constraint.motion.MotionScene.TAG;

public class queryModel {

    private boolean general,anime,people,sfw,sketchy,nsfw;
    private int resolutionW,resolutionH,ratioX,ratioY,id,activePage;
    private String colorHex,orderBy,query,sorting,url,topRange;


    public queryModel(boolean general, boolean anime, boolean people, boolean sfw, boolean sketchy, boolean nsfw,
                      int resolutionW, int resolutionH, int ratioX, int ratioY, int id,
                      String colorHex, String orderBy, String query, String sorting,String topRange)
    {
        this.general = general;
        this.anime = anime;
        this.people = people;
        this.sfw = sfw;
        this.nsfw = nsfw;
        this.sketchy = sketchy;
        this.resolutionH = resolutionH;
        this.resolutionW = resolutionW;
        this.ratioX = ratioX;
        this.ratioY = ratioY;
        this.colorHex = colorHex;
        this.orderBy = orderBy;
        this.sorting = sorting;
        this.activePage = 1;
        this.query = query;
        this.topRange = topRange;

        prepareUrl();
    }


    public void prepareUrl()
    {
        if (activePage == 0) activePage = 1;
        this.url = "search?";
        String categories ="";
        String purity = "";
        String atLeast = "";
        String ratios = "";
        categories += this.isGeneral() ? "1" : "0";
        categories += this.isAnime() ? "1" : "0";
        categories += this.isPeople() ? "1" : "0";


        purity +=this.isSfw() ? "1" : "0";
        purity +=this.isSketchy() ? "1" : "0";
        purity +=this.isNsfw() ? "1" : "0";


        if(this.getResolutionH() != 0 && this.getResolutionW() != 0) atLeast = this.getResolutionW() + "x" + this.getResolutionH();
        if(this.getRatioY() != 0 & this.getRatioX() != 0) ratios = this.getRatioX() + "x" + this.getRatioY();


        this.url +=this.getQuery() != "" ? "q=" + this.getQuery().replace(" ","%20") + "&" : "";
        this.url +=categories != "" ? "categories=" + categories + "&" : "";
        this.url +=purity != "" ? "purity=" + purity + "&" : "";
        this.url +=atLeast != "" ? "atLeast=" + atLeast + "&" : "";
        this.url +=ratios != "" ? "ratios=" + ratios + "&" : "";
        this.url +=this.topRange != null ? "topRange=" + topRange + "&" : "";
        this.url += this.getSorting() != "" ? "sorting=" + this.getSorting() + "&" : "";
        this.url += this.getOrderBy() != "" ? "order=" + this.getOrderBy() + "&" : "";
        this.url += this.getColorHex() != "" ? "colors=" + this.getColorHex() + "&" : "";
        this.url +="page=" + this.getActivePage();


        this.url = "https://wallhaven.cc/" + this.url;
    }

    public int getActivePage() {
        return activePage;
    }

    public void setActivePage(int activePage) {
        this.activePage = activePage;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public String getUrl() {
        prepareUrl();
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isGeneral() {
        return general;
    }

    public void setGeneral(boolean general) {
        this.general = general;
    }

    public boolean isAnime() {
        return anime;
    }

    public void setAnime(boolean anime) {
        this.anime = anime;
    }

    public boolean isPeople() {
        return people;
    }

    public void setPeople(boolean people) {
        this.people = people;
    }

    public boolean isSfw() {
        return sfw;
    }

    public void setSfw(boolean sfw) {
        this.sfw = sfw;
    }

    public boolean isSketchy() {
        return sketchy;
    }

    public void setSketchy(boolean sketchy) {
        this.sketchy = sketchy;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public int getResolutionW() {
        return resolutionW;
    }

    public void setResolutionW(int resolutionW) {
        this.resolutionW = resolutionW;
    }

    public int getResolutionH() {
        return resolutionH;
    }

    public void setResolutionH(int resolutionH) {
        this.resolutionH = resolutionH;
    }

    public int getRatioX() {
        return ratioX;
    }

    public void setRatioX(int ratioX) {
        this.ratioX = ratioX;
    }

    public int getRatioY() {
        return ratioY;
    }

    public void setRatioY(int ratioY) {
        this.ratioY = ratioY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}