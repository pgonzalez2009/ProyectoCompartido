package es.cice.practicapedrogonzalez;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.cice.practicapedrogonzalez.adapter.GaleriaAdapter;
import es.cice.practicapedrogonzalez.adapter.ReviewAdapter;
import es.cice.practicapedrogonzalez.json.AddressComponent;
import es.cice.practicapedrogonzalez.json.Aspect;
import es.cice.practicapedrogonzalez.json.DetalleSchema;
import es.cice.practicapedrogonzalez.json.Geometry;
import es.cice.practicapedrogonzalez.json.Period;
import es.cice.practicapedrogonzalez.json.Photo;
import es.cice.practicapedrogonzalez.json.ResultDetalle;
import es.cice.practicapedrogonzalez.json.Review;
import es.cice.practicapedrogonzalez.model.Galeria;
import es.cice.practicapedrogonzalez.model.Tools;

/*
* Muestro detalle
* Enlace a website
* Enlace a map activiy
*
* Pendiente menu action (guardar favorito)
*/

public class DetailActivity extends AppCompatActivity {
    private final static String TAG = "DetailActivity";
    private RecyclerView rvGaleriaFotos;
    private TextView tvDnombre;
    private TextView tvDrating;
    private RatingBar ratingBarDetalle;
    private TextView tvDdistance;
    private TextView tvDduration;
    private ImageView ivWeb, ivMapa;
    private TextView tvDvicinity;
    private TextView tvDphoneNumber;
    private TextView tvDopenNow;
    private TextView tvHeaderReviews;
    private RecyclerView rvReviews;
    private TextView tvDweekday1, tvDweekday2, tvDweekday3, tvDweekday4, tvDweekday5, tvDweekday6, tvDweekday7;
    private String itemSeleccionado, distance, duration;
    private String website;
    private Double latitud, longitud;
    private String nombre;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.e(TAG, "onCreate()");

        itemSeleccionado = getIntent().getExtras().getString("ItemSeleccionado");
        distance = getIntent().getExtras().getString("Distance");
        duration = getIntent().getExtras().getString("Duration");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        rvGaleriaFotos = (RecyclerView) findViewById(R.id.rvGaleriaFotos);
        tvDnombre = (TextView) findViewById(R.id.tvDnombre);
        tvDrating = (TextView) findViewById(R.id.tvDrating);
        ratingBarDetalle = (RatingBar) findViewById(R.id.ratingBarDetalle);
        tvDdistance = (TextView) findViewById(R.id.tvDdistance);
        tvDduration = (TextView) findViewById(R.id.tvDduration);
        ivWeb = (ImageView) findViewById(R.id.ivWeb);
        ivMapa = (ImageView) findViewById(R.id.ivMapa);
        tvDvicinity = (TextView) findViewById(R.id.tvDvicinity);
        tvDphoneNumber = (TextView) findViewById(R.id.tvDphoneNumer);
        tvDopenNow = (TextView) findViewById(R.id.tvDopenNow);
        tvHeaderReviews = (TextView) findViewById(R.id.tvHeaderReviews);

        tvDweekday1 = (TextView) findViewById(R.id.tvDweekday1);
        tvDweekday2 = (TextView) findViewById(R.id.tvDweekday2);
        tvDweekday3 = (TextView) findViewById(R.id.tvDweekday3);
        tvDweekday4 = (TextView) findViewById(R.id.tvDweekday4);
        tvDweekday5 = (TextView) findViewById(R.id.tvDweekday5);
        tvDweekday6 = (TextView) findViewById(R.id.tvDweekday6);
        tvDweekday7 = (TextView) findViewById(R.id.tvDweekday7);

        rvReviews = (RecyclerView) findViewById(R.id.rvReviews);

        if (Tools.isNetworkAvailable(this)) {
            new TaskDetalleData().execute();
        } else {
            Log.e(TAG, "- Network is not available");
        }

        ivWeb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebsiteActivity.class);
                intent.putExtra("WebSite", website);
                startActivity(intent);
            }
        });

        ivMapa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsDetalleActivity.class);
                intent.putExtra("Latitud", latitud);
                intent.putExtra("Longitud", longitud);
                intent.putExtra("Nombre", nombre);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected()");

        if(item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public class TaskDetalleData extends AsyncTask<Void, Void, Boolean> {
        private final static String DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
        private final static String PLACE_URL = "https://maps.googleapis.com/maps/api/place/";
        private final static String PHOTO_URL = "photo?";
        private final static String KEY = "AIzaSyAvd0kOh8DqLuQ-9OcB7CClXnIIP8Ju6OA";
        //private final static String KEY = "AIzaSyCH1rsLE7uV8N-6xZJ-PicWWarqDV2w1JU";
        private final static int MAX_HEIGHT = 400;
        private final static int MAX_WIDTH = 400;
        private DetalleSchema detalleSchema;
        private List<Galeria> galeria;
        private List<Review> reviewList;
        private Float rating;
        private String telefono;
        private String openNow;
        private List<String> weekdayText = null;
        private String errorMsg = "OK";

        @Override
        protected Boolean doInBackground(Void... items) {
            Log.e(TAG, "- doInBackground()...");
            String statusJSON;
            HashMap<String, String> param;
            int maxWidth, maxHeight;
            String photoreference;
            Bitmap imgFoto = null;

            try {
                param = new HashMap<String, String>();
                param.put("key", KEY);
                param.put("placeid", itemSeleccionado);
                param.put("language", Locale.getDefault().getLanguage());



                String responseJSON = Tools.getGoogleApiData(DETAILS_URL, param);
                //String responseJSON = datosDetallePrueba();
                Log.e(TAG, "- responseJSON : " + responseJSON);




                if (responseJSON == null || responseJSON.isEmpty()) {
                    Log.e(TAG, "- Error en responseJSON");
                    errorMsg = "Error in Google Place";
                    return false;
                }

                Gson gson = new Gson();
                detalleSchema = gson.fromJson(responseJSON, DetalleSchema.class);

                // comprobar Status dentro de respuesta
                statusJSON = detalleSchema.getStatus();

                if(!statusJSON.contains("OK")){
                    Log.e(TAG, "- statusJSON : " + statusJSON);
                    errorMsg = statusJSON;
                    return false;
                }

                if(detalleSchema.getResultDetalle() == null){
                    Log.e(TAG, "- No resultDetalle available");
                    errorMsg = "Error in Google Place";
                    return false;
                }

                ResultDetalle resultDetalle = detalleSchema.getResultDetalle();
                website = resultDetalle.getWebsite();
                latitud = resultDetalle.getGeometry().getLocation().getLat();
                longitud = resultDetalle.getGeometry().getLocation().getLng();
                nombre = resultDetalle.getName();

                if (resultDetalle.getPhotos() != null){
                    List<Photo> photos = resultDetalle.getPhotos();
                    galeria = new ArrayList<>();
                    param = new HashMap<String, String>();

                    for(int i=0; i<photos.size(); i++){
                        maxWidth = photos.get(i).getWidth();
                        maxHeight = photos.get(i).getHeight();

                        if(maxWidth < 0 || maxWidth > MAX_WIDTH){
                            maxWidth = MAX_WIDTH;
                        }

                        if(maxHeight < 0 || maxHeight > MAX_HEIGHT){
                            maxHeight = MAX_HEIGHT;
                        }

                        photoreference = photos.get(i).getPhotoReference();
                        param.put("key", KEY);
                        param.put("photoreference", photoreference);
                        param.put("maxwidth", String.valueOf(maxWidth));
                        param.put("maxheight", String.valueOf(maxHeight));
                        imgFoto = Tools.getGooglePlacePhoto(PLACE_URL + PHOTO_URL, param);

                        if (imgFoto != null){
                            galeria.add(new Galeria(imgFoto));
                        }
                    }
                }

                if(resultDetalle.getReviews() != null){
                    reviewList = resultDetalle.getReviews();
                    String photoUrl;
                    byte[] byteImg = null;
                    Bitmap foto;

                    for(int i=0; i<reviewList.size(); i++){
                        photoUrl = reviewList.get(i).getProfilePhotoUrl();
                        byteImg = Tools.getInternetBitmap(photoUrl);
                        foto = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
                        reviewList.get(i).setProfilePhoto(foto);
                    }
                }else{
                    Log.e(TAG, "- resultDetalle.getReviews() != null");
                    reviewList = null;
                }

                rating = resultDetalle.getRating();
                telefono = resultDetalle.getFormattedPhoneNumber();

                if(resultDetalle.getOpeningHours() != null){
                    if(resultDetalle.getOpeningHours().getOpenNow() == true){
                        openNow = context.getResources().getString(R.string.open);
                    }else{
                        openNow = context.getResources().getString(R.string.closed);
                    }
                }else{
                    openNow = context.getResources().getString(R.string.closed);
                }

                if(resultDetalle.getOpeningHours().getWeekdayText() != null){
                    weekdayText = resultDetalle.getOpeningHours().getWeekdayText();
                }

            }catch (Exception e){
                Log.e(TAG, "- Exception");
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            Log.e(TAG, "- onPostExecute()...");
            Log.e(TAG, "- status : " + status);

            if(status){
                GaleriaAdapter gAdapter = new GaleriaAdapter(galeria);
                rvGaleriaFotos.setAdapter(gAdapter);
                rvGaleriaFotos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

                ResultDetalle resultDetalle = detalleSchema.getResultDetalle();
                tvDnombre.setText(nombre);
                tvDrating.setText(String.valueOf(rating));
                ratingBarDetalle.setRating(resultDetalle.getRating());
                tvDdistance.setText(distance);
                tvDduration.setText(duration);
                tvDvicinity.setText(resultDetalle.getVicinity());
                tvDphoneNumber.setText(telefono);
                tvDopenNow.setText(openNow);
                tvHeaderReviews.setText(getResources().getString(R.string.reviews));

                if(weekdayText != null){
                    tvDweekday1.setText(weekdayText.get(0));
                    tvDweekday2.setText(weekdayText.get(1));
                    tvDweekday3.setText(weekdayText.get(2));
                    tvDweekday4.setText(weekdayText.get(3));
                    tvDweekday5.setText(weekdayText.get(4));
                    tvDweekday6.setText(weekdayText.get(5));
                    tvDweekday7.setText(weekdayText.get(6));
                }

                if(resultDetalle.getReviews() != null){
                    ReviewAdapter rAdapter = new ReviewAdapter(resultDetalle.getReviews());
                    rvReviews.setNestedScrollingEnabled(false);
                    rvReviews.setAdapter(rAdapter);
                    rvReviews.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                }
            }else{
                Log.e(TAG, "- Status Asynctask = false");
                Intent intent = new Intent(context, ErrorActivity.class);
                intent.putExtra("errorMsg", errorMsg);
                startActivity(intent);
            }
        }

        private String datosDetallePrueba() {
            return "{\n" +
                    "   \"html_attributions\" : [],\n" +
                    "   \"result\" : {\n" +
                    "      \"address_components\" : [\n" +
                    "         {\n" +
                    "            \"long_name\" : \"Local 6\",\n" +
                    "            \"short_name\" : \"Local 6\",\n" +
                    "            \"types\" : [ \"floor\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"13A\",\n" +
                    "            \"short_name\" : \"13A\",\n" +
                    "            \"types\" : [ \"street_number\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"Paseo Tierra de Melide\",\n" +
                    "            \"short_name\" : \"Paseo Tierra de Melide\",\n" +
                    "            \"types\" : [ \"route\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"Madrid\",\n" +
                    "            \"short_name\" : \"Madrid\",\n" +
                    "            \"types\" : [ \"locality\", \"political\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"Madrid\",\n" +
                    "            \"short_name\" : \"M\",\n" +
                    "            \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"Comunidad de Madrid\",\n" +
                    "            \"short_name\" : \"Comunidad de Madrid\",\n" +
                    "            \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"España\",\n" +
                    "            \"short_name\" : \"ES\",\n" +
                    "            \"types\" : [ \"country\", \"political\" ]\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"long_name\" : \"28050\",\n" +
                    "            \"short_name\" : \"28050\",\n" +
                    "            \"types\" : [ \"postal_code\" ]\n" +
                    "         }\n" +
                    "      ],\n" +
                    "      \"adr_address\" : \"Local 6, \\u003cspan class=\\\"street-address\\\"\\u003ePaseo Tierra de Melide, 13A\\u003c/span\\u003e, \\u003cspan class=\\\"postal-code\\\"\\u003e28050\\u003c/span\\u003e \\u003cspan class=\\\"locality\\\"\\u003eMadrid\\u003c/span\\u003e, \\u003cspan class=\\\"country-name\\\"\\u003eEspaña\\u003c/span\\u003e\",\n" +
                    "      \"formatted_address\" : \"Local 6, Paseo Tierra de Melide, 13A, 28050 Madrid, España\",\n" +
                    "      \"formatted_phone_number\" : \"912 87 62 72\",\n" +
                    "      \"geometry\" : {\n" +
                    "         \"location\" : {\n" +
                    "            \"lat\" : 40.5068355,\n" +
                    "            \"lng\" : -3.6643151\n" +
                    "         },\n" +
                    "         \"viewport\" : {\n" +
                    "            \"northeast\" : {\n" +
                    "               \"lat\" : 40.5082340302915,\n" +
                    "               \"lng\" : -3.662961019708497\n" +
                    "            },\n" +
                    "            \"southwest\" : {\n" +
                    "               \"lat\" : 40.5055360697085,\n" +
                    "               \"lng\" : -3.665658980291501\n" +
                    "            }\n" +
                    "         }\n" +
                    "      },\n" +
                    "      \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "      \"id\" : \"e2e8797d29e5322c2e7031db295e614efc166cd8\",\n" +
                    "      \"international_phone_number\" : \"+34 912 87 62 72\",\n" +
                    "      \"name\" : \"Maye's Bistró\",\n" +
                    "      \"opening_hours\" : {\n" +
                    "         \"open_now\" : false,\n" +
                    "         \"periods\" : [\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 1,\n" +
                    "                  \"time\" : \"1600\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 1,\n" +
                    "                  \"time\" : \"1300\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 1,\n" +
                    "                  \"time\" : \"2300\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 1,\n" +
                    "                  \"time\" : \"2000\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 2,\n" +
                    "                  \"time\" : \"1600\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 2,\n" +
                    "                  \"time\" : \"1300\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 2,\n" +
                    "                  \"time\" : \"2300\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 2,\n" +
                    "                  \"time\" : \"2000\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 3,\n" +
                    "                  \"time\" : \"1600\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 3,\n" +
                    "                  \"time\" : \"1300\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 3,\n" +
                    "                  \"time\" : \"2300\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 3,\n" +
                    "                  \"time\" : \"2000\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 4,\n" +
                    "                  \"time\" : \"1600\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 4,\n" +
                    "                  \"time\" : \"1300\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 4,\n" +
                    "                  \"time\" : \"2300\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 4,\n" +
                    "                  \"time\" : \"2000\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 5,\n" +
                    "                  \"time\" : \"1630\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 5,\n" +
                    "                  \"time\" : \"1330\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 5,\n" +
                    "                  \"time\" : \"2330\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 5,\n" +
                    "                  \"time\" : \"2000\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 6,\n" +
                    "                  \"time\" : \"1630\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 6,\n" +
                    "                  \"time\" : \"1330\"\n" +
                    "               }\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"close\" : {\n" +
                    "                  \"day\" : 6,\n" +
                    "                  \"time\" : \"2330\"\n" +
                    "               },\n" +
                    "               \"open\" : {\n" +
                    "                  \"day\" : 6,\n" +
                    "                  \"time\" : \"2000\"\n" +
                    "               }\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"weekday_text\" : [\n" +
                    "            \"lunes: 13:00–16:00, 20:00–23:00\",\n" +
                    "            \"martes: 13:00–16:00, 20:00–23:00\",\n" +
                    "            \"miércoles: 13:00–16:00, 20:00–23:00\",\n" +
                    "            \"jueves: 13:00–16:00, 20:00–23:00\",\n" +
                    "            \"viernes: 13:30–16:30, 20:00–23:30\",\n" +
                    "            \"sábado: 13:30–16:30, 20:00–23:30\",\n" +
                    "            \"domingo: Cerrado\"\n" +
                    "         ]\n" +
                    "      },\n" +
                    "      \"photos\" : [\n" +
                    "         {\n" +
                    "            \"height\" : 2048,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115938105757261159840/photos\\\"\\u003eMaye&#39;s Bistró\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAEb9xwiM2VbPLTs1qewfUTdVU728xhQiT-puyjYzdVnSQwTAYq0GwgFxI-plQUWZFkydw74f_zGIaipw6m6oxmu9pT5q4-T8yyqnFalZ3UiA9K_-g4xRvTH0YsSff3oBkVf3pMsVEHgms_GhfN_ZXtJRnM9aa25gbMLmXm-65wV_EhDG4uZ3UJDEw5XfPE-dmiyBGhRHKKVEimWQPDlaPIzBJRDR6WDAmQ\",\n" +
                    "            \"width\" : 2047\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 2592,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/103142665758189320072/photos\\\"\\u003eA. Alon\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAPp28XZaWhc_FiOKWYGj9S7LvclCFHo3aWgqdOSlQrLrCZlfNjgBPf9zUsSTxlIzSn7oiDPvff6CNrUeOTgwWoebB496_yEnrZhFue6uFsamlS6GQaoJOyolDAz2A6scCZIJJV4psODkNlzP78vxVIl-kRUfE1Vuk706vq2jRK7wEhBS1TuEJT2fMq7NRDYQ7Aa2GhSKRIrGvWUdCF6XU5MKZM_Bz31Gjw\",\n" +
                    "            \"width\" : 1456\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 2232,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115848348628868090666/photos\\\"\\u003eAdal Alom Rodríguez\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAL1VrSUstzzk9OHcWRvtq7qmmpOmixq-B5E8BNS4dBGrd6wpjLrBT8mFGTIgV3LpJWoMQs2baB1DA5eZ5JYwKiYCFlKoaqY6n2g_aP8fTtQu9-IDK9NDMx3BdiZIHvHtkT78ntsw7kszkMzvkdeIQhJ9yOjIHJtg2KEML1H_uAOXEhBx_XHrkmaIgAgTfeTpnIKeGhRWfqGVIGR_UNa3iY9N7d3DHNBczg\",\n" +
                    "            \"width\" : 4032\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 3264,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117405853525842067862/photos\\\"\\u003eClaudia Rodriguez\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAABu_Z0SmqE_Dq_KAaXgs6kPNjadM2CV-F3v-SjxTi-qY7gBDzzIZh_cXQ1Gf2d4Sfsw3Bblc_85aCvjASVxuN1CIgpRKGHud1kqpAKmTazKhOZ6GzWSo5gHJ3hLy2IekgKOXuMr2SP3XXFrPGLcCSbApSiU7p5nTKBMUlbzwU0qZEhBYuIhQJiZ0lEmeDC4r__8zGhSPaFaWVN0b4MXIL1T6u_MMecjcAQ\",\n" +
                    "            \"width\" : 2448\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 2048,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/103221465295319900785/photos\\\"\\u003eMiguel Angel Gallego Preciado\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAANRzepOMsU9fgwwezyUNgdf2d80Vi1nqyKvPT-EzhRlOGIDTMWJrKAskrFz7nnenpe6fNS_CnVf4jfdMtpmIBDnL0x7MnK8yEtSf83hbQAqeG1OQAx54ZAmfE3p4Eiu8Nr_425vGquQJzPVn914HfiSsyDDZHmoo_f3sX4nhsgtZEhBQRcQglPfFYb9IG6_Dp9lpGhTOi4JByijRq5uSeSikj1kheCli8g\",\n" +
                    "            \"width\" : 1536\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 3120,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/106246426944103639050/photos\\\"\\u003eJoel Reis\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAACb5FptGN2Zxj1pwuh2Nusn5VMsAE12xQEYN3Y2pfjOqoW9d_jsqq-wbo8kfKYaNvp-MKJMiewMR6VSMFmCWhZsZi9g6K7NGwsS_2sgikL0WNiQAD-KYed3-IOwNXGOsoxfB3zG1PEITgNE8ptxBF8vLtU92cCR4mPM7-YowqNeBEhBfOlSVkc92cC78qLIQWCr1GhQ3qv_0BU5hG6fg-nbTfnDwpP9nNQ\",\n" +
                    "            \"width\" : 4160\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 4160,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101262207714769104588/photos\\\"\\u003eIsaías EB\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAJpeKWCsHauxmhOc254kU3s9t8fq3DZ8MkTGs7GJoUx3pfmCgDvVoBoqaoE1Jec16jZiUP2MhtkNW3iE8RWxk-Kq-7kHkH2xGDlnIww9dne_Ti5uli2_EQoxxXJyaBdQLMD5LvrWwlAg9dGBboeNjGZsFkXLq2PT8Yn1HEN761YDEhBDvzYDWUzLo7tVgdF8yoozGhSAH_ZMpQK8vXMve3UMSSXUNcSy9A\",\n" +
                    "            \"width\" : 3120\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 3264,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117405853525842067862/photos\\\"\\u003eClaudia Rodriguez\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAPFdn_2AEjaldzL8jL6wCFVt6u_o74r84_kkb_heOoq8h32xLEeWbD811PJkBJ7Qv4GbbvwNcWGuQb7tbAyIYzBemoG2jL3Z7S1xv8Ht832oF4ltLnD7aNYfwxgo9nmYaROpjLi8cK7xTO7VThTLooX-DDQNrGJXBh17fLcA38ttEhDbuIXYAxSDj3u_b7H0kKKQGhRVKREX9RhHZHyCGN6jYfHKBYL1Sw\",\n" +
                    "            \"width\" : 2448\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 3088,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/102297760495097566769/photos\\\"\\u003eBorja Eugui\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAC51bRMyN-lHBki2Js7Xw7g9HJIuJ0BCrGZjqHWOCHHOKfKtQ4m0oAb5jcCPmJG7glCWsTM-gYf0afP2wiew-HlF6X8D3zSLjBbr6HQ36njcXsrlucdPXHBmbfN2AMa8XDwm5Y4pilCjhrlbFp_OciETIHCzuWAFsHQVYva9dXEZEhDDD5LeZuqOykJJ872N2sNzGhQ3tJLXeoD69qOPdmAfDk9YMiTwOQ\",\n" +
                    "            \"width\" : 4160\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"height\" : 3264,\n" +
                    "            \"html_attributions\" : [\n" +
                    "               \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/102172841595249717885/photos\\\"\\u003eAristides Artiñano\\u003c/a\\u003e\"\n" +
                    "            ],\n" +
                    "            \"photo_reference\" : \"CoQBdwAAAAOkRbzRsG7_JnkOMLxBHduBilg4xI34D05lSD1__0OOMTvEBBMZLn1dHAtdWAN-DXx26Er71wNc7Hf3l--xtdb8zEQjPlg7Zvqo0O5x2mPBv34kj9w7ZSGPTbKXJ8j6qLn2PqihVu_bCbZliJJjJO1GwbjXfBY4Lt6NiiiHQPT5EhCe0cm8N8NIQaHI576txVX2GhT6D9Vuhx0By3ho6wGPFtnYWjuqAg\",\n" +
                    "            \"width\" : 2448\n" +
                    "         }\n" +
                    "      ],\n" +
                    "      \"place_id\" : \"ChIJ7_mKofcrQg0RIvDVTh0S0EY\",\n" +
                    "      \"rating\" : 4.5,\n" +
                    "      \"reference\" : \"CmRRAAAARLoIQ6XVmH1DpzB-oPmYwlcnyMfQ7x8MLzBFVQeFK8JBPLEZBnGWCP890690yOXU7sXy9BfKZsxxeKeUOUURNYMQCDnu5rMsvpiv7NirtQzkN9EqfwFJHDiR74T-_6abEhB63ULcW0SEgTLHp_nWuYKZGhTyC-l8vUADm96Sxz3tu_NFkYEshA\",\n" +
                    "      \"reviews\" : [\n" +
                    "         {\n" +
                    "            \"aspects\" : [\n" +
                    "               {\n" +
                    "                  \"rating\" : 3,\n" +
                    "                  \"type\" : \"overall\"\n" +
                    "               }\n" +
                    "            ],\n" +
                    "            \"author_name\" : \"Karen Mkhitaryan\",\n" +
                    "            \"author_url\" : \"https://www.google.com/maps/contrib/114764025391279620615/reviews\",\n" +
                    "            \"language\" : \"es\",\n" +
                    "            \"profile_photo_url\" : \"https://lh4.googleusercontent.com/-r3EPYo2hlA4/AAAAAAAAAAI/AAAAAAAAASg/zZ6Xdpawvso/s128-c0x00000000-cc-rp-mo/photo.jpg\",\n" +
                    "            \"rating\" : 5,\n" +
                    "            \"relative_time_description\" : \"Hace 3 semanas\",\n" +
                    "            \"text\" : \"Un sitio fenomenal para cenar!! La comida muy rica con gran variación! La atención súper cálida y la gente muy maja! Todos los platos que hemos probado nos gustan! Siempre volvemos :)\",\n" +
                    "            \"time\" : 1490220049\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"aspects\" : [\n" +
                    "               {\n" +
                    "                  \"rating\" : 2,\n" +
                    "                  \"type\" : \"overall\"\n" +
                    "               }\n" +
                    "            ],\n" +
                    "            \"author_name\" : \"Ivan Sierra\",\n" +
                    "            \"author_url\" : \"https://www.google.com/maps/contrib/112489504107480036236/reviews\",\n" +
                    "            \"language\" : \"es\",\n" +
                    "            \"profile_photo_url\" : \"https://lh6.googleusercontent.com/-_K-QHI06Rks/AAAAAAAAAAI/AAAAAAAAId4/2i24vm0nCpo/s128-c0x00000000-cc-rp-mo-ba1/photo.jpg\",\n" +
                    "            \"rating\" : 4,\n" +
                    "            \"relative_time_description\" : \"Hace 3 meses\",\n" +
                    "            \"text\" : \"Uno de los mejores sitios para comer hamburguesas en las tablas. Son de buena calidad, muy jugosas y de diferentes sabores e ingredientes. La hamburguesa viene acompañada de patatas sin congelar o de bolsa por lo que se agradece. Si todavía puedes con más las tartas son de primera calidad, tarta de queso, tarta guiness, tarta de chocolate. El precio es asequible para la calidad. Es imprescindible reservar entre semana para las comidas\",\n" +
                    "            \"time\" : 1482273035\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"aspects\" : [\n" +
                    "               {\n" +
                    "                  \"rating\" : 3,\n" +
                    "                  \"type\" : \"overall\"\n" +
                    "               }\n" +
                    "            ],\n" +
                    "            \"author_name\" : \"Asi Omez\",\n" +
                    "            \"author_url\" : \"https://www.google.com/maps/contrib/102221529911955221932/reviews\",\n" +
                    "            \"language\" : \"es\",\n" +
                    "            \"profile_photo_url\" : \"https://lh4.googleusercontent.com/-52LSu_ifopE/AAAAAAAAAAI/AAAAAAAAAAA/AMcAYi9vZYl-ZJaGkXIBFZ7FM-UFQOp72g/s128-c0x00000000-cc-rp-mo/photo.jpg\",\n" +
                    "            \"rating\" : 5,\n" +
                    "            \"relative_time_description\" : \"Hace una semana\",\n" +
                    "            \"text\" : \"muy buena hamburguesa para los tiempos que corren, al igual que el servicio, pedí una salsa picante y me trajo una brutal!! volveré en mis siguientes visitas a las tablas. \",\n" +
                    "            \"time\" : 1491337074\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"aspects\" : [\n" +
                    "               {\n" +
                    "                  \"rating\" : 3,\n" +
                    "                  \"type\" : \"overall\"\n" +
                    "               }\n" +
                    "            ],\n" +
                    "            \"author_name\" : \"Juanjo Garcia Cabrera\",\n" +
                    "            \"author_url\" : \"https://www.google.com/maps/contrib/114196028292633656765/reviews\",\n" +
                    "            \"language\" : \"es\",\n" +
                    "            \"profile_photo_url\" : \"https://lh6.googleusercontent.com/-3nKjoQTg43g/AAAAAAAAAAI/AAAAAAAAEz4/k5ubKQ_rQ1E/s128-c0x00000000-cc-rp-mo-ba1/photo.jpg\",\n" +
                    "            \"rating\" : 5,\n" +
                    "            \"relative_time_description\" : \"Hace 3 semanas\",\n" +
                    "            \"text\" : \"La carne esta realmente buena, es cortada no picada. Merece la pena pedirla al punto y 250gr\",\n" +
                    "            \"time\" : 1489940468\n" +
                    "         },\n" +
                    "         {\n" +
                    "            \"aspects\" : [\n" +
                    "               {\n" +
                    "                  \"rating\" : 3,\n" +
                    "                  \"type\" : \"overall\"\n" +
                    "               }\n" +
                    "            ],\n" +
                    "            \"author_name\" : \"iube mend\",\n" +
                    "            \"author_url\" : \"https://www.google.com/maps/contrib/113791213483936232467/reviews\",\n" +
                    "            \"language\" : \"es\",\n" +
                    "            \"profile_photo_url\" : \"https://lh6.googleusercontent.com/-yQioD7MsApY/AAAAAAAAAAI/AAAAAAAAkb4/E2Y_JCUtz3E/s128-c0x00000000-cc-rp-mo/photo.jpg\",\n" +
                    "            \"rating\" : 5,\n" +
                    "            \"relative_time_description\" : \"Hace 1 mes\",\n" +
                    "            \"text\" : \"Primera visita y grata sorpresa! Pedimos Nachos con Pulled Pork riquisimos! Las hamburguesas con una gran variedad de ingredientes atipicos pero con unos resultados muy buenos y sorprendentes. \\nLa carne de gran calidad junto con un apetecible pan y buenos ingredientes consigue una mezcla que merece ser probada. Repetiremos por la comida y por el trato. \",\n" +
                    "            \"time\" : 1487528791\n" +
                    "         }\n" +
                    "      ],\n" +
                    "      \"scope\" : \"GOOGLE\",\n" +
                    "      \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "      \"url\" : \"https://maps.google.com/?cid=5102598294896767010\",\n" +
                    "      \"utc_offset\" : 120,\n" +
                    "      \"vicinity\" : \"Local 6, Paseo Tierra de Melide, 13A, Madrid\",\n" +
                    "      \"website\" : \"http://www.mayesbistro.es/\"\n" +
                    "   },\n" +
                    "   \"status\" : \"OK\"\n" +
                    "}";
        }
    }
}
