package com.example.shubham.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Review implements Parcelable {

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    private final String author;
    private final String content;

    private Review(@NonNull Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @NonNull
        @Override
        public Review createFromParcel(@NonNull Parcel in) {
            return new Review(in);
        }

        @NonNull
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }
}
