package es.cice.practicapedrogonzalez.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.cice.practicapedrogonzalez.DetailActivity;
import es.cice.practicapedrogonzalez.R;
import es.cice.practicapedrogonzalez.adapter.SitiosAdapter;
import es.cice.practicapedrogonzalez.model.Sitio;

/**
 * Created by pgonzalez on 09/04/2017.
 */

public class ListaSitiosFragment extends Fragment {
    private final static String TAG = "ListaSitiosFragment";
    private RecyclerView recyclerViewLista;
    private String itemSeleccionado, distance, duration;
    private List<Sitio> sitios;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "- onCreateView()...");
        View v = inflater.inflate(R.layout.fragmento_sitios, container, false);
        recyclerViewLista = (RecyclerView) v.findViewById(R.id.recyclerViewSitios);

        SitiosAdapter adapter = new SitiosAdapter(sitios);
        adapter.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           int position = recyclerViewLista.getChildAdapterPosition(view);
                                           itemSeleccionado = sitios.get(position).getPlaceId();
                                           distance = sitios.get(position).getDistance();
                                           duration = sitios.get(position).getDuration();

                                           Intent intent = new Intent(getContext(), DetailActivity.class);
                                           intent.putExtra("ItemSeleccionado", itemSeleccionado);
                                           intent.putExtra("Distance", distance);
                                           intent.putExtra("Duration", duration);
                                           startActivity(intent);
                                       }
                                   }
        );
        recyclerViewLista.setAdapter(adapter);
        recyclerViewLista.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return v;
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

    public void setSitios(List<Sitio> sitios) {
        Log.e(TAG, "- setSitios()...");
        this.sitios = sitios;
    }
}
