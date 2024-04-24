package com.pnu.b_gosu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class QuizModel {
    @SerializedName("title")
    private String title;
    @SerializedName("quiz_problem")
    private String quiz_problem;
    @SerializedName("quiz_answer")
    private int quiz_answer;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuiz_problem() {
        return quiz_problem;
    }

    public void setQuiz_problem(String quiz_problem) {
        this.quiz_problem = quiz_problem;
    }

    public int getQuiz_answer() {
        return quiz_answer;
    }

    public void setQuiz_answer(int quiz_answer) {
        this.quiz_answer = quiz_answer;
    }

    @Override
    public String toString() {
        return "QuizModel{" +
                "title='" + title + '\'' +
                ", quiz_problem='" + quiz_problem + '\'' +
                ", quiz_answer=" + quiz_answer +
                '}';
    }
}
