package com.pnu.b_gosu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class TipModel {
    @SerializedName("count")
    private int count;

    @SerializedName("title")
    private String title;

    @SerializedName("daily_quiz")
    private String daily_quiz;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDaily_quiz() {
        return daily_quiz;
    }

    public void setDaily_quiz(String daily_quiz) {
        this.daily_quiz = daily_quiz;
    }

    @Override
    public String toString() {
        return "TipModel{" +
                "count=" + count +
                ", title='" + title + '\'' +
                ", daily_quiz='" + daily_quiz + '\'' +
                '}';
    }
}
