package khujedaw.saiful.creativesaif.com.khujedaw;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.Posts.PostEdit;

public class Comment_Complaint extends AppCompatActivity {

    TextInputEditText edcomment;
    CardView cardViewCommentBtn;
    String comment, user_phone;
    ProgressBar progressBar;
    SharedPreferences pref;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment__complaint);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        edcomment = findViewById(R.id.comment);
        cardViewCommentBtn = findViewById(R.id.cardViewCommtSend);
        progressBar = findViewById(R.id.progressBarCmtPost);

        cardViewCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = edcomment.getText().toString().trim();
                user_phone = pref.getString("phone",null);
                if (comment.isEmpty()){
                    edcomment.setError("Type comment...");

                }else if(!isLoading){

                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(isNetworkConnected()){

                    add_comment();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection.",Snackbar.LENGTH_LONG).show();

                }
            }
        });
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

    public void add_comment(){
        progressBar.setVisibility(View.VISIBLE);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.comment_add);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("success")) {
                        Toast.makeText(Comment_Complaint.this, "Your comment has been sent.",Toast.LENGTH_SHORT).show();
                        finish();

                    }else{

                        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        isLoading = true;
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                isLoading = true;
                Toast.makeText(Comment_Complaint.this, "Network Error!!",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("user_phone", user_phone);
                map.put("comment", comment);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(request);
    }
}