package es.cice.practicapedrogonzalez.model;

import android.graphics.Bitmap;

/**
 * Created by pgonzalez on 14/04/2017.
 */

public class Galeria {
    private Bitmap fotoGaleria;

    public Galeria(Bitmap fotoGaleria) {
        this.fotoGaleria = fotoGaleria;
    }

    public Bitmap getFotoGaleria() {
        return fotoGaleria;
    }

    public void setFotoGaleria(Bitmap fotoGaleria) {
        this.fotoGaleria = fotoGaleria;
    }
}
