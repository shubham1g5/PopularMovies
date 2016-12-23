package com.example.shubham.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Movie implements Parcelable {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185";

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Video> getVideos() {
        return videos;
    }

    private String posterPath;
    private String overview;
    private String releaseDate;
    private String title;
    private int id;
    private int voteCount;
    private double voteAverage;
    private List<Review> reviews;
    private List<Video> videos;

    /**
     * @param id
     * @param title
     * @param overview
     * @param posterPath
     * @param releaseDate
     * @param voteCount
     * @param voteAverage
     */
    public Movie(int id,
                 String title,
                 String overview,
                 String posterPath,
                 String releaseDate,
                 int voteCount,
                 double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.reviews = new ArrayList<>();
        this.videos = new ArrayList<>();
    }

    private Movie(){
        reviews = new ArrayList<>();
        videos = new ArrayList<>();
    }

    private Movie(@NonNull Parcel in) {
        this();

        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        id = in.readInt();
        voteCount = in.readInt();
        voteAverage = in.readDouble();
        in.readTypedList(reviews, Review.CREATOR);
        in.readTypedList(videos, Video.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeInt(voteCount);
        dest.writeDouble(voteAverage);
        dest.writeTypedList(reviews);
        dest.writeTypedList(videos);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @NonNull
        @Override
        public Movie createFromParcel(@NonNull Parcel source) {
            return new Movie(source);
        }

        @NonNull
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @NonNull
    public String getAbsoluteUrl() {
        return BASE_URL + posterPath;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
