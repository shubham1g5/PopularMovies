package com.example.shubham.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shubham.popularmovies.databinding.FragmentDetailBinding;
import com.example.shubham.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import static com.example.shubham.popularmovies.MovieListFragment.EXTRA_MOVIE_PARCELABLE;

/**
 * Created by shubham on 20/11/16.
 */
public class DetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentDetailBinding fragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        View view = fragmentDetailBinding.getRoot();

        ImageView posterView = (ImageView) view.findViewById(R.id.poster_imageview);


        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE_PARCELABLE);
            fragmentDetailBinding.setMovie(movie);

            String posterUrl = movie.getAbsoluteUrl();
            Picasso.with(getActivity())
                    .load(posterUrl)
                    .placeholder(R.drawable.loading_animation)
                    .error(android.R.drawable.stat_notify_error)
                    .into(posterView);
        }

        return view;
    }
}
