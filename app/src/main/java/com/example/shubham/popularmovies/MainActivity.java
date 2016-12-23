package com.example.shubham.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.shubham.popularmovies.model.Movie;

import static com.example.shubham.popularmovies.MovieListFragment.EXTRA_MOVIE_PARCELABLE;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.OnMovieSelectedCallback {

    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTwoPane = getResources().getBoolean(R.bool.has_two_panes);
        PicassoSingleton.getPicasso(this);
    }


    @Override
    public void onMovieSelected(Movie movie, int position) {
        MovieListFragment movieListFragment = ((MovieListFragment) getFragmentManager().findFragmentById(R.id.fragment_movie_list));
        movieListFragment.setmPosition(position);
        if (mTwoPane) {
            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.fragment_movie_detail);
            if (detailFragment != null) {
                detailFragment.displayMovie(movie);
            }
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(EXTRA_MOVIE_PARCELABLE, movie);
            startActivity(intent);
        }
    }
}
