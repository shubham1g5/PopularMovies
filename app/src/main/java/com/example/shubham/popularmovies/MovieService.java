package com.example.shubham.popularmovies;

import android.support.annotation.NonNull;

import com.example.shubham.popularmovies.model.MovieResults;
import com.example.shubham.popularmovies.model.ReviewResults;
import com.example.shubham.popularmovies.model.VideoResults;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


interface MovieService {

    @NonNull
    @GET("{sortType}")
    Observable<MovieResults> listMovies(@Path("sortType") String sortType, @Query("api_key") String apiKey);

    @NonNull
    @GET("{movieId}/reviews")
    Observable<ReviewResults> getReviews(@Path("movieId") int movieId, @Query("api_key") String apiKey);

    @NonNull
    @GET("{movieId}/videos")
    Observable<VideoResults> getVideos(@Path("movieId") int movieId, @Query("api_key") String apiKey);

}
