package es.cice.practicapedrogonzalez.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import es.cice.practicapedrogonzalez.R;
import es.cice.practicapedrogonzalez.json.Review;

/**
 * Created by pgonzalez on 09/04/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomerViewHolder>{
    private List<Review> reviews;

    public static class CustomerViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivProfilePhoto;
        public TextView tvRnombre;
        public TextView tvRelativeTime;
        public TextView tvRrating;
        public RatingBar ratingBarReview;
        public TextView tvRtext;

        public CustomerViewHolder(View v){
            super(v);
            tvRnombre = (TextView)v.findViewById(R.id.tvRnombre);
            tvRelativeTime = (TextView)v.findViewById(R.id.tvRelativeTime);
            tvRrating = (TextView)v.findViewById(R.id.tvRrating);
            ratingBarReview = (RatingBar)v.findViewById(R.id.ratingBarReview);
            tvRtext = (TextView)v.findViewById(R.id.tvRtext);
            ivProfilePhoto = (ImageView)v.findViewById(R.id.ivProfilePhoto);
        }
    }

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.elem_review, viewGroup, false);
        return new CustomerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder viewHolder, int position) {
        viewHolder.ivProfilePhoto.setImageBitmap(reviews.get(position).getProfilePhoto());
        viewHolder.tvRnombre.setText(reviews.get(position).getAuthorName());
        viewHolder.tvRelativeTime.setText(reviews.get(position).getRelativeTimeDescription());
        Float rating = reviews.get(position).getRating();
        viewHolder.tvRrating.setText(String.valueOf(rating));
        viewHolder.ratingBarReview.setRating(rating);
        viewHolder.ratingBarReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        viewHolder.tvRtext.setText(reviews.get(position).getText());
    }
}
