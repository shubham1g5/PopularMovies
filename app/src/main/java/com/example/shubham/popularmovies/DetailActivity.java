package com.example.shubham.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.example.shubham.popularmovies.MainActivity.DETAILFRAGMENT_TAG;
import static com.example.shubham.popularmovies.MovieListFragment.EXTRA_MOVIE_PARCELABLE;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If should be in two-pane mode, finish to return to main activity
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            finish();
            return;
        }

        DetailFragment detailFragment = new DetailFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, detailFragment, DETAILFRAGMENT_TAG)
                .commit();
        detailFragment.setMovie(getIntent().getParcelableExtra(EXTRA_MOVIE_PARCELABLE));
    }
}