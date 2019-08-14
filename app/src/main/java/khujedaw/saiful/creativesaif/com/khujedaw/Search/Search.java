package khujedaw.saiful.creativesaif.com.khujedaw.Search;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MainActivity;
import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.Post;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.PostAdapterPublic;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class Search extends AppCompatActivity {

    Context context;

    //progressBar for more post load
    ProgressBar progressBar;

    private String string;


    // initialize adapter and data structure here
    private PostAdapterPublic postAdapterPublic;
    private ArrayList<Post> postArrayList;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Type location, price, category etc.");

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //Toast.makeText(Search.this, "You have search "+query,Toast.LENGTH_LONG).show();

                if (!isLoading){

                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(isNetworkConnected()){

                    loadSearchPosts(query);
                }
                else{

                    Snackbar.make(findViewById(android.R.id.content),"Please! Check Internet Connection.",Snackbar.LENGTH_LONG).show();
                    isLoading = true;
                    progressBar.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String tag) {
                // filter recycler view when text is changed
                //Toast.makeText(MainActivity.this, "You have search "+newText,Toast.LENGTH_LONG).show();

                return false;
            }
        });
        return true;
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


    //search
    public void loadSearchPosts(String key){

        progressBar.setVisibility(View.VISIBLE);
        isLoading = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free
        string = key.trim();

        String url = getString(R.string.base_url)+getString(R.string.search_post);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    postArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");

                    if(m){

                        Toast.makeText(Search.this,"No post found",Toast.LENGTH_SHORT).show();
                        isLoading = true;
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
                        isLoading = true;
                        progressBar.setVisibility(View.GONE);
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = true;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Search.this,"Network Error!!",Toast.LENGTH_LONG).show();
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
}
