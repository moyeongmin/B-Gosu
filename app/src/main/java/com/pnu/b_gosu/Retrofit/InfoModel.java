package com.pnu.b_gosu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class InfoModel {
    @SerializedName("characterid")
    private int characterid;
    @SerializedName("times")
    private String times;
    @SerializedName("success_count")
    private int success_count;
    @SerializedName("success_area")
    private String success_area;
    @SerializedName("total_score1")
    private int total_score1;
    @SerializedName("total_score2")
    private int total_score2;
    @SerializedName("total_money")
    private int total_money;
    @SerializedName("type")
    private int type;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("character_name")
    private String character_name;

    public int getCharacterid() {
        return characterid;
    }

    public void setCharacterid(int characterid) {
        this.characterid = characterid;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public int getSuccess_count() {
        return success_count;
    }

    public void setSuccess_count(int success_count) {
        this.success_count = success_count;
    }

    public String getSuccess_area() {
        return success_area;
    }

    public void setSuccess_area(String success_area) {
        this.success_area = success_area;
    }

    public int getTotal_score1() {
        return total_score1;
    }

    public void setTotal_score1(int total_score1) {
        this.total_score1 = total_score1;
    }

    public int getTotal_score2() {
        return total_score2;
    }

    public void setTotal_score2(int total_score2) {
        this.total_score2 = total_score2;
    }

    public int getTotal_money() {
        return total_money;
    }

    public void setTotal_money(int total_money) {
        this.total_money = total_money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCharacter_name() {
        return character_name;
    }

    public void setCharacter_name(String character_name) {
        this.character_name = character_name;
    }

    @Override
    public String toString() {
        return "InfoModel{" +
                "characterid=" + characterid +
                ", times='" + times + '\'' +
                ", success_count=" + success_count +
                ", success_area='" + success_area + '\'' +
                ", total_score1=" + total_score1 +
                ", total_score2=" + total_score2 +
                ", total_money=" + total_money +
                ", type=" + type +
                ", birthday='" + birthday + '\'' +
                ", character_name='" + character_name + '\'' +
                '}';
    }
}
