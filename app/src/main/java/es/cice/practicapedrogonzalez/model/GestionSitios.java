package es.cice.practicapedrogonzalez.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.cice.practicapedrogonzalez.R;
import es.cice.practicapedrogonzalez.json.MatrixObject;
import es.cice.practicapedrogonzalez.json.Photo;
import es.cice.practicapedrogonzalez.json.Result;
import es.cice.practicapedrogonzalez.json.SitiosSchema;

/**
 * Created by pgonzalez on 16/04/2017.
 */

public class GestionSitios {
    private final static String TAG = "GestionSitios";
    private final static String MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private final static String PLACE_URL = "https://maps.googleapis.com/maps/api/place/";
    private final static String NEAR_MODE = "nearbysearch/json?";
    private final static String TEXT_MODE = "textsearch/json?";
    private final static String PHOTO_URL = "photo?";
    private final static String KEY = "AIzaSyAvd0kOh8DqLuQ-9OcB7CClXnIIP8Ju6OA";
    //private final static String KEY = "AIzaSyCH1rsLE7uV8N-6xZJ-PicWWarqDV2w1JU";
    private final static String QUERY = "restaurante";
    private final static String TYPES = "restaurant";
    //private final static String TYPES = "point_of_interest";    // no funciona
    private final static String KEYWORD = "restaurant";
    private final static String NAME = "comida";
    private final static String LOCATION = "40.417258,-3.703486";
    private final static String RADIUS = "50000";
    private final static String RANKBY = "distance";
    private final static String MODE = "walking";
    private final static String UNITS = "metric";
    private final static String SEARCH_MODE = NEAR_MODE;
    private final static int MAX_HEIGHT = 400;
    private final static int MAX_WIDTH = 400;
    private static List<Sitio> sitios;
    private static String errorMsg = "OK";

    public static String obtenerDatosSitios(Context context, Double latitud, Double longitud, String keyword){
        Log.e(TAG, "- obtenerDatosSitios()...");
        boolean status = false;
        HashMap<String, String> param = new HashMap<String, String>();

        try {
            param.put("key", KEY);
            param.put("location", latitud + "," + longitud);
            param.put("language", Locale.getDefault().getLanguage());

            if(SEARCH_MODE == NEAR_MODE){
                param.put("rankby", RANKBY);
                //param.put("type", TYPES); // no va bien
                param.put("keyword", keyword);
                //param.put("name", NAME);
            }else if (SEARCH_MODE == TEXT_MODE){
                param.put("query", QUERY);
                param.put("radius", RADIUS);
            }else{
                Log.e(TAG, "- Error en SEARCH_MODE");
                errorMsg = "Error in Search Mode";
                return errorMsg;
            }



            String responseJSON = Tools.getGoogleApiData(PLACE_URL + SEARCH_MODE, param);
            //String responseJSON = datosSitioPrueba();




            Log.e(TAG, "- responseJSON : " + responseJSON);

            if (responseJSON != null && !responseJSON.isEmpty()) {
                Gson gson = new Gson();
                SitiosSchema sitiosSchema = gson.fromJson(responseJSON, SitiosSchema.class);
                status = parseDatosSitios(context, sitiosSchema, latitud, longitud);
                //status = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "- Exception 1 : No Place data available");
        }
        Log.e(TAG, "- status obtenerDatosSitios() : " + status);
        return errorMsg;
    }

    private static Boolean parseDatosSitios(Context context, SitiosSchema sitiosSchema, Double latitud, Double longitud) {
        Log.e(TAG, "- parseDatosSitios()...");
        List<Result> resultObjects = sitiosSchema.getResults();
        HashMap<String, String> param;
        List<Photo> photo;
        String placeId;
        Bitmap imgFoto = null;
        String nombre;
        Float rating;
        String direccion;
        String openNow;
        int maxWidth, maxHeight;
        String photoreference;
        String latitudDestino;
        String longitudDestino;
        Double latitudSitio;
        Double longitudSitio;
        boolean foundPhoto = false;
        boolean foundMatrix;
        MatrixObject matrixObject = null;
        String distance, duration;
        String statusJSON;

        statusJSON = sitiosSchema.getStatus();

        if(!statusJSON.contains("OK")){
            Log.e(TAG, "- statusJSON : " + statusJSON);
            errorMsg = statusJSON;
            return false;
        }

        if(resultObjects.size() == 0){
            Log.e(TAG, "- No resultObjects available");
            errorMsg = "Error in Google Place";
            return false;
        }

        try {
            sitios = new ArrayList<Sitio>();

            for(int i=0; i<resultObjects.size(); i++){
                if(resultObjects.get(i).getPhotos() == null){
                    continue;   // solo mostraremos sitios con foto
                }

                photo = resultObjects.get(i).getPhotos();
                latitudSitio = resultObjects.get(i).getGeometry().getLocation().getLat();
                longitudSitio = resultObjects.get(i).getGeometry().getLocation().getLng();
                latitudDestino = String.valueOf(latitudSitio);
                longitudDestino = String.valueOf(longitudSitio);

                for(int j=0; j<photo.size(); j++){

                    if(photo.get(j) != null){   // solo mostraremos sitios con foto
                        photoreference = photo.get(j).getPhotoReference();

                        if(photoreference != null && !photoreference.isEmpty()){
                            param = new HashMap<String, String>();
                            param.put("key", KEY);
                            param.put("photoreference", photoreference);
                            maxWidth = photo.get(j).getWidth();
                            maxHeight = photo.get(j).getHeight();

                            if(maxWidth < 0 || maxWidth > MAX_WIDTH){
                                maxWidth = MAX_WIDTH;
                            }

                            if(maxHeight < 0 || maxHeight > MAX_HEIGHT){
                                maxHeight = MAX_HEIGHT;
                            }

                            param.put("maxwidth", String.valueOf(maxWidth));
                            param.put("maxheight", String.valueOf(maxHeight));
                            Log.e(TAG, "- Obtener foto ...");
                            imgFoto = Tools.getGooglePlacePhoto(PLACE_URL + PHOTO_URL, param);

                            if (imgFoto != null){
                                foundPhoto = true;
                                break;
                            }
                        }
                    }
                }

                if(!foundPhoto) {
                    continue;
                }

                foundMatrix = false;
                param = new HashMap<String, String>();
                param.put("key", KEY);
                param.put("origins", latitud + "," + longitud);
                param.put("destinations", latitudDestino + "," + longitudDestino);
                param.put("mode", MODE);
                param.put("language", Locale.getDefault().getLanguage());
                param.put("units", UNITS);

                Log.e(TAG, "- Obtener datosMatrix ...");



                String responseJSON = Tools.getGoogleApiData(MATRIX_URL, param);
                //String responseJSON = datosMatrixPrueba();



                Log.e(TAG, "- responseJSON : " + responseJSON);

                if (responseJSON != null && !responseJSON.isEmpty()) {
                    Gson gson = new Gson();
                    matrixObject = gson.fromJson(responseJSON, MatrixObject.class);
                    statusJSON = matrixObject.getStatus();

                    if(statusJSON.contains("OK")){
                        foundMatrix = true;
                    }
                }

                placeId = resultObjects.get(i).getPlaceId();
                nombre = resultObjects.get(i).getName();
                rating = resultObjects.get(i).getRating();

                if(SEARCH_MODE == NEAR_MODE){
                    direccion = resultObjects.get(i).getVicinity();
                }else if (SEARCH_MODE == TEXT_MODE){
                    direccion = resultObjects.get(i).getFormattedAddress();
                }else{
                    direccion = "";
                }

                if(resultObjects.get(i).getOpeningHours() != null){
                    if(resultObjects.get(i).getOpeningHours().getOpenNow() == true){
                        openNow = context.getResources().getString(R.string.open);
                    }else{
                        openNow = context.getResources().getString(R.string.closed);
                    }
                }else{
                    openNow = context.getResources().getString(R.string.closed);
                }

                if(foundMatrix){
                    distance = matrixObject.getRows().get(0).getElements().get(0).getDistance().getText();
                    duration = matrixObject.getRows().get(0).getElements().get(0).getDuration().getText();
                }else{
                    distance = "";
                    duration = "";
                }

                sitios.add(new Sitio(placeId, latitudSitio, longitudSitio, imgFoto, nombre, rating, direccion, openNow, distance, duration));
            }
        } catch (Exception e) {
            Log.e(TAG, "- Exception 2 : No additional data available");
        }

        if(foundPhoto) {
            return true;
        }else{
            errorMsg = "Error in Google Place";
            return false;
        }
    }

    public static List<Sitio> getSitios(){
        return sitios;
    }

    private static String datosSitioPrueba() {
        return "{\n" +
                "   \"html_attributions\" : [],\n" +
                "   \"next_page_token\" : \"CpQCBQEAAMioi3KAnVBLmRh56K5RivY8ortoJEVLW5lEa1LXuUGs7LZNw2dSLxVJ67iwbywgqNZtmyHMDEb8ACZ3GM3lXpPL3-Bhdb_WrdN1EFufPzb33o6zk8koFL-c-P3uwKiTZdo1dWlnXkZfh-qpTFEiQgseA_HBRSVo6UNOj4efq9BbGuDMfeajq4rp27n83gFAW-DBNlmHfGYonnQsEPUQljZDPskUNOYd_trJ44dg0PB9CwPsU5XctizSTwfcagBo_eXzx9ptWH-borU2SBMgw3i0OBLYxzKHadKjnZjJZnZAMB44f69c-Zyw6Ph1WTyDU3IPbRDiyK2emWaJC8yzFTykEndYOyCaK_lUzRT1-yPbEhD72lZgmFkxTaI7D6ceo6mmGhRPpVr-eHNHr3kBjmMzR9jwJe0l4w\",\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 40.5068355,\n" +
                "               \"lng\" : -3.6643151\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 40.5082340302915,\n" +
                "                  \"lng\" : -3.662961019708497\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 40.5055360697085,\n" +
                "                  \"lng\" : -3.665658980291501\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                "         \"id\" : \"e2e8797d29e5322c2e7031db295e614efc166cd8\",\n" +
                "         \"name\" : \"Maye's Bistró\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : false,\n" +
                "            \"weekday_text\" : []\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2048,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115938105757261159840/photos\\\"\\u003eMaye&#39;s Bistró\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CoQBdwAAABYd7t5me-3DnT95YAHs_3SYEg4MVhoVZyf7u6iaY075gw05eBfdv9yHCxk21W_fuvENRc4MQWhf98S78KEHRKbLQavKQNFy2EoMh8xU19Ac2q3WqDo1aa2K5vG_49blC0MgTs51S-FuIE7Rz8e94AAOCWq921-2jsEBexN1nQg7EhB9dox77gRfOc0GVKH4K-yWGhRSjZvj3uI_nZ0cVY0qZ-QDFD0o9w\",\n" +
                "               \"width\" : 2047\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJ7_mKofcrQg0RIvDVTh0S0EY\",\n" +
                "         \"rating\" : 4.5,\n" +
                "         \"reference\" : \"CmRRAAAAPpaV81NWFGYACu-f_LyCLz_RtS_7HKLTGEHXGU4NsuqBJb3-OaCespV23XUiKMmQ0JV17IMNXMFsmx64VO0-F-dwis6uXEBFLByIUt_Y-e66xb8eviJrvmiMZui7FqWAEhDWMupu32yy_fj_o5vIokYcGhSE0BYxyU-lbTr2HTgFQhis0gDy4A\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Local 6, Paseo Tierra de Melide, 13A, Madrid\"\n" +
                "      },\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
    }

    private static String datosMatrixPrueba() {
        return "{\n" +
                "   \"destination_addresses\" : [ \"Paseo Tierra de Melide, 15, 28050 Madrid, España\" ],\n" +
                "   \"origin_addresses\" : [ \"Calle de Valcarlos, 30-36, 28050 Madrid, España\" ],\n" +
                "   \"rows\" : [\n" +
                "      {\n" +
                "         \"elements\" : [\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"0,2 km\",\n" +
                "                  \"value\" : 221\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"3 min\",\n" +
                "                  \"value\" : 156\n" +
                "               },\n" +
                "               \"status\" : \"OK\"\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
    }
}
