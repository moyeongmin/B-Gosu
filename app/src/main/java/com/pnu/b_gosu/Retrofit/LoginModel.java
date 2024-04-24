package com.pnu.b_gosu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("")
    private int result_code;

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }
}
