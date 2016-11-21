package com.example.shubham.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shubham on 18/11/16.
 */

public class Movie implements Parcelable{
    private static final String POSTER_PATH = "poster_path";
    private static final String PLOT = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String TITLE = "title";
    private static final String ID = "id";
    private static final String VOTES = "vote_count";
    private static final String RATING = "vote_average";


    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185";


    private static final String LOG_TAG = Movie.class.getSimpleName();

    String posterPath, plot, releaseDate, title;
    int id, votes;
    double rating;

    public Movie(JSONObject movie) {
        if (movie != JSONObject.NULL && movie.has(ID)) {
            try {
                posterPath = movie.optString(POSTER_PATH);
                plot = movie.optString(PLOT);
                releaseDate = movie.optString(RELEASE_DATE);
                title = movie.optString(TITLE);
                id = movie.getInt(ID);
                votes = movie.optInt(VOTES);
                rating = movie.optDouble(RATING);
            } catch (JSONException e) {
                Log.e(LOG_TAG,"Error ",e);
            }
        }else {
            throw new IllegalArgumentException("JSONObject parameter passed to constructor must be non NULL and must have a non NULL id field");
        }
    }

    public Movie(Parcel in){
        posterPath = in.readString();
        plot = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        id = in.readInt();
        votes = in.readInt();
        rating = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(plot);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeInt(votes);
        dest.writeDouble(rating);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getAbsoluteUrl(){
        return BASE_URL + posterPath;
    }
}
