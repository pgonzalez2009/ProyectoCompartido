package es.cice.practicapedrogonzalez.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import es.cice.practicapedrogonzalez.DetailActivity;
import es.cice.practicapedrogonzalez.adapter.InfoAdapter;
import es.cice.practicapedrogonzalez.model.Sitio;

/**
 * Created by pgonzalez on 17/04/2017.
 */

public class MapaSitiosFragment extends MapFragment implements OnMapReadyCallback {
    private final static String TAG = "MapaSitiosFragment";
    private GoogleMap mMap;
    private Double latitud, longitud;
    private List<Sitio> sitios;
    private String itemSeleccionado, distance, duration;
    private Marker marcador;
    private Sitio itemSitio = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "- onCreateView()...");
        View v = super.onCreateView(inflater, container, savedInstanceState);
        getMapAsync(this);
        return v;
    }

    public void setParametrosLista(Double latitud, Double longitud, List<Sitio> sitios) {
        Log.e(TAG, "- setParametrosLista()...");
        this.latitud = latitud;
        this.longitud = longitud;
        this.sitios = sitios;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "- onMapReady()...");
        LatLng lugar = null;
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Click sobre marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.e(TAG, "- onMarkerClick() marker : " + marker);
                return false;
            }
        });

        // Click sobre info windows
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.e(TAG, "- onInfoWindowClick() marker : " + marker);

                itemSitio = (Sitio)marker.getTag();
                itemSeleccionado = itemSitio.getPlaceId();
                distance = itemSitio.getDistance();
                duration = itemSitio.getDuration();

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("ItemSeleccionado", itemSeleccionado);
                intent.putExtra("Distance", distance);
                intent.putExtra("Duration", duration);
                startActivity(intent);

            }
        });

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i=0; i < sitios.size(); i++){
            Log.e(TAG, "- for(Sitio sitio:sitios)");

            itemSitio = sitios.get(i);
            mMap.setInfoWindowAdapter(new InfoAdapter(getActivity()));
            lugar = new LatLng(itemSitio.getLatitud(), itemSitio.getLongitud());
            marcador = mMap.addMarker(new MarkerOptions().position(lugar));
            marcador.setTag(itemSitio);
            Log.e(TAG, "- marcador : " + marcador);

            builder.include(lugar);
        }

        LatLngBounds bounds = builder.build();
        int padding = 0;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //mMap.animateCamera(cameraUpdate);
        mMap.moveCamera(cameraUpdate);

        /*
        // Nos posicionamos en el lugar actual
        lugar = new LatLng(latitud, longitud);
        // LatLng lugar = new LatLng(40.505529, -3.663819);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar));
        CameraPosition camPos = new CameraPosition.Builder().target(lugar).zoom(14).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(cameraUpdate);
        */
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    @Override
    public void onStart() {
        Log.e(TAG, "- onStart()...");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "- onStop()...");
        super.onStop();
    }


}
