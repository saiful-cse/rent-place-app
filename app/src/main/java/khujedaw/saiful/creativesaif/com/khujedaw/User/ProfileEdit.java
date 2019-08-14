package khujedaw.saiful.creativesaif.com.khujedaw.User;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MainActivity;
import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class ProfileEdit extends AppCompatActivity {

    TextInputEditText editTextName,editTextEmail, editTextAddress;
    SharedPreferences pref;
    ProgressBar progressBar;
    ImageView profilePhoto;
    TextView phoneView;
    Bitmap bitmapImage;
    String phone, edited_name, edited_email, edited_address, edited_photo;
    CardView cardView;

    private boolean isLoading = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        Initialize
         */
        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);
        //ProgressBar
        progressBar = findViewById(R.id.progressBarReg);
        profilePhoto = findViewById(R.id.ivPhotoView);

        phoneView = findViewById(R.id.tvPhoneView);
        //Name
        editTextName = findViewById(R.id.edUpName);
        //Email
        editTextEmail = findViewById(R.id.edUpEmail);
        //address
        editTextAddress = findViewById(R.id.edUpAddress);

        //CardView click as a Button
        cardView = findViewById(R.id.cardUpBtn);

        /*
        Current profile info.
         */
        Glide
                .with(this)
                .load(getString(R.string.base_url)+getString(R.string.user_photo_path)+pref.getString("photo", null))
                .placeholder(R.drawable.profile_icon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilePhoto);

        phoneView.setText(pref.getString("phone",null));

        editTextName.setText(pref.getString("name",null));
        editTextEmail.setText(pref.getString("email",null));
        editTextAddress.setText(pref.getString("address",null));


        /*
        For editable profile info set
         */
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call to Runtime permission confirmation method
                permissionGrant();

            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edited_name = editTextName.getText().toString().trim();
                edited_email = editTextEmail.getText().toString().trim();
                edited_address = editTextAddress.getText().toString().trim();

               if(edited_name.isEmpty()){
                    editTextName.setError("Enter name");
                    editTextName.requestFocus();
                }
                else if(edited_email.isEmpty()){
                    editTextEmail.setError("Enter an email");
                    editTextEmail.requestFocus();

                }else if(edited_address.isEmpty()){
                    editTextAddress.setError("Enter address");
                    editTextAddress.requestFocus();

                }else if (!isLoading){

                   Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

               }else if(isNetworkConnected()){

                   profileUpdate();

               }else{
                   Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
               }

            }
        });

    }


    //Runtime permission confirmation
    public void permissionGrant(){
        Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                //call to shoImageChoser method
                showImageChooser();
            }
        });
    }

    //Chosser photo
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 99);
    }

    //Getting image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {

                //Getting the Bitmap from Gallery
                Bitmap bitmapOrginalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                bitmapImage = getResizedBitmap(bitmapOrginalImage, 200);

                //Setting the Bitmap to ImageView
                profilePhoto.setImageBitmap(bitmapImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap bitmapImage, int maxSize) {
        int width = bitmapImage.getWidth();
        int height = bitmapImage.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmapImage, width, height, true);
    }


    //Bitmap to string convert
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public void profileUpdate(){
        progressBar.setVisibility(View.VISIBLE);
        isLoading = false;

        String url = getString(R.string.base_url)+getString(R.string.updateUserInfo);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("success")){

                        pref.edit().putString("name", edited_name).apply();
                        pref.edit().putString("email", edited_email).apply();
                        pref.edit().putString("address", edited_address).apply();
                        Toast.makeText(ProfileEdit.this, "Your profile has been updated.",Toast.LENGTH_SHORT).show();
                        finish();

                    }else{
                        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();

                    }
                    isLoading = true;

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileEdit.this, "Something went wrong!!",Toast.LENGTH_SHORT).show();
                isLoading = true;
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                phone = pref.getString("phone", null);

                if (bitmapImage == null){
                    edited_photo = pref.getString("phone", null);

                }else{
                    edited_photo = getStringImage(bitmapImage);
                }

                map.put("phone", phone);
                map.put("new_name", edited_name);
                map.put("new_email", edited_email);
                map.put("new_address", edited_address);
                map.put("new_photo", edited_photo);
                return map;

                // phone, new_phone, new_name, new_email, new_address, new_photo
            }
        };
        MySingleton.getInstance().addToRequestQueue(request);
    }





    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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
}
