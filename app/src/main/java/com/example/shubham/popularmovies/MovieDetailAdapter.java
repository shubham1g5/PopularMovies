package com.example.shubham.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shubham.popularmovies.model.Movie;
import com.example.shubham.popularmovies.model.Review;
import com.example.shubham.popularmovies.model.Video;

import java.util.List;

class MovieDetailAdapter extends RecyclerView.Adapter {

    private static final int HEADER_ITEM_VIEW_TYPE = 1;
    private static final int SECTION_HEADER_ITEM_VIEW_TYPE = 2;
    private static final int VIDEO_ITEM_VIEW_TYPE = 3;
    private static final int REVIEW_ITEM_VIEW_TYPE = 4;

    private final List<Review> mReviews;
    private final List<Video> mVideos;
    @NonNull
    private final Movie mMovie;
    private final Context mContext;

    public MovieDetailAdapter(Context context, @NonNull Movie movie) {
        mMovie = movie;
        mReviews = movie.getReviews();
        mVideos = movie.getVideos();
        mContext = context;
    }

    @Nullable
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADER_ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_header_item, null);
                return new HeaderViewHolder(view);
            case SECTION_HEADER_ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_section_header_item, null);
                return new SectionHeaderViewHolder(view);
            case VIDEO_ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_video_item, null);
                return new VideoViewHolder(view);
            case REVIEW_ITEM_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_detail_review_item, null);
                return new BindingHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER_ITEM_VIEW_TYPE:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.getBinding().setVariable(BR.movie, mMovie);
                headerViewHolder.getBinding().executePendingBindings();
                ImageView mPosterView = headerViewHolder.getmPosterView();
                if (mPosterView != null) {
                    String posterUrl = mMovie.getAbsoluteUrl();
                    PicassoSingleton.getPicasso(mContext)
                            .load(posterUrl)
                            .placeholder(R.drawable.loading_animation)
                            .error(android.R.drawable.stat_notify_error)
                            .into(mPosterView);
                    mPosterView.setContentDescription(mMovie.getTitle());
                }
                break;
            case SECTION_HEADER_ITEM_VIEW_TYPE:
                ((SectionHeaderViewHolder) holder).mHeaderTv.setText(mVideos.size() > 0 && position == 1 ? R.string.trailers : R.string.reviews);
                break;
            case VIDEO_ITEM_VIEW_TYPE:
                int videoPosition = position - 2;
                Video video = mVideos.get(videoPosition);
                ((BindingHolder) holder).getBinding().setVariable(BR.video, video);
                ((BindingHolder) holder).getBinding().executePendingBindings();


                break;
            case REVIEW_ITEM_VIEW_TYPE:
                int reviewPosition = position - 2 - mVideos.size() - (mVideos.size() > 0 ? 1 : 0);
                Review review = mReviews.get(reviewPosition);
                ((BindingHolder) holder).getBinding().setVariable(BR.review, review);
                ((BindingHolder) holder).getBinding().executePendingBindings();
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 1 + mReviews.size() + mVideos.size() +
                (mReviews.size() > 0 ? 1 : 0) + (mVideos.size() > 0 ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        int videosCount = mVideos.size();
        if (position == 0) {
            return HEADER_ITEM_VIEW_TYPE;
        } else if ((videosCount > 0 && (position == 1 || position == videosCount + 2))
                || (videosCount == 0 && position == 1)) {
            return SECTION_HEADER_ITEM_VIEW_TYPE;
        } else if (position < mVideos.size() + 2) {
            return VIDEO_ITEM_VIEW_TYPE;
        } else {
            return REVIEW_ITEM_VIEW_TYPE;
        }
    }

    public static class HeaderViewHolder extends BindingHolder {
        // each data item is just a string in this case
        private final ImageView mPosterView;

        public HeaderViewHolder(@NonNull View v) {
            super(v);
            mPosterView = (ImageView) v.findViewById(R.id.poster_imageview);
        }

        public ImageView getmPosterView() {
            return mPosterView;
        }
    }

    public static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView mHeaderTv;

        public SectionHeaderViewHolder(@NonNull View v) {
            super(v);
            mHeaderTv = (TextView) v.findViewById(R.id.header_tv);
        }
    }

    public class VideoViewHolder extends BindingHolder implements View.OnClickListener{

        public VideoViewHolder(@NonNull View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            playVideo(mVideos.get(getAdapterPosition() - 2).getVideoUrl());
        }

        private void playVideo(String videoUrl) {
            Uri videoUri = Uri.parse(videoUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(videoUri);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }else {
                Toast.makeText(mContext, R.string.no_video_app, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final ViewDataBinding mBinding;

        public BindingHolder(@NonNull View v) {
            super(v);
            mBinding = DataBindingUtil.bind(v);
        }

        public ViewDataBinding getBinding() {
            return mBinding;
        }

    }
}
