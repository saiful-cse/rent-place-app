package khujedaw.saiful.creativesaif.com.khujedaw.Search;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MainActivity;
import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.Post;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.PostAdapterPublic;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class Search_Location extends AppCompatActivity implements LocationListener {

    //progressBar for more post load
    ProgressBar progressBar;

    private String string;

    // i need this variable to lock and unlock loading more
    // e.g i should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    boolean itShouldLoad = true;

    // initialize adapter and data structure here
    private PostAdapterPublic postAdapterPublic;
    private ArrayList<Post> postArrayList;

    //for location search
    public double latitude;
    public double longitude;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBarMain);

        //assign all objects to avoid nullPointerException
        postArrayList = new ArrayList<>();
        postAdapterPublic = new PostAdapterPublic(this,postArrayList);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(postAdapterPublic);

        //initializing
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationPermisson();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //location
    //Runtime permission confirmation
    public void locationPermisson(){
        Permissions.check(this/*context*/, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                if(isNetworkConnected()){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,Search_Location.this);
                    progressBar.setVisibility(View.VISIBLE);

                }else{

                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();
                    itShouldLoad = true;
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    //search
    public void loadSearchPosts(String key){

        progressBar.setVisibility(View.VISIBLE);
        itShouldLoad = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free
        string = key.trim();

        //String url = getString(R.string.base_url)+getString(R.string.search_post)+keywords;
        String url = getString(R.string.base_url)+getString(R.string.search_post);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    postArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");

                    if(m){

                        Toast.makeText(Search_Location.this,"No post found",Toast.LENGTH_SHORT).show();
                        itShouldLoad = true;
                        progressBar.setVisibility(View.GONE);

                    }else{

                        JSONArray jsonArrayData = jsonObject.getJSONArray("posts");

                        for (int i=0; i<jsonArrayData.length(); i++){

                            Post post = new Post();

                            JSONObject jsonObject1 = jsonArrayData.getJSONObject(i);

                            //post data
                            post.setPost_id(jsonObject1.getString("post_id"));
                            post.setTime(jsonObject1.getString("created_at"));
                            post.setPhone(jsonObject1.getString("user_phone"));
                            post.setAlt_phone(jsonObject1.getString("alt_phone"));
                            post.setCategory(jsonObject1.getString("category"));
                            post.setPlace_name(jsonObject1.getString("place_name"));
                            post.setFee(jsonObject1.getString("fee"));
                            post.setPlace_address(jsonObject1.getString("place_address"));
                            post.setDescription(jsonObject1.getString("description"));
                            post.setPlace_photo(jsonObject1.getString("place_photo"));
                            //user data
                            post.setName(jsonObject1.getString("name"));
                            post.setEmail(jsonObject1.getString("email"));
                            post.setPhoto(jsonObject1.getString("photo"));

                            postArrayList.add(post);

                            //Notifying the adapter that data has been added or changed
                            postAdapterPublic.notifyDataSetChanged();
                        }
                        itShouldLoad = true;
                        progressBar.setVisibility(View.GONE);
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                itShouldLoad = true;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Search_Location.this,"Network Error!!",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("s", string);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onLocationChanged(Location location) {

        //Hey, a non null location! Sweet!

        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        String info = gettingLocationInfo(latitude,longitude);

        if(isNetworkConnected() && itShouldLoad){
            loadSearchPosts(info);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // getting info from lat long.
    private String gettingLocationInfo(double lat, double lon){
        String info = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(lat, lon, 10);

            if (addresses.size() > 0){

                for (Address adr: addresses){

                    if (adr.getLocality() != null && adr.getLocality().length() > 0){

                        info = adr.getLocality()+" "+adr.getSubLocality()+" "+adr.getSubAdminArea()+"";

                        //+adr.getSubLocality()+" "++" "+adr.getSubAdminArea()
                        break;
                    }
                }

            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return info;
    }
}
