package com.example.shubham.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.shubham.popularmovies.app";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COL_ID = "id";
        public static final String COL_TITLE = "title";
        public static final String COL_POSTER_PATH = "poster_path";
        public static final String COL_OVERVIEW = "overview";
        public static final String COL_RELEASE_DATE = "release_date";
        public static final String COL_VOTE_COUNT = "vote_count";
        public static final String COL_VOTE_AVERAGE = "vote_average";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdfromUri(@NonNull Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
