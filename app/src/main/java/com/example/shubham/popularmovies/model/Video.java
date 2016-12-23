package com.example.shubham.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Video implements Parcelable {

    private static final String BASE_URL = "https://www.youtube.com/watch?v=";
    public static final CharSequence TYPE_TRAILER = "Trailer";

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    private final String key;
    private final String name;
    private final String type;

    private Video(@NonNull Parcel in) {
        key = in.readString();
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @NonNull
        @Override
        public Video createFromParcel(@NonNull Parcel in) {
            return new Video(in);
        }

        @NonNull
        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(type);
    }

    @NonNull
    public String getVideoUrl() {
        return BASE_URL + key;
    }
}
