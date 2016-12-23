package com.example.shubham.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.shubham.popularmovies.data.MovieContract;
import com.example.shubham.popularmovies.model.Movie;
import com.example.shubham.popularmovies.model.Review;
import com.example.shubham.popularmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MenuItem mFavoriteItem;
    private Movie mMovie;

    private static final int MOVIE_LOADER = 0;
    private boolean mFavorite;
    private boolean mTwoPane;
    private RecyclerView mMovieDetailRecyclerView;
    private android.support.v7.widget.ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mMovieDetailRecyclerView = (RecyclerView) view.findViewById(R.id.moviedetail_recyclerview);
        mMovieDetailRecyclerView.setHasFixedSize(true);
        mMovieDetailRecyclerView.setAdapter(null);
        mMovieDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTwoPane = getResources().getBoolean(R.bool.has_two_panes);

        fetchMovieDetails();
        return view;
    }

    private void setAdapter() {
        if (mMovie != null) {
            MovieDetailAdapter mMovieDetailAdapter = new MovieDetailAdapter(getActivity(), mMovie);
            mMovieDetailRecyclerView.setAdapter(mMovieDetailAdapter);

            if (mMovie.getVideos().size() > 0) {
                setShareIntent(createShareVideoIntent());
            }
        }
    }

    @NonNull
    private Intent createShareVideoIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getVideos().get(0).getVideoUrl());
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mMovie != null) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        // IsVisible check is necessary as otherwise when going from detail to master flow,
        // detailFragment is still alive in background and causes the detail menu to show up
        // in one pane layout of MainActivity

        if (isVisible()) {
            inflater.inflate(R.menu.detail, menu);
        }
        mFavoriteItem = menu.findItem(R.id.action_favorite);
        updateFavoriteMenu();

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                mFavorite = !mFavorite;
                updateFavoriteMenu();
                updateDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFavoriteMenu() {
        if (mFavoriteItem != null) {
            if (mFavorite) {
                mFavoriteItem.setIcon(R.drawable.ic_favorite);
                mFavoriteItem.setTitle(R.string.action_not_favorite);
            } else {
                mFavoriteItem.setIcon(R.drawable.ic_favorite_border);
                mFavoriteItem.setTitle(R.string.action_favorite);
            }
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void updateDatabase() {
        if (mFavorite) {
            insertMovie();
        } else {
            deleteMovie();
        }
    }

    private void insertMovie() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COL_ID, mMovie.getId());
        movieValues.put(MovieContract.MovieEntry.COL_TITLE, mMovie.getTitle());
        movieValues.put(MovieContract.MovieEntry.COL_POSTER_PATH, mMovie.getPosterPath());
        movieValues.put(MovieContract.MovieEntry.COL_OVERVIEW, mMovie.getOverview());
        movieValues.put(MovieContract.MovieEntry.COL_RELEASE_DATE, mMovie.getReleaseDate());
        movieValues.put(MovieContract.MovieEntry.COL_VOTE_COUNT, mMovie.getVoteCount());
        movieValues.put(MovieContract.MovieEntry.COL_VOTE_AVERAGE, mMovie.getVoteAverage());

        Uri insertedUri = getActivity().getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,
                movieValues
        );

        showResponse(ContentUris.parseId(insertedUri) > 0);

    }

    private void showResponse(boolean success) {
        if (success) {
            Snackbar.make(getView(), R.string.favorite_success, Snackbar.LENGTH_SHORT).show();
        } else {
            // Revert
            mFavorite = !mFavorite;
            updateFavoriteMenu();
            Snackbar.make(getView(), R.string.favorite_error, Snackbar.LENGTH_SHORT).show();
        }
    }


    private void deleteMovie() {
        int rowsDeleted = getActivity().getContentResolver().delete(
                MovieContract.MovieEntry.buildMovieUri(mMovie.getId()),
                null,
                null
        );

        showResponse(rowsDeleted > 0);
    }

    @Nullable
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieContract.MovieEntry.buildMovieUri(mMovie.getId()),
                        null,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, @NonNull Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER:
                mFavorite = data.moveToFirst();
                updateFavoriteMenu();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void displayMovie(Movie movie) {
        setMovie(movie);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        fetchMovieDetails();
    }

    private void fetchMovieDetails() {
        if (mMovie != null) {

            // Lets not wait for fetching reviews and show the basic movie as soon as user selects a movie
            setAdapter();
            MovieService movieService = RetrofitServiceGenerator.createService(MovieService.class);

            Observable<List<Review>> reviewsObservable = movieService.getReviews(mMovie.getId(), BuildConfig.MOVIE_DB_API_KEY)
                    .subscribeOn(Schedulers.io())
                    .map(reviewResults -> reviewResults.results);


            Observable<List<Video>> videosObservable = movieService.getVideos(mMovie.getId(), BuildConfig.MOVIE_DB_API_KEY)
                    .subscribeOn(Schedulers.io())
                    .map(videoResults -> videoResults.results)
                    .map(videos -> filterTrailers(videos));

            Observable.zip(
                    reviewsObservable,
                    videosObservable,
                    this::createMovieWithReviewsAndVideos)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movie -> setAdapter());
        }
    }

    private List<Video> filterTrailers(List<Video> videos) {
        List<Video> trailers = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++) {
            if(videos.get(i).getType().contentEquals(Video.TYPE_TRAILER)){
                trailers.add(videos.get(i));
            }
        }
        return trailers;
    }

    private Movie createMovieWithReviewsAndVideos(List<Review> reviews, List<Video> videos) {
        mMovie.setReviews(reviews);
        mMovie.setVideos(videos);
        return mMovie;
    }


    public void setMovie(Movie movie) {
        mMovie = movie;
    }
}
