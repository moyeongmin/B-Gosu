package com.pnu.b_gosu;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.pnu.b_gosu.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog(@NonNull Context context) {

        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading);
        ImageView loadinggif = findViewById(R.id.loadingframe);
        Glide.with(context).load(R.raw.loading).into(loadinggif);


    }
}
