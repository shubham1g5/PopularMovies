package com.example.shubham.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * The fragment uses TheMovieDB API to list movies in a grid layout according to user settings.
 */
public class MovieListFragment extends Fragment {


    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private static final String MOVIES_KEY = "movies";
    public static final String EXTRA_MOVIE_PARCELABLE = "movie_parcel";
    public static boolean refreshDisplay = false;

    private GridView movieListGridView;
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

        movieListGridView = (GridView) view.findViewById(R.id.movielist_gridview);
        movieListAdapter = new MovieListAdapter(getContext(), new ArrayList<>(Arrays.asList(movies)));
        movieListGridView.setAdapter(movieListAdapter);

        movieListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = movieListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(EXTRA_MOVIE_PARCELABLE, selectedMovie);
                startActivity(intent);
            }
        });
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
            String sortType = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.pref_sortType_key),
                            getString(R.string.pref_sortTypes_default));
            new fetchMoviesTask().execute(sortType);
        } else {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class fetchMoviesTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            loadingIndicatorView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseStr = null;

            try {

                final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/" + strings[0];
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                responseStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                parseMovies(responseStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private void parseMovies(String responseStr) throws JSONException {
            if (TextUtils.isEmpty(responseStr)) {
                return;
            }

            final String RESULTS = "results";

            JSONObject responseObj = new JSONObject(responseStr);
            JSONArray results = responseObj.optJSONArray(RESULTS);

            movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++) {
                movies[i] = new Movie(results.getJSONObject(i));
            }
        }

        @Override
        protected void onPostExecute(Void object) {
            loadingIndicatorView.setVisibility(View.GONE);
            if (movies != null) {
                movieListAdapter.clear();
                movieListAdapter.addAll(movies);
            }
        }
    }
}
