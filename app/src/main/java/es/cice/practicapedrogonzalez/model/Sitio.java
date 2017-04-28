package es.cice.practicapedrogonzalez.model;

import android.graphics.Bitmap;

/**
 * Created by pgonzalez on 11/04/2017.
 */

public class Sitio {
    private String placeId;
    Double latitud;
    Double longitud;
    private Bitmap foto;
    private String nombre;
    private Float rating;
    private String direccion;
    private String openNow;
    private String distance;
    private String duration;

    public Sitio(String placeId, Double latitud, Double longitud, Bitmap foto, String nombre, Float rating, String direccion, String openNow, String distance, String duration) {
        this.placeId = placeId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.nombre = nombre;
        this.rating = rating;
        this.direccion = direccion;
        this.openNow = openNow;
        this.distance = distance;
        this.duration = duration;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
