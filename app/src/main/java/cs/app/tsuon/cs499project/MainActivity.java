package cs.app.tsuon.cs499project;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tsuon.cs499project.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button favoritesButton;
    private ImageView profilepicImageView;
    private Button bookmarkedButton;
    private EditText destinationEditText;
    private Button searchButton;
    private ProgressBar loading;
    boolean waiting = false;
    YelpFusionApi yelpFusionApi;
    YelpFusionApiFactory apiFactory;
    Map<String, String> searchParam;
    OkHttpClient client;
    List<YelpBusinesses> nYelpBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoritesButton = (Button) findViewById(R.id.favoritesButton);
        bookmarkedButton = (Button) findViewById(R.id.bookmarkedButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        destinationEditText = (EditText) findViewById(R.id.destinationEditText);
        loading = (ProgressBar) findViewById(R.id.loading);
        client = new OkHttpClient();
        nYelpBus = new ArrayList<>();

        favoritesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        bookmarkedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

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
                    Toast.makeText(MainActivity.this, "Enter a location!", Toast.LENGTH_SHORT).show();
                } else {
                    waitToLoad(true);
                    new DestinationAsyncTask().execute(destination);
                    new HTTPAsyncTask().execute(destination);
                }
            }
        });
    }

    synchronized public void waitToLoad(boolean stillWait) {
        if (stillWait) {
            waiting = true;
            loading.setVisibility(View.VISIBLE);
        } else {
            waiting = false;
            loading.setVisibility(View.INVISIBLE);
        }
    }

    class HTTPAsyncTask extends AsyncTask<String, String, String> {
        String urlLocation;
        double latitude, longitude;

        @Override
        protected void onPostExecute(String s) {
            double[] latlng = new double[] {latitude, longitude};
            Intent intent = new Intent(MainActivity.this, DestinationActivity.class);
            intent.putParcelableArrayListExtra("yelpbus", (ArrayList<YelpBusinesses>) nYelpBus);
            intent.putExtra("location", destinationEditText.getText().toString());
            intent.putExtra("latlng", latlng);
            startActivity(intent);
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
                    //yelpBus = new ArrayList<>();
                    YelpBusinesses yb;
                    int i = 0;
                    for (Business b : businessList) {
                        yb = new YelpBusinesses(b.getName(), searchParam.get("term"), b.getUrl());
                        yb.setRating(b.getRating());
                        yb.setReviewCount(b.getReviewCount());
                        yb.setLatitude(b.getCoordinates().getLatitude());
                        yb.setLongitude(b.getCoordinates().getLongitude());
                        //yelpBus.add(yb);
                        getPictures(yb);//, i);
                        i++;
                    }
                }
            }
            Log.v("new1", "1");
            return null;
        }

        private void getPictures(final YelpBusinesses yb) {//, final int pos) {
            Request request = new Request.Builder()
                    .url(yb.getPicUrl())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    //Log.v("new7", yb.getPicUrl());
                    List<String> pictures = ImageParser.getPictures(response.body().string());
                    if(pictures.size() > 0) {
                        yb.setPictures(pictures);
                    }
                }
            });
            publishProgress(yb);
        }
    }
}