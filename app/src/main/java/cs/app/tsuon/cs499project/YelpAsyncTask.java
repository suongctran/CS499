package cs.app.tsuon.cs499project;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tsuon.cs499project.R;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class YelpAsyncTask extends AsyncTask<String, Void, String> {
    private YelpFusionApi yelpFusionApi;
    private YelpFusionApiFactory apiFactory;
    private Map<String, String> searchParam;
    private String term;
    private String location;
    private String sortBy;

    public YelpAsyncTask() {
        apiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = apiFactory.createAPI(String.valueOf(R.string.accessToken));
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchParam = new HashMap<String, String>();
    }

    @Override
    protected String doInBackground(String... params) {
        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(searchParam);
        Response<SearchResponse> response = null;
        try {
            Log.v("works", searchParam.get("location") + searchParam.get("sort_by") + searchParam.get("term"));
            response = call.execute();
            Log.v("works", "yes");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            Log.v("works", "yes2");
            SearchResponse business = response.body();
            String businessName = business.getBusinesses().get(0).getName().toString();
            Log.v("businessName", businessName);
            return businessName;
            //Double rating = business.getRating();
        }
        return null;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        searchParam.put("term", term);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        searchParam.put("location", location);
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        searchParam.put("sort_by", sortBy);
    }
}
