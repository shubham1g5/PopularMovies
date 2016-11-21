package com.example.shubham.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shubham on 18/11/16.
 */

public class MovieListAdapter extends ArrayAdapter<Movie> {

    public MovieListAdapter(Context context, ArrayList<Movie> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_item, null);
        }

        Movie movie = getItem(position);
        String posterUrl = movie.getAbsoluteUrl();

        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_poster_imageview);

        Picasso.with(getContext())
                .load(posterUrl)
                .placeholder(R.drawable.loading_animation)
                .error(android.R.drawable.stat_notify_error)
                .into(imageView);

        return convertView;
    }
}
