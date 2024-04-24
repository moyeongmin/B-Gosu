package com.pnu.b_gosu.Retrofit;

public class Documents
{

    private String collection;
    private String datetime;
    private String display_sitename;
    private String doc_url;
    private int height;
    private String image_url;
    private String thumbnail_url;
    private int width;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDisplay_sitename() {
        return display_sitename;
    }

    public void setDisplay_sitename(String display_sitename) {
        this.display_sitename = display_sitename;
    }

    public String getDoc_url() {
        return doc_url;
    }

    public void setDoc_url(String doc_url) {
        this.doc_url = doc_url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Documents{" +
                "collection='" + collection + '\'' +
                ", datetime='" + datetime + '\'' +
                ", display_sitename='" + display_sitename + '\'' +
                ", doc_url='" + doc_url + '\'' +
                ", height=" + height +
                ", image_url='" + image_url + '\'' +
                ", thumbnail_url='" + thumbnail_url + '\'' +
                ", width=" + width +
                '}';
    }
}
