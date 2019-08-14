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
import com.bumptech.glide.Glide;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.R;
import khujedaw.saiful.creativesaif.com.khujedaw.User.ProfileEdit;
import khujedaw.saiful.creativesaif.com.khujedaw.User.UserRegistration;

public class PostEdit extends AppCompatActivity {

    Post post;

    RadioGroup radioGroup;
    TextInputEditText building_market_name, fee, address, post_desc, alt_phone;
    TextView tvPhone;
    CardView cardViewPost;
    String category, place_name, feeBdt, place_address, desc, altPhone, place_photo, new_place_photo;

    SharedPreferences pref;
    ProgressBar progressBar;
    ImageView imageViewPostPhoto;

    Bitmap bitmapOrginalImage;
    Bitmap bitmapImage;

    CardView cardViewPostUpdateBtn;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        post = getIntent().getExtras().getParcelable("postEdit");

        setID();

        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.post_photo_path)+post.getPlace_photo())
                .placeholder(R.drawable.ic_menu_gallery)
                .into(imageViewPostPhoto);


        imageViewPostPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call to Runtime permission confirmation method
                permissionconfirmation();

            }
        });

        switch (post.getCategory()){
            case "Flat":
                radioGroup.check(R.id.rdbFlat);
                break;
            case "Hostel":
                radioGroup.check(R.id.rdbHostel);
                break;
            case "Mess":
                radioGroup.check(R.id.rdbMess);
                break;
            case "Office":
                radioGroup.check(R.id.rdbOffice);
                break;
            case "Shop":
                radioGroup.check(R.id.rdbShop);
                break;
            case "Garage":
                radioGroup.check(R.id.rdbGarage);
                break;
        }

        //TextInputEditText building_market_name, fee, address, post_desc, alt_phone;
        building_market_name.setText(post.getPlace_name());
        fee.setText(post.getFee());
        address.setText(post.getPlace_address());
        post_desc.setText(post.getDescription());
        alt_phone.setText(post.getAlt_phone());
        tvPhone.setText(post.getPhone()+"(Account)");

        //post update
        cardViewPostUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getting string from editText
                getString();

                /*
                Validate post data
                place_name, feeBdt, place_address, desc, altPhone
                no need to validate photo and category
                 */
                if (place_name.isEmpty()){
                    building_market_name.setError("Building/Market name cannot be empty.");

                }else if(feeBdt.isEmpty()){
                    fee.setError("Fee cannot be empty.");

                }else if(place_address.isEmpty()){
                    address.setError("Address cannot be empty.");

                }else if(desc.isEmpty()){
                    post_desc.setError("Description cannot be empty.");

                }else if(altPhone.isEmpty() || altPhone.length() < 11){
                    alt_phone.setError("Enter valid phone number.");

                }else if(!isLoading){

                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(isNetworkConnected()){

                    post_update();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection or Try again later.",Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }


    //Runtime permission confirmation
    public void permissionconfirmation(){
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

        cardViewPost = findViewById(R.id.cardViewPost);
        progressBar = findViewById(R.id.progressBarPost);

        cardViewPostUpdateBtn = findViewById(R.id.cardViewPostUpdate);
    }

    public void getString(){
        place_name = building_market_name.getText().toString().trim();
        feeBdt = fee.getText().toString().trim();
        place_address = address.getText().toString();
        desc = post_desc.getText().toString().trim();
        altPhone = alt_phone.getText().toString().trim();
    }


    public void post_update(){
        progressBar.setVisibility(View.VISIBLE);
        isLoading = false;

        String url = getString(R.string.base_url)+getString(R.string.updatePost);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("success")) {
                        Toast.makeText(PostEdit.this, "Your post has been updated.",Toast.LENGTH_SHORT).show();
                        finish();

                    }else{

                        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
                        isLoading = true;
                        progressBar.setVisibility(View.GONE);
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
                Toast.makeText(PostEdit.this, "Network Error!!",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();


                if (bitmapImage == null){
                    new_place_photo = post.getPlace_photo();

                }else{
                    new_place_photo = getStringImage(bitmapImage);
                }

                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioButton = findViewById(selectedId);
                category = radioButton.getText().toString();

                map.put("post_id", post.getPost_id());
                map.put("user_phone", post.getPhone());
                map.put("alt_phone", altPhone);
                map.put("category", category);
                map.put("place_name", place_name);
                map.put("fee", feeBdt);
                map.put("place_address", place_address);
                map.put("description", desc);
                map.put("place_photo", post.getPlace_photo());
                map.put("new_place_photo", new_place_photo);
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