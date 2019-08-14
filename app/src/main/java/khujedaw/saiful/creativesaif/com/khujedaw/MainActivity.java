package khujedaw.saiful.creativesaif.com.khujedaw;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify.PhoneVerifyStep1;
import khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify.PhoneVerifyStep2;
import khujedaw.saiful.creativesaif.com.khujedaw.Policeies.Policies;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.AddPost;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.MyPosts;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.Post;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.PostAdapter;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.PostAdapterPublic;
import khujedaw.saiful.creativesaif.com.khujedaw.Posts.PostEdit;
import khujedaw.saiful.creativesaif.com.khujedaw.Search.Search;
import khujedaw.saiful.creativesaif.com.khujedaw.Search.Search_Location;
import khujedaw.saiful.creativesaif.com.khujedaw.User.ProfileView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences pref;

    //progressBar for more post load
    ProgressBar progressBar;

    //refresh posts
    SwipeRefreshLayout swipeRefreshLayout;
    String current_phone;

    // i will be loading 10 items per page or per load
    private static final String LOAD_LIMIT = "?limit=10";
    private static final String TAG_LAST_ID_KEY = "&last_id=";


    // last post id to be loaded from php page,
    private String lastPostId = "0"; // this will issued to php page, so no harm make it string

    // i need this variable to lock and unlock loading more
    // e.g i should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean isLoading = true;

    // initialize adapter and data structure here
    private PostAdapterPublic postAdapterPublic;
    private ArrayList<Post> postArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.post_refresh);
        progressBar = findViewById(R.id.progressBarMain);

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        //assign all objects to avoid nullPointerException
        postArrayList = new ArrayList<>();
        postAdapterPublic = new PostAdapterPublic(this,postArrayList);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
//if internet is connected, then posts is load from server
        if (isNetworkConnected()){

            loadPosts();

        }else{
            Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        //now set adapter to recyclerView
        recyclerView.setAdapter(postAdapterPublic);



        // here add a recyclerView listener, to listen to scrolling,
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //this is the ONLY method that we need, ignore the rest
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (isLoading && isNetworkConnected()) {
                            loadMorePosts();
                        }
                    }

                }
            }
        });

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkConnected() && isLoading){
                    loadPosts();

                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        //Drawer element
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation Header content
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        TextView name = hView.findViewById(R.id.nav_name);
        TextView email = hView.findViewById(R.id.nav_email);
        TextView phone = hView.findViewById(R.id.nav_phone);

        name.setText(pref.getString("name", null));
        email.setText(pref.getString("email",null));
        phone.setText(pref.getString("phone",null));

        View header = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) header.findViewById(R.id.imageView);

        Glide
                .with(this)
                .load( getString(R.string.base_url)+getString(R.string.user_photo_path)+pref.getString("photo", null))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //finish();
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home){
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, Search.class));
            return true;

        }else if(id == R.id.action_location){
            startActivity(new Intent(this, Search_Location.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //startActivity(new Intent(this, PhoneVerifyStep1.class));
            //startActivity(new Intent(this, UserRegistration.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileView.class));

        } else if (id == R.id.nav_my_ads) {
            startActivity(new Intent(this, MyPosts.class));

        } else if (id == R.id.nav_add_ads) {
            startActivity(new Intent(this, AddPost.class));

        } else if (id == R.id.nav_comment_complaint) {
            startActivity(new Intent(this, Comment_Complaint.class));
        } else if(id == R.id.nav_policies){
            startActivity(new Intent(this, Policies.class));
        }
        else if (id == R.id.nav_developer) {
            startActivity(new Intent(this, About.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public void loadPosts(){

        swipeRefreshLayout.setRefreshing(true);
        isLoading = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        String url = getString(R.string.base_url)+getString(R.string.post_load)+LOAD_LIMIT;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    postArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");

                    if(m){

                        Toast.makeText(MainActivity.this,"No post found",Toast.LENGTH_SHORT).show();
                        isLoading = true;
                        swipeRefreshLayout.setRefreshing(false);

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

                            lastPostId = jsonObject1.getString("post_id");

                            //Notifying the adapter that data has been added or changed
                            postAdapterPublic.notifyDataSetChanged();
                        }

                        isLoading = true;
                        swipeRefreshLayout.setRefreshing(false);
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = true;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this,"Network Problem!!",Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void loadMorePosts(){

        progressBar.setVisibility(View.VISIBLE);
        isLoading = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        String url = getString(R.string.base_url)+getString(R.string.post_load)+LOAD_LIMIT+TAG_LAST_ID_KEY+lastPostId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");

                    if(m){

                        Toast.makeText(MainActivity.this,"No more post",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        isLoading = true;

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

                            lastPostId = jsonObject1.getString("post_id");

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
                Toast.makeText(MainActivity.this,"Network Error!!",Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }




    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}
