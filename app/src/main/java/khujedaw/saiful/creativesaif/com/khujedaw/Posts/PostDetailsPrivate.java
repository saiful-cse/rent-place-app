package khujedaw.saiful.creativesaif.com.khujedaw.Posts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MainActivity;
import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify.PhoneVerifyStep1;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class PostDetailsPrivate extends AppCompatActivity {


    Post post;
    private TextView  post_id,time, place_name, category, fee, place_address,
    description, name, email, phone, alt_phone;
    private Button edit, delete;

    ImageView place_photo, photo;

    private boolean isLoading = true;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details_private);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        setId();

        post = getIntent().getExtras().getParcelable("postDetails");

        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.post_photo_path)+post.getPlace_photo())
                .placeholder(R.drawable.ic_menu_gallery)
                .into(place_photo);

        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.user_photo_path)+post.getPhoto())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.profile_icon)
                .into(photo);

        post_id.setText("#Post ID: 000"+post.getPost_id());
        time.setText(post.getTime());
        place_name.setText(post.getPlace_name());
        category.setText(post.getCategory());
        fee.setText("BDT: "+post.getFee()+"/Monthly");
        place_address.setText(post.getPlace_address());
        description.setText(post.getDescription());
        name.setText(post.getName());
        email.setText(post.getEmail());
        phone.setText(post.getPhone());
        alt_phone.setText(post.getAlt_phone());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoading){
                    Intent i = new Intent(PostDetailsPrivate.this, PostEdit.class);
                    i.putExtra("postEdit",post);
                    startActivity(i);

                }else{

                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isNetworkConnected() && isLoading){
                    deleteDialog();

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection or Try again later.",Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    public void setId(){
        post_id = findViewById(R.id.tvDetPostId);
        time = findViewById(R.id.tvDetTime);
        place_name = findViewById(R.id.tvDetPlaceName);
        category = findViewById(R.id.tvDetCategory);
        fee = findViewById(R.id.tvDetFee);
        place_address = findViewById(R.id.tvDetAddress);
        description = findViewById(R.id.tvDetDescrip);
        place_photo = findViewById(R.id.imageViewPlacePhoto);
        photo = findViewById(R.id.imageViewUserImage);
        name = findViewById(R.id.tvDetName);
        email = findViewById(R.id.tvDetEmail);
        phone = findViewById(R.id.tvDetPhone);
        alt_phone = findViewById(R.id.tvDetAltPhone);

        edit = findViewById(R.id.btnEdit);
        delete = findViewById(R.id.btnDelete);

        progressBar = findViewById(R.id.progressBarDelete);
    }


    public void deletePost(){

        String url = getString(R.string.base_url)+getString(R.string.deletePost);
        isLoading = false;
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String message = jsonObject.getString("message");

                    if (message.equals("success")) {
                        Toast.makeText(PostDetailsPrivate.this, "Your post has been deleted.",Toast.LENGTH_SHORT).show();
                        finish();

                    }else{

                        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        isLoading = true;
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PostDetailsPrivate.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(PostDetailsPrivate.this, "Network Error!!",Toast.LENGTH_SHORT).show();
                isLoading = true;
                progressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("post_id", post.getPost_id());
                map.put("place_photo", post.getPlace_photo());
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(request);
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

    public void deleteDialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(true);
        aleart1.setTitle("Warning!!!");
        aleart1.setMessage("Are you sure want to permanently delete this post?");
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePost();
            }
        });

        aleart1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = aleart1.create();
        dlg.show();
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
