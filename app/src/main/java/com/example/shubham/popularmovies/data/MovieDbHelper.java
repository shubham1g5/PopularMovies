package com.example.shubham.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.shubham.popularmovies.data.MovieContract.MovieEntry;

class MovieDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COL_ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COL_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COL_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COL_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COL_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COL_VOTE_AVERAGE + " REAL NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Won't be called as we are on version 1.
    }
}
