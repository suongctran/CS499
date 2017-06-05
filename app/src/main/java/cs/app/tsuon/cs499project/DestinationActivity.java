package cs.app.tsuon.cs499project;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tsuon.cs499project.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.apache.commons.io.IOUtils;

public class DestinationActivity extends AppCompatActivity
        implements OnMapReadyCallback, Serializable {
    private Button backButton;
    private EditText destinationEditText;
    private Button searchButton;
    YelpFusionApi yelpFusionApi;
    YelpFusionApiFactory apiFactory;
    Map<String, String> searchParam;
    Map<String, YelpBusinesses> mapYelpBus;
    OkHttpClient client;
    List<YelpBusinesses> nYelpBus;
    private GoogleMap map;
    String destination;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        backButton = (Button) findViewById(R.id.backButton);
        destinationEditText = (EditText) findViewById(R.id.destinationEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
        client = new OkHttpClient();
        mapYelpBus = new HashMap<>();
        nYelpBus = getIntent().getParcelableArrayListExtra("yelpbus");
        destination = getIntent().getStringExtra("location");
        latitude = getIntent().getDoubleArrayExtra("latlng")[0];
        longitude = getIntent().getDoubleArrayExtra("latlng")[1];

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DestinationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        destinationEditText.setText(destination);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                apiFactory = new YelpFusionApiFactory();
                try {
                    yelpFusionApi = apiFactory.createAPI(getString(R.string.accessToken));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String destination = destinationEditText.getText().toString();
                if (destination.equals("") || destination == null) {
                    Toast.makeText(DestinationActivity.this, "Enter a destination!", Toast.LENGTH_SHORT).show();
                } else {
                    new DestinationAsyncTask().execute(destination);
                    new HTTPAsyncTask().execute(destination);
                }
            }
        });
    }

    public void addMapMarkers() {
        for(YelpBusinesses yb : nYelpBus) {
            double lat = yb.getLatitude();
            double lng = yb.getLongitude();
            String busType = yb.getBusType();
            LatLng latlng = new LatLng(lat, lng);
            float markerColor = 0;

            switch (busType) {
                case "Desserts":
                    markerColor = BitmapDescriptorFactory.HUE_BLUE;
                    break;
                case "Restaurants":
                    markerColor = BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case "Hotels":
                    markerColor = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case "Things to do":
                    markerColor = BitmapDescriptorFactory.HUE_ROSE;
                    break;
            }

            map.addMarker(new MarkerOptions().position(latlng)
                .icon(BitmapDescriptorFactory
                    .defaultMarker(markerColor))
                .title(yb.getName())
            );

            mapYelpBus.put(yb.getName(), yb);
        }
        Log.v("new3", String.valueOf(nYelpBus.size()));
        Log.v("new3", String.valueOf(mapYelpBus.size()));
    }
    @Override
    public void onMapReady(GoogleMap retMap) {
        map = retMap;
        addMapMarkers();
        map.getUiSettings().setZoomControlsEnabled(true);
        LatLng latlng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                .zoom(12)                   // Sets the zoom
                //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.v("new3", "click marker");
                Intent intent = new Intent(DestinationActivity.this, InfoActivity.class);
                Log.v("new3", "click marker b4 extra");
                //intent.putStringArrayListExtra("pics", (ArrayList) mapYelpBus.get(marker.getTitle()).getPictures());
                //intent.putParcelableArrayListExtra("yelpbus", (ArrayList<YelpBusinesses>) nYelpBus);
                Bundle b = new Bundle();
                b.putSerializable("YBClass", mapYelpBus.get(marker.getTitle()));
                intent.putExtras(b);
                Log.v("new3", "click marker after exta");
                startActivity(intent);
                return false;
            }
        });
    }

    class DestinationAsyncTask extends AsyncTask<String, YelpBusinesses, String> {
        List<YelpBusinesses> yelpBus;

        @Override
        protected void onProgressUpdate(YelpBusinesses... values) {
            super.onProgressUpdate(values);
            nYelpBus.add(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            String[] terms = new String[]{"Hotels", "Things to do", "Desserts", "Restaurants"};
            for (String t : terms) {
                searchParam = new HashMap<String, String>();
                searchParam.put("term", t);
                searchParam.put("location", params[0]);
                searchParam.put("sort_by", "best_match");
                searchParam.put("limit", "10");

                Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(searchParam);
                Response<SearchResponse> response = null;
                try {
                    response = call.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response != null) {
                    SearchResponse business = response.body();
                    List<Business> businessList = business.getBusinesses();
                    yelpBus = new ArrayList<>();
                    YelpBusinesses yb;
                    int i = 0;
                    for (Business b : businessList) {
                        yb = new YelpBusinesses(b.getName(), searchParam.get("term"), b.getUrl());
                        yb.setRating(b.getRating());
                        yb.setReviewCount(b.getReviewCount());
                        yb.setLatitude(b.getCoordinates().getLatitude());
                        yb.setLongitude(b.getCoordinates().getLongitude());
                        yelpBus.add(yb);
                        getPictures(yb, i);
                        i++;
                    }
                }
            }
            return null;
        }

        private void getPictures(YelpBusinesses yb, final int pos) {
            Request request = new Request.Builder()
                    .url(yb.getPicUrl())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    List<String> pictures = ImageParser.getPictures(response.body().string());
                    if(pictures.size() > 0) {
                        yelpBus.get(pos).setPictures(pictures);
                    }
                }
            });

            publishProgress(yelpBus.get(pos));
        }
    }

    class HTTPAsyncTask extends AsyncTask<String, String, String> {
        String urlLocation;

        @Override
        protected void onPostExecute(String s) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(DestinationActivity.this);
        }

        @Override
        protected String doInBackground(String... location) {
            String s = "";
            urlLocation = location[0].replaceAll(" ", "+");
            try {
                URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="
                        + urlLocation + "&sensor=true");
                URLConnection conn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                IOUtils.copy(in, output);
                s = output.toString();

                JSONObject jObj = new JSONObject(s);
                String Status = jObj.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray results = jObj.getJSONArray("results");
                    JSONObject item = results.getJSONObject(0);//read first item of results
                    JSONObject geometry = item.getJSONObject("geometry");//location is inside geometry object
                    JSONObject loc = geometry.getJSONObject("location");
                    latitude = loc.getDouble("lat");
                    longitude = loc.getDouble("lng");
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
