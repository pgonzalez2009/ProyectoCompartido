package es.cice.practicapedrogonzalez;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import es.cice.practicapedrogonzalez.fragments.ListaSitiosFragment;
import es.cice.practicapedrogonzalez.fragments.MapaSitiosFragment;
import es.cice.practicapedrogonzalez.model.GestionSitios;
import es.cice.practicapedrogonzalez.model.Sitio;
import es.cice.practicapedrogonzalez.model.Tools;
/*
* ActionBar
* Obtengo lista de Sitios
* Inicialmente se muestra listaFragment
* Eventos capturados:
**** click en pestaña mapa --> pone mapaFragment
**** click en recyclerView sitios --> pone detalleFragment
**** click en infoWindows --> pone detalleFragment
**** click en onBackPressed --> pone fragmento anterior:
******** fragmentListaActivo --> se sale
******** fragmentMapaActivo --> pone listaFragment
******** fragmentDetalleActivo --> pone listaFragment
*
* Pendiente menu action (guardar búsqueda)
*/

// Comprobar navegacion en pila de fragmentos guardados vs onBackPressed
// Gestionar savedInstanceState

public class ListMapActivity extends AppCompatActivity {
    private final static String TAG = "ListMapActivity";
    FragmentManager fragmentManager = getFragmentManager();
    private FrameLayout layout_portrait_list_map;
    private ListaSitiosFragment listaFragment;
    private MapaSitiosFragment mapaFragment;
    private Boolean fragmentListaActivo = false;
    private Boolean fragmentMapaActivo = false;
    private Double latitud, longitud;
    private String keyword;
    private Context context = this;
    private List<Sitio> sitios;
    private CardView lista, mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map);
        Log.e(TAG, "onCreate()");

        lista = (CardView) findViewById(R.id.lista);
        mapa = (CardView) findViewById(R.id.mapa);
        layout_portrait_list_map = (FrameLayout)findViewById(R.id.layout_portrait_list_map);

        if(savedInstanceState == null) {    // Primera vez
            Log.e(TAG, "- savedInstanceState == null");
            Log.e(TAG, "- fragmentListaActivo : " + fragmentListaActivo);
            Log.e(TAG, "- fragmentMapaActivo : " + fragmentMapaActivo);

            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);

            latitud = getIntent().getExtras().getDouble("Latitud");
            longitud = getIntent().getExtras().getDouble("Longitud");
            keyword = getIntent().getExtras().getString("Keyword");

            listaFragment = new ListaSitiosFragment();
            mapaFragment = new MapaSitiosFragment();

            getSitiosSchema();

        }else{
            Log.e(TAG, "- savedInstanceState is NOT null");
            Log.e(TAG, "- fragmentListaActivo : " + fragmentListaActivo);
            Log.e(TAG, "- fragmentMapaActivo : " + fragmentMapaActivo);

            if(fragmentListaActivo){
                listaFragment = (ListaSitiosFragment)fragmentManager.getFragment(savedInstanceState, "listaFragment");
            }else if(fragmentMapaActivo){
                mapaFragment = (MapaSitiosFragment)fragmentManager.getFragment(savedInstanceState, "mapaFragment");
            }
        }

        mapa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Tenemos que estar en listaFragment --> ponemos mapaFragment
                if(fragmentListaActivo){
                    Log.e(TAG, "mapa.setOnClickListener()");
                    replaceToMapFragment();
                }
            }
        });

        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tenemos que estar en mapaFragment --> ponemos listaFragment
                if(fragmentMapaActivo){
                    Log.e(TAG, "lista.setOnClickListener()");
                    replaceToListaFragment();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed()");

        if(fragmentMapaActivo){
            replaceToListaFragment();
        }else if(fragmentListaActivo){
            fragmentListaActivo = false;
            fragmentMapaActivo = false;
            super.onBackPressed();
        }

        Log.e(TAG, "- fragmentListaActivo : " + fragmentListaActivo);
        Log.e(TAG, "- fragmentMapaActivo : " + fragmentMapaActivo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected()");

        if(item.getItemId() == android.R.id.home){
            this.onBackPressed();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "- onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        if(fragmentListaActivo){
            Log.e(TAG, "- save listaFragment");
            getFragmentManager().putFragment(outState, "listaFragment", listaFragment);
        }

        if(fragmentMapaActivo){
            Log.e(TAG, "- save mapaFragment");
            getFragmentManager().putFragment(outState, "mapaFragment", mapaFragment);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(TAG, "- onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void addListaFragment() {
        Log.e(TAG, "- addListaFragment()");
        lista.setBackgroundColor(Color.WHITE);
        mapa.setBackgroundColor(0xFFE7E8EA);
        listaFragment.setSitios(sitios);
        fragmentManager.beginTransaction().add(R.id.layout_portrait_list_map, listaFragment).commit();
        fragmentListaActivo = true;
        fragmentMapaActivo = false;
    }

    private void addMapaFragment() {
        Log.e(TAG, "- addMapaFragment()");
        lista.setBackgroundColor(0xFFE7E8EA);
        mapa.setBackgroundColor(Color.WHITE);
        mapaFragment.setParametrosLista(latitud, longitud, sitios);
        fragmentManager.beginTransaction().add(R.id.layout_portrait_list_map, mapaFragment).commit();
        fragmentListaActivo = false;
        fragmentMapaActivo = true;
    }

    private void replaceToListaFragment() {
        Log.e(TAG, "- replaceToListaFragment()");
        lista.setBackgroundColor(Color.WHITE);
        mapa.setBackgroundColor(0xFFE7E8EA);
        listaFragment.setSitios(sitios);
        fragmentManager.beginTransaction().replace(R.id.layout_portrait_list_map, listaFragment).commit();
        Log.e(TAG, "- listaFragment replaced");
        fragmentManager.executePendingTransactions();
        fragmentListaActivo = true;
        fragmentMapaActivo = false;
    }

    private void replaceToMapFragment() {
        Log.e(TAG, "- replaceToMapFragment()");
        lista.setBackgroundColor(0xFFE7E8EA);
        mapa.setBackgroundColor(Color.WHITE);
        mapaFragment.setParametrosLista(latitud, longitud, sitios);
        fragmentManager.beginTransaction().replace(R.id.layout_portrait_list_map, mapaFragment).commit();
        Log.e(TAG, "- mapaFragment replaced");
        fragmentManager.executePendingTransactions();
        fragmentListaActivo = false;
        fragmentMapaActivo = true;
    }

    private void getSitiosSchema() {
        Log.e(TAG, "- getSitiosSchema()...");
        TaskSitiosData taskGooglePlaceData = new TaskSitiosData();

        if (Tools.isNetworkAvailable(this)) {
            taskGooglePlaceData.execute();
        } else {
            Log.e(TAG, "- Network is not available");
        }
    }

    public class TaskSitiosData extends AsyncTask<String, Void, Boolean> {
        private String errorMsg = "OK";

        @Override
        protected Boolean doInBackground(String... params) {
            boolean status = false;
            Log.e(TAG, "- status doInBackground() : " + status);
            errorMsg = GestionSitios.obtenerDatosSitios(context, latitud, longitud, keyword);

            if(errorMsg.contains("OK")){
                sitios = GestionSitios.getSitios();
                status = true;
            }

            Log.e(TAG, "- status doInBackground() : " + status);
            return status;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            Log.e(TAG, "- onPostExecute()...");

            if (status) {
                // Primera vez
                if(!fragmentListaActivo){
                    addListaFragment();
                    Log.e(TAG, "- listaFragment added");
                    Log.e(TAG, "- fragmentListaActivo : " + fragmentListaActivo);
                    Log.e(TAG, "- fragmentMapaActivo : " + fragmentMapaActivo);
                }else{
                    Log.e(TAG, "- ERROR: fragmentListaActivo == true");
                    startActivity(new Intent(context, ErrorActivity.class));
                }
            }else{
                Log.e(TAG, "- ERROR: status == false");
                Intent intent = new Intent(context, ErrorActivity.class);
                intent.putExtra("errorMsg", errorMsg);
                startActivity(intent);
            }
            Log.e(TAG, "- status onPostExecute() : " + status);
        }
    }
}