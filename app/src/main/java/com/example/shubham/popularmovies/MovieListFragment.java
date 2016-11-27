package com.example.shubham.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shubham.popularmovies.model.Movie;
import com.example.shubham.popularmovies.model.MovieResults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * The fragment uses TheMovieDB API to list movies in a grid layout according to user settings.
 */

public class MovieListFragment extends Fragment {


    private static final String MOVIES_KEY = "movies";
    public static final String EXTRA_MOVIE_PARCELABLE = "movie_parcel";
    public static boolean refreshDisplay = false;

    private RecyclerView movieListRecyclerView;
    private MovieListAdapter movieListAdapter;
    private View loadingIndicatorView;
    private Movie[] movies = new Movie[0];


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_KEY)) {
            movies = (Movie[]) savedInstanceState.getParcelableArray(MOVIES_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(MOVIES_KEY, movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        loadingIndicatorView = view.findViewById(R.id.loading_indicator);

        movieListRecyclerView = (RecyclerView) view.findViewById(R.id.movielist_recyclerview);
        movieListRecyclerView.setHasFixedSize(true);
        movieListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        movieListAdapter = new MovieListAdapter(getContext(), new ArrayList<>(Arrays.asList(movies)));
        movieListRecyclerView.setAdapter(movieListAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (movies == null || movies.length == 0 || refreshDisplay) {
            updateMovieList();
            refreshDisplay = false;
        }
    }

    /*
        Fetch movies using API and attach that to the adapter once it's done
    */
    private void updateMovieList() {
        if (isOnline()) {
            loadingIndicatorView.setVisibility(View.VISIBLE);
            MovieService movieService = RetrofitServiceGenerator.createService(MovieService.class);

            String sortType = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.pref_sortType_key),
                            getString(R.string.pref_sortTypes_default));

            Observable<MovieResults> movieResultsObservable = movieService.listMovies(sortType, BuildConfig.MOVIE_DB_API_KEY);

            movieResultsObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movieResults -> {
                        loadingIndicatorView.setVisibility(View.GONE);
                        List<Movie> movieList = movieResults.results;
                        movies = movieList.toArray(new Movie[movieList.size()]);
                        updateUI();
                    }, throwable -> {
                        loadingIndicatorView.setVisibility(View.GONE);
                        try {
                            throw new Exception(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        if (movies != null) {
            movieListAdapter.clear();
            movieListAdapter.addAll(movies);
        } else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}