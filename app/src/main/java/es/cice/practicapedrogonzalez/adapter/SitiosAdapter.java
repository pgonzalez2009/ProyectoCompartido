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
import es.cice.practicapedrogonzalez.model.Sitio;

/**
 * Created by pgonzalez on 09/04/2017.
 */

public class SitiosAdapter extends RecyclerView.Adapter<SitiosAdapter.CustomerViewHolder> implements View.OnClickListener{
    private List<Sitio> sitios;
    private View.OnClickListener listener;

    public static class CustomerViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageViewFoto;
        public TextView tvNombre;
        public TextView tvRating;
        public RatingBar ratingBar;
        public TextView tvDireccion;
        public TextView tvOpenNow;
        public TextView tvDistance;
        public TextView tvDuration;

        public CustomerViewHolder(View v){
            super(v);
            imageViewFoto = (ImageView)v.findViewById(R.id.imageViewFoto);
            tvNombre = (TextView)v.findViewById(R.id.tvNombre);
            tvRating = (TextView)v.findViewById(R.id.tvRating);
            ratingBar = (RatingBar)v.findViewById(R.id.ratingBar);
            tvDireccion = (TextView)v.findViewById(R.id.tvDireccion);
            tvOpenNow = (TextView)v.findViewById(R.id.tvOpenNow);
            tvDistance = (TextView)v.findViewById(R.id.tvDistance);
            tvDuration = (TextView)v.findViewById(R.id.tvDuration);
        }
    }

    public SitiosAdapter(List<Sitio> sitios) {
        this.sitios = sitios;
    }

    @Override
    public int getItemCount() {
        return sitios.size();
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.elem_sitio, viewGroup, false);
        v.setOnClickListener(this);
        return new CustomerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder viewHolder, int position) {
        viewHolder.imageViewFoto.setImageBitmap(sitios.get(position).getFoto());
        viewHolder.tvNombre.setText(sitios.get(position).getNombre());
        Float rating = sitios.get(position).getRating();
        viewHolder.tvRating.setText(String.valueOf(rating));
        viewHolder.ratingBar.setRating(rating);
        viewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        viewHolder.tvDireccion.setText(sitios.get(position).getDireccion().trim());
        viewHolder.tvOpenNow.setText(sitios.get(position).getOpenNow());
        viewHolder.tvDistance.setText(sitios.get(position).getDistance());
        viewHolder.tvDuration.setText(sitios.get(position).getDuration());
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null)
            listener.onClick(v);
    }
}
