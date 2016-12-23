package com.example.shubham.popularmovies;

import android.content.Context;

import com.squareup.picasso.Picasso;


class PicassoSingleton {


    private static Picasso picasso;

    public static Picasso getPicasso(Context context) {
        if (picasso == null) {
            picasso = Picasso.with(context);
        }
        return picasso;
    }
}
