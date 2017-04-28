package es.cice.practicapedrogonzalez;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
/*
* Obtengo localización
* Capturo keyword
* Llamo a ListMapActivity
*
* Pendiente barra lateral (búsquedas guardadas y favoritos)
*/
public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private final static String TAG = "MainActivity";
    private Context context = this;
    private GoogleApiClient apiClient = null;
    private Location lastLocation;
    private Double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate()");

        CardView cvRestaurantes = (CardView)findViewById(R.id.cvRestaurantes);
        CardView cvTiendas = (CardView)findViewById(R.id.cvTiendas);
        CardView cvSitiosInteres = (CardView)findViewById(R.id.cvSitiosInteres);

        cvRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListMapActivity.class);
                intent.putExtra("Latitud", latitud);
                intent.putExtra("Longitud", longitud);
                intent.putExtra("Keyword", "restaurant");
                startActivity(intent);
            }
        });

        cvTiendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListMapActivity.class);
                intent.putExtra("Latitud", latitud);
                intent.putExtra("Longitud", longitud);
                intent.putExtra("Keyword", "shopping");
                startActivity(intent);
            }
        });

        cvSitiosInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListMapActivity.class);
                intent.putExtra("Latitud", latitud);
                intent.putExtra("Longitud", longitud);
                intent.putExtra("Keyword", "lugares de interés turístico");
                startActivity(intent);
            }
        });

        if(apiClient == null){
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "- onStart()...");
        if(!apiClient.isConnected()){
            apiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "- onStop()...");
        if(apiClient.isConnected()){
            apiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "- onConnected()...");

        try{
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);

            if(lastLocation != null){
                latitud = lastLocation.getLatitude();
                longitud = lastLocation.getLongitude();
                Log.e(TAG, "- origen : " + latitud + "," + longitud);
            }else{
                Log.e(TAG, "- No se ha podido recuperar lastLocation");
            }

        }catch (SecurityException e){
            Log.e(TAG, "- SecurityException");
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "- onConnectionSuspended()...");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "- onConnectionFailed()...");
    }
}
