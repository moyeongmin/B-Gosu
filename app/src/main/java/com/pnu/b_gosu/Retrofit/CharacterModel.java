package com.pnu.b_gosu.Retrofit;

import com.google.gson.annotations.SerializedName;

public class CharacterModel {

    @SerializedName("characterid")
    private int characterid;

    public int getCharacterid() {
        return characterid;
    }

    public void setCharacterid(int characterid) {
        this.characterid = characterid;
    }


}
