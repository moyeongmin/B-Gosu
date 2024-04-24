package com.pnu.b_gosu.Map;

import com.google.gson.annotations.SerializedName;

public class ReceiveModel {

    @SerializedName("title")
    private String title;

    @SerializedName("top10")
    private int top10;

    @SerializedName("auth")
    private int auth;

    @SerializedName("title_en")
    private String title_en;

    @SerializedName("addr1")
    private String addr1;

    @SerializedName("addr2")
    private String addr2;

    @SerializedName("contenttypeid")
    private String contenttypeid;

    @SerializedName("firstimage")
    private String firstimage;

    @SerializedName("firstimage2")
    private String firstimage2;

    @SerializedName("mapx")
    private String mapx;

    @SerializedName("mapy")
    private String mapy;

    @SerializedName("sigungucode")
    private String sigungucode;

    @SerializedName("tel")
    private String tel;

    @SerializedName("contants")
    private String contants;

    @SerializedName("tag1")
    private String tag1;

    @SerializedName("tag2")
    private String tag2;

    @SerializedName("tag3")
    private String tag3;

    @SerializedName("tag4")
    private String tag4;

    @SerializedName("tag5")
    private String tag5;

    @SerializedName("menu")
    private String menu;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }


    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getContenttypeid() {
        return contenttypeid;
    }

    public void setContenttypeid(String contenttypeid) {
        this.contenttypeid = contenttypeid;
    }

    public String getFirstimage() {
        return firstimage;
    }

    public void setFirstimage(String firstimage) {
        this.firstimage = firstimage;
    }

    public String getFirstimage2() {
        return firstimage2;
    }

    public void setFirstimage2(String firstimage2) {
        this.firstimage2 = firstimage2;
    }

    public String getMapx() {
        return mapx;
    }

    public void setMapx(String mapx) {
        this.mapx = mapx;
    }

    public String getMapy() {
        return mapy;
    }

    public void setMapy(String mapy) {
        this.mapy = mapy;
    }

    public String getSigungucode() {
        return sigungucode;
    }

    public void setSigungucode(String sigungucode) {
        this.sigungucode = sigungucode;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getContants() {
        return contants;
    }

    public void setContants(String contants) {
        this.contants = contants;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getTag4() {
        return tag4;
    }

    public void setTag4(String tag4) {
        this.tag4 = tag4;
    }

    public String getTag5() {
        return tag5;
    }

    public void setTag5(String tag5) {
        this.tag5 = tag5;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public int getTop10() {
        return top10;
    }

    public void setTop10(int top10) {
        this.top10 = top10;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "ResModel{" +
                "title='" + title + '\'' +
                ", title_en='" + title_en + '\'' +
                ", top10='" + top10 + '\'' +
                ", auth='" + auth + '\'' +
                ", addr1='" + addr1 + '\'' +
                ", addr2='" + addr2 + '\'' +
                ", contenttypeid='" + contenttypeid + '\'' +
                ", firstimage='" + firstimage + '\'' +
                ", firstimage2='" + firstimage2 + '\'' +
                ", mapx='" + mapx + '\'' +
                ", mapy='" + mapy + '\'' +
                ", sigungucode='" + sigungucode + '\'' +
                ", tel='" + tel + '\'' +
                ", contants='" + contants + '\'' +
                ", tag1='" + tag1 + '\'' +
                ", tag2='" + tag2 + '\'' +
                ", tag3='" + tag3 + '\'' +
                ", tag4='" + tag4 + '\'' +
                ", tag5='" + tag5 + '\'' +
                ", menu='" + menu + '\'' +
                ", top10='" + top10 + '\'' +
                '}';
    }
}
