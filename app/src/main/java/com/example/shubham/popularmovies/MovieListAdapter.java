package com.example.shubham.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shubham.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.shubham.popularmovies.MovieListFragment.EXTRA_MOVIE_PARCELABLE;

/**
 * Created by shubham on 18/11/16.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private final ArrayList<Movie> movies;
    private final Context mContext;

    public MovieListAdapter(Context context, ArrayList<Movie> movies) {
        this.movies = movies;
        mContext = context;
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    public void addAll(Movie[] moviesToAdd) {
        movies.addAll(Arrays.asList(moviesToAdd));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.movie_poster_imageview);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        String posterUrl = movie.getAbsoluteUrl();
        Picasso.with(mContext)
                .load(posterUrl)
                .placeholder(R.drawable.loading_animation)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(EXTRA_MOVIE_PARCELABLE, movie);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
