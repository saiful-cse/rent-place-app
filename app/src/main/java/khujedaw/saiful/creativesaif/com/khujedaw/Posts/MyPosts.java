package khujedaw.saiful.creativesaif.com.khujedaw.Posts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.R;
import khujedaw.saiful.creativesaif.com.khujedaw.User.UserRegistration;

public class MyPosts extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayoutMyPosts;
    SharedPreferences pref;

    //Creating a list of posts
    private List<Post> postList;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        //Initializing Views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing post list
        postList = new ArrayList<>();

        //Initializing adapter
        adapter = new PostAdapter(this, postList);

        //Adding adapter to recyclerView
        recyclerView.setAdapter(adapter);

        //ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        swipeRefreshLayoutMyPosts = findViewById(R.id.swipe_refresh_myposts);

        swipeRefreshLayoutMyPosts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(!isLoading){

                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }
                else if (isNetworkConnected()){

                    myPostLoad(pref.getString("phone",null));

                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
                    isLoading = true;
                    swipeRefreshLayoutMyPosts.setRefreshing(false);
                }

            }
        });

        if (isNetworkConnected()){
            myPostLoad(pref.getString("phone",null));
        }else{
            Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
        }

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        FloatingActionButton fab = (findViewById(R.id.fab));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPosts.this, AddPost.class));
            }
        });
    }


    public void myPostLoad(String myPhone){
        swipeRefreshLayoutMyPosts.setRefreshing(true);
        isLoading = false;

        String url = getString(R.string.base_url)+getString(R.string.myPostLoad)+myPhone;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    postList.clear();
                    //JSONObject jsonObject = new JSONObject(response);

                    boolean m = response.has("message");

                    if(m){
                        Toast.makeText(MyPosts.this,"No post found",Toast.LENGTH_SHORT).show();

                    }else{

                        JSONArray array = response.getJSONArray("posts");

                        for (int i=0; i<array.length(); i++){

                            Post post = new Post();

                            // Get current json object
                            JSONObject post_item = array.getJSONObject(i);

                            //post data
                            post.setPost_id(post_item.getString("post_id"));
                            post.setTime(post_item.getString("created_at"));
                            post.setPhone(post_item.getString("user_phone"));
                            post.setAlt_phone(post_item.getString("alt_phone"));
                            post.setCategory(post_item.getString("category"));
                            post.setPlace_name(post_item.getString("place_name"));
                            post.setFee(post_item.getString("fee"));
                            post.setPlace_address(post_item.getString("place_address"));
                            post.setDescription(post_item.getString("description"));
                            post.setPlace_photo(post_item.getString("place_photo"));
                            //user data
                            post.setName(post_item.getString("name"));
                            post.setEmail(post_item.getString("email"));
                            post.setPhoto(post_item.getString("photo"));
                            postList.add(post);

                        }
                    }
                    isLoading = true;
                    swipeRefreshLayoutMyPosts.setRefreshing(false);

                }catch (JSONException e){
                    e.printStackTrace();
                }


                //Notifying the adapter that data has been added or changed
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayoutMyPosts.setRefreshing(false);
                isLoading = true;
                Toast.makeText(MyPosts.this,"Network Problem!!",Toast.LENGTH_LONG).show();
            }
        });
        //MySingleton.getInstance().addToRequestQueue(jsonObjectRequest);
        requestQueue.add(jsonObjectRequest);
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

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
