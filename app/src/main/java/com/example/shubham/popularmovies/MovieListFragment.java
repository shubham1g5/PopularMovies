package com.example.shubham.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shubham.popularmovies.data.MovieContract;
import com.example.shubham.popularmovies.model.Movie;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * The fragment uses TheMovieDB API to list movies in a grid layout according to user settings.
 */

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String MOVIES_KEY = "movies";
    public static final String EXTRA_MOVIE_PARCELABLE = "movie_parcel";
    private static final int FAVORITE_LOADER = 1;
    private static final String SELECTED_POSITION = "selected_position";
    private static boolean isFavoriteMode;

    public static boolean refreshDisplay = false;

    private RecyclerView mMovieListRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private View loadingIndicatorView;
    @NotNull
    private Movie[] mMovies = new Movie[0];

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COL_ID,
            MovieContract.MovieEntry.COL_TITLE,
            MovieContract.MovieEntry.COL_OVERVIEW,
            MovieContract.MovieEntry.COL_RELEASE_DATE,
            MovieContract.MovieEntry.COL_POSTER_PATH,
            MovieContract.MovieEntry.COL_VOTE_COUNT,
            MovieContract.MovieEntry.COL_VOTE_AVERAGE
    };


    private static final int COL_ID = 0;
    private static final int COL_TITLE = 1;
    private static final int COL_OVERVIEW = 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_POSTER_PATH = 4;
    private static final int COL_VOTE_COUNT = 5;
    private static final int COL_VOTE_AVERAGE = 6;

    private boolean mTwoPane;
    private int mPosition = RecyclerView.NO_POSITION;
    @Nullable
    private String mSortType;
    private TextView mEmptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_KEY)) {
            mMovies = (Movie[]) savedInstanceState.getParcelableArray(MOVIES_KEY);
            mPosition = savedInstanceState.getInt(SELECTED_POSITION);
        }
        mTwoPane = getResources().getBoolean(R.bool.has_two_panes);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray(MOVIES_KEY, mMovies);
        outState.putInt(SELECTED_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        loadingIndicatorView = view.findViewById(R.id.loading_indicator);

        mMovieListRecyclerView = (RecyclerView) view.findViewById(R.id.movielist_recyclerview);
        mMovieListRecyclerView.setHasFixedSize(true);

        mEmptyView = (TextView) view.findViewById(R.id.empty_tv);
        mMovieListAdapter = new MovieListAdapter(getActivity(), new ArrayList<>(Arrays.asList(mMovies)), mEmptyView);
        mMovieListRecyclerView.setAdapter(mMovieListAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mTwoPane ? 1 : 2));
    }

    @Override
    public void onStart() {
        super.onStart();

        mSortType = Utility.getSortType(getActivity());;
        isFavoriteMode = mSortType.contentEquals(getString(R.string.pref_sortType_favorites_value));
        if (isFavoriteMode) {
            // Start the loader to query the DB
            getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        } else if (mMovies == null || mMovies.length == 0 || refreshDisplay) {
            updateMovieList();
            refreshDisplay = false;
        } else {
            restoreSelection();
        }
        setEmptyText();
    }

    private void setEmptyText() {
        mEmptyView.setText(isFavoriteMode ? getString(R.string.no_fav_movie) : getString(R.string.no_movie));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
                Fetch movies using API and attach that to the adapter once it's done
            */
    private void updateMovieList() {
        if (Utility.isOnline(getActivity())) {
            loadingIndicatorView.setVisibility(View.VISIBLE);
            MovieService movieService = RetrofitServiceGenerator.createService(MovieService.class);

            movieService.listMovies(mSortType, BuildConfig.MOVIE_DB_API_KEY)
                    .subscribeOn(Schedulers.io())
                    .map(movieResults -> movieResults.results)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movieList -> {
                        loadingIndicatorView.setVisibility(View.GONE);
                        mMovies = movieList.toArray(new Movie[movieList.size()]);
                        updateUI();
                    }, throwable -> {
                        try {
                            throw new Exception(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loadingIndicatorView.setVisibility(View.GONE);
                        Snackbar.make(getView(), R.string.error, Snackbar.LENGTH_LONG).show();
                    });
        } else {
            Utility.displayNoInternetMessage(getView());
        }
    }

    private void updateUI() {
        if (mMovies != null) {
            mMovieListAdapter.clear();
            mMovieListAdapter.addAll(mMovies);
            restoreSelection();
        } else {
            Snackbar.make(getView(), R.string.error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void restoreSelection() {
        if (mPosition != RecyclerView.NO_POSITION && mPosition < mMovies.length) {
            mMovieListRecyclerView.getLayoutManager().smoothScrollToPosition(mMovieListRecyclerView, null, mPosition);

            // Populate the detail with movie at this position in case of 2pane
            if (mTwoPane) {
                ((MovieListAdapter.OnMovieSelectedCallback) getActivity()).onMovieSelected(mMovies[mPosition], mPosition);
            }
        } else if (mMovies.length > 0 && mTwoPane) {
            // show by default the first movie
            ((MovieListAdapter.OnMovieSelectedCallback) getActivity()).onMovieSelected(mMovies[0], 0);
        }
    }


    @Nullable
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, @NonNull Cursor data) {
        if (isFavoriteMode) {
            if (!data.isAfterLast()) {
                mMovies = getMoviesFromCursor(data);
            }
            updateUI();
        }
    }

    @NonNull
    private Movie[] getMoviesFromCursor(@NonNull Cursor data) {
        Movie[] favMovies = new Movie[data.getCount()];
        while (data.moveToNext()) {
            Movie movieItem = new Movie(
                    data.getInt(COL_ID),
                    data.getString(COL_TITLE),
                    data.getString(COL_OVERVIEW),
                    data.getString(COL_POSTER_PATH),
                    data.getString(COL_RELEASE_DATE),
                    data.getInt(COL_VOTE_COUNT),
                    data.getDouble(COL_VOTE_AVERAGE)
            );
            favMovies[data.getPosition()] = movieItem;
        }
        return favMovies;
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
}