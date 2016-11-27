package com.example.shubham.popularmovies;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shubham on 27/11/16.
 */

public class RetrofitServiceGenerator {

    private static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/movie/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    public static <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
