package com.example.shubham.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubham.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    private final ArrayList<Movie> movies;
    private final Context mContext;
    private final View mEmptyView;

    public interface OnMovieSelectedCallback {
        void onMovieSelected(Movie movie, int position);
    }

    public MovieListAdapter(Context context, ArrayList<Movie> movies, View emptyView) {
        this.movies = movies;
        mContext = context;
        mEmptyView = emptyView;
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    public void addAll(Movie[] moviesToAdd) {
        movies.addAll(Arrays.asList(moviesToAdd));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public final ImageView imageView;

        public ViewHolder(@NonNull View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.movie_poster_imageview);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            ((OnMovieSelectedCallback) mContext).onMovieSelected(movies.get(getAdapterPosition()), getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        String posterUrl = movie.getAbsoluteUrl();
        PicassoSingleton.getPicasso(mContext)
                .load(posterUrl)
                .placeholder(R.drawable.loading_animation)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.imageView);
        holder.imageView.setContentDescription(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        mEmptyView.setVisibility(movies.size() > 0 ? View.GONE : View.VISIBLE);
        return movies.size();
    }
}
