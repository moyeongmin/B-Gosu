package com.pnu.b_gosu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class SearchModel {
    @SerializedName("collection")
    private String collection;
    @SerializedName("thumbnail_url")
    private String thumbnail_url;
    @SerializedName("image_url")
    private String image_url;
    @SerializedName("width")
    private Integer width;
    @SerializedName("height")
    private Integer height;
    @SerializedName("display_sitename")
    private String display_sitename;
    @SerializedName("doc_url")
    private String doc_url;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
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

    @Override
    public String toString() {
        return "SearchModel{" +
                "collection='" + collection + '\'' +
                ", thumbnail_url='" + thumbnail_url + '\'' +
                ", image_url='" + image_url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", display_sitename='" + display_sitename + '\'' +
                ", doc_url='" + doc_url + '\'' +
                '}';
    }
}
