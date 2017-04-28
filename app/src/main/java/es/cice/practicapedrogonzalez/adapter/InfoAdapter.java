package es.cice.practicapedrogonzalez.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import es.cice.practicapedrogonzalez.R;
import es.cice.practicapedrogonzalez.model.Sitio;

/**
 * Created by cice on 6/4/17.
 */

public class InfoAdapter implements GoogleMap.InfoWindowAdapter {
    private final static String TAG = "InfoAdapter";
    private Activity activity;
    private Sitio sitioTag;
    private ImageView imageViewFoto;
    private TextView tvNombre;
    private TextView tvRating;
    private RatingBar ratingBar;
    private TextView tvDireccion;
    private TextView tvOpenNow;
    private TextView tvDistance;
    private TextView tvDuration;


    public InfoAdapter(Activity activity) {
        Log.e(TAG, "- Constructor InfoAdapter()...");
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.e(TAG, "- getInfoWindow()...");
        View v = activity.getLayoutInflater().inflate(R.layout.elem_sitio, null);

        Log.e(TAG, "- marker : " + marker);

        sitioTag = (Sitio)marker.getTag();
        Log.e(TAG, "- sitioTag.getNombre() : " + sitioTag.getNombre());

        imageViewFoto = (ImageView)v.findViewById(R.id.imageViewFoto);
        tvNombre = (TextView)v.findViewById(R.id.tvNombre);
        tvRating = (TextView)v.findViewById(R.id.tvRating);
        ratingBar = (RatingBar)v.findViewById(R.id.ratingBar);
        tvDireccion = (TextView)v.findViewById(R.id.tvDireccion);
        tvOpenNow = (TextView)v.findViewById(R.id.tvOpenNow);
        tvDistance = (TextView)v.findViewById(R.id.tvDistance);
        tvDuration = (TextView)v.findViewById(R.id.tvDuration);

        imageViewFoto.setImageBitmap(sitioTag.getFoto());
        tvNombre.setText(sitioTag.getNombre());
        Float rating = sitioTag.getRating();
        tvRating.setText(String.valueOf(rating));
        ratingBar.setRating(rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        tvDireccion.setText(sitioTag.getDireccion().trim());
        tvOpenNow.setText(sitioTag.getOpenNow());
        tvDistance.setText(sitioTag.getDistance());
        tvDuration.setText(sitioTag.getDuration());

        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.e(TAG, "- getInfoContents()...");
        Log.e(TAG, "- marker : " + marker);
        return null;
    }
}
