package khujedaw.saiful.creativesaif.com.khujedaw.Posts;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class AddPost extends AppCompatActivity {

    RadioGroup radioGroup;
    TextInputEditText building_market_name, fee, address, post_desc, alt_phone;
    TextView tvPhone;
    CardView cardViewPost;
    String accPhone, category, place_name, feeBdt, place_address, desc, altPhone, place_photo;

    SharedPreferences pref;
    ProgressBar progressBar;
    ImageView imageViewPostPhoto;
    Bitmap bitmapOrginalImage;
    Bitmap bitmapImage;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ads);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        //Setting ID
        setID();

        imageViewPostPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call to Runtime permission confirmation method
                permissionconfirmation();

                //call to shoImageChoser method
                showImageChooser();
            }
        });

        cardViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //category = rb.getText().toString().trim();
                place_name = building_market_name.getText().toString().trim();
                feeBdt = fee.getText().toString().trim();
                place_address = address.getText().toString().trim();
                desc = post_desc.getText().toString().trim();
                altPhone = alt_phone.getText().toString().trim();
                accPhone = pref.getString("phone",null);


                if(bitmapImage == null){
                    Snackbar.make(findViewById(android.R.id.content),"Please!! select photo",Snackbar.LENGTH_LONG).show();

                } else if(radioGroup.getCheckedRadioButtonId() == -1){
                    Snackbar.make(findViewById(android.R.id.content),"Please!! select category.",Snackbar.LENGTH_LONG).show();

                }else if(place_name.isEmpty()){
                    building_market_name.setError("Building/Market name cannot be empty.");

                }else if(feeBdt.isEmpty()){
                    fee.setError("Fee cannot be empty.");

                }else if(place_address.isEmpty() ){
                    address.setError("Address cannot be empty.");

                }else if(desc.isEmpty()){
                    post_desc.setError("Description cannot be empty.");

                }else if(altPhone.isEmpty() || alt_phone.length() < 11){
                    alt_phone.setError("Enter valid phone number.");

                } else if(!isLoading){
                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(isNetworkConnected()){

                    post_publish();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();
                    isLoading = true;
                    progressBar.setVisibility(View.GONE);
                }

            }
        });


    }

    //Runtime permission confirmation
    public void permissionconfirmation(){
        Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {
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
                bitmapOrginalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                bitmapImage = getResizedBitmap(bitmapOrginalImage, 500);

                //Setting the Bitmap to ImageView
                imageViewPostPhoto.setImageBitmap(bitmapImage);
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


    public void setID(){
        //User input
        imageViewPostPhoto = findViewById(R.id.postPhoto);
        radioGroup = findViewById(R.id.radioGroup);
        building_market_name = findViewById(R.id.edBuilding_market);
        fee = findViewById(R.id.edFee);
        address = findViewById(R.id.edPostAddress);
        post_desc = findViewById(R.id.edServiceDesc);
        alt_phone = findViewById(R.id.edPhoneAlt);

        //phone account read
        tvPhone = findViewById(R.id.tvAccPhone);
        tvPhone.setText(pref.getString("phone",null)+" (Account)");

        cardViewPost = findViewById(R.id.cardViewPost);
        progressBar = findViewById(R.id.progressBarPost);
    }


    public void post_publish(){
        progressBar.setVisibility(View.VISIBLE);
        isLoading = false;

        String url = getString(R.string.base_url)+getString(R.string.post_publish);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("success")) {
                        Toast.makeText(AddPost.this, "Your post has been published.",Toast.LENGTH_LONG).show();
                        finish();

                    }else{

                        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
                    }
                    isLoading = true;
                    progressBar.setVisibility(View.GONE);

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isLoading = true;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddPost.this, "Network Error!!",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                place_photo = getStringImage(bitmapImage);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = findViewById(selectedId);
                category = radioButton.getText().toString();

                map.put("user_phone", accPhone);
                map.put("alt_phone", altPhone);
                map.put("category", category);
                map.put("place_name", place_name);
                map.put("fee", feeBdt);
                map.put("place_address", place_address);
                map.put("description", desc);
                map.put("place_photo", place_photo);
                return map;

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
