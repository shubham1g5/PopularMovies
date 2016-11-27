package com.example.shubham.popularmovies;

import com.example.shubham.popularmovies.model.MovieResults;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by shubham on 27/11/16.
 */

public interface MovieService {

    @GET("{sortType}")
    Observable<MovieResults> listMovies(@Path("sortType") String sortType, @Query("api_key") String apiKey);
}
