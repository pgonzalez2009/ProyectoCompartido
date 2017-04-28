package es.cice.practicapedrogonzalez.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import es.cice.practicapedrogonzalez.R;
import es.cice.practicapedrogonzalez.model.Galeria;

/**
 * Created by pgonzalez on 09/04/2017.
 */

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.CustomerViewHolder>{
    private List<Galeria> galeria;

    public static class CustomerViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivFotoGaleria;

        public CustomerViewHolder(View v){
            super(v);
            ivFotoGaleria = (ImageView)v.findViewById(R.id.ivFotoGaleria);
        }
    }

    public GaleriaAdapter(List<Galeria> galeria) {
        this.galeria = galeria;
    }

    @Override
    public int getItemCount() {
        return galeria.size();
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.elem_galeria, viewGroup, false);
        return new CustomerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder viewHolder, int position) {
        viewHolder.ivFotoGaleria.setImageBitmap(galeria.get(position).getFotoGaleria());
    }
}
