package com.example.shubham.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.shubham.popularmovies.MovieListFragment.EXTRA_MOVIE_PARCELABLE;

/**
 * Created by shubham on 20/11/16.
 */
public class DetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container);

        ImageView posterView = (ImageView) view.findViewById(R.id.poster_imageview);
        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        TextView plotTv = (TextView) view.findViewById(R.id.plot_tv);
        TextView releaseTv = (TextView) view.findViewById(R.id.release_tv);
        TextView voteCountTv = (TextView) view.findViewById(R.id.count_tv);
        TextView ratingTv = (TextView) view.findViewById(R.id.rating_tv);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE_PARCELABLE);
            titleTv.setText(movie.title);
            plotTv.setText(movie.plot);
            voteCountTv.setText(getString(R.string.votes_count, movie.votes));
            releaseTv.setText(getString(R.string.released_on, movie.releaseDate));
            ratingBar.setRating((float) (movie.rating / 10));
            ratingTv.setText("" + movie.rating);

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
