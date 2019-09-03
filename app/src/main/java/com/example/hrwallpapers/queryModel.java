package com.example.hrwallpapers;

public class queryModel {

    private boolean general = false;
    private boolean anime = false;
    private boolean people = false;
    private boolean sfw = false;
    private boolean nsfw = false;
    private boolean sketchy = false;
    private boolean nsf = false;
    private int resolutionW = 0;
    private int resolutionH = 0;
    private int ratioX = 0;
    private int ratioY = 0;
    private int id = 0;
    private int activePage = 0;
    private String colorHex = "";
    private String orderBy = "";
    private String query = "";
    private String sorting = "";
    private String url = "";
    private String topRange = "";


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
    public queryModel()
    {

    }


    public void prepareUrl()
    {
        try
        {
            if (activePage == 0) activePage = 1;
            this.url = "search?";
            String categories ="";
            String purity = "";
            String resolutions = "";
            String ratios = "";
            categories += this.isGeneral() ? "1" : "0";
            categories += this.isAnime() ? "1" : "0";
            categories += this.isPeople() ? "1" : "0";


            purity +=this.isSfw() ? "1" : "0";
            purity +=this.isSketchy() ? "0" : "0";
            purity +=this.isNsfw() ? "1" : "1";


            if(this.getResolutionH() != 0 && this.getResolutionW() != 0) resolutions = this.getResolutionW() + "x" + this.getResolutionH();
            if(this.getRatioY() != 0 & this.getRatioX() != 0) ratios = this.getRatioX() + "x" + this.getRatioY();


            this.url += this.getQuery().length() > 0 ? "q=" + this.getQuery().replace(" ","%20") + "&" : "";
            this.url +=categories.length() > 0 ? "categories=" + categories + "&" : "";
            this.url +=purity.length() > 0 ? "purity=" + purity + "&" : "";
            this.url +=resolutions.length() > 0 ? "resolutions=" + resolutions + "&" : "";
            this.url +=ratios.length() > 0 ? "ratios=" + ratios + "&" : "";
            this.url +=this.getTopRange().length() > 0 ? "topRange=" + topRange + "&" : "";
            this.url +=this.getSorting().length() > 0 ? "sorting=" + this.getSorting() + "&" : "";
            this.url +=this.getOrderBy().length() > 0 ? "order=" + this.getOrderBy() + "&" : "";
            this.url +=this.getColorHex().length() > 0 ? "colors=" + this.getColorHex() + "&" : "";
            this.url +="page=" + this.getActivePage();
            this.url = "https://wallhaven.cc/" + this.url;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public int getActivePage() {
        return activePage;
    }

    public void setActivePage(int activePage) {
        this.activePage = activePage;
    }

    public String getSorting() {
        return sorting != null ? sorting : "";
    }

    public void setSorting(String sorting) {
        this.sorting = sorting.toLowerCase();
    }

    public String getUrl() {
        prepareUrl();
        return url!= null ? url: "";
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
        return colorHex!= null ? colorHex: "";
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getOrderBy() {
        return orderBy != null ? orderBy: "";
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getQuery() {
        return query != null ? query: "";
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTopRange() {
        return topRange != null ? topRange: "";
    }
    public void setTopRange(String topRange)
    {
        this.topRange = topRange;
    }
}