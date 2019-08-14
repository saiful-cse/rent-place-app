package khujedaw.saiful.creativesaif.com.khujedaw;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;
import khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify.PhoneVerifyStep1;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences pref;
    String current_phone;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide(); //hide action bar
        Fabric.with(this, new Crashlytics());

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        current_phone = pref.getString("phone", null);
        progressBar = findViewById(R.id.progressBarSplash);


        if (isNetworkConnected()){
            //calling a function for Load post
            getProfileInfo(current_phone);

        }else{
            aleartDialog("Please!! make sure internet connection");
            //Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();

        }

    }

    //get profile info
    public void getProfileInfo(String phone){

        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.base_url)+getString(R.string.getUserInfo)+phone;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean message = jsonObject.has("message");
                    if (message) {
                        finish();
                        startActivity(new Intent(SplashScreen.this, PhoneVerifyStep1.class));

                    }else{

                        pref.edit().putString("phone",jsonObject.getString("phone")).apply();
                        pref.edit().putString("name",jsonObject.getString("name")).apply();
                        pref.edit().putString("email",jsonObject.getString("email")).apply();
                        pref.edit().putString("photo",jsonObject.getString("photo")).apply();
                        pref.edit().putString("address",jsonObject.getString("address")).apply();

                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashScreen.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                progressBar.setVisibility(View.GONE);
                aleartDialog("Something went wrong!! Try again later.");
                //Toast.makeText(SplashScreen.this, "Network Error!!",Toast.LENGTH_SHORT).show();

            }
        });
        MySingleton.getInstance().addToRequestQueue(request);
    }


    public void aleartDialog(String message){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setTitle("Warning!");
        aleart1.setMessage(message);
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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
