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

public class UserRegistration extends AppCompatActivity {

    CardView cardView;
    TextInputEditText editTextName, editTextEmail, editTextAddress;
    TextView textViewPhone;
    SharedPreferences pref;
    ProgressBar progressBar;
    ImageView profilePhoto;
    Bitmap bitmapImage;
    String phone, name, email, address, photo;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        //getting phone from previous activity
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        textViewPhone = findViewById(R.id.tvPhone);
        textViewPhone.setText(phone);
        //ProgressBar
        progressBar = findViewById(R.id.progressBarReg);

        //Name
        editTextName = findViewById(R.id.edName);

        //Email
        editTextEmail = findViewById(R.id.edEmail);

        //address
        editTextAddress = findViewById(R.id.edAddress);

        //CardView click as a Button
        cardView = findViewById(R.id.cardViewRegistration);

        profilePhoto = findViewById(R.id.circleImageView);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call to Runtime permission confirmation method

                permissionconfirmation();

            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = editTextName.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                address = editTextAddress.getText().toString().trim();

                if (name.isEmpty()) {
                    editTextName.setError("Enter your name");

                }else if(email.isEmpty()){
                    editTextEmail.setError("Enter an email");

                }else if(address.isEmpty()){
                    editTextAddress.setError("Enter address");

                }else if(bitmapImage == null){
                    Snackbar.make(findViewById(android.R.id.content),"Please! Select an image",Snackbar.LENGTH_LONG).show();

                } else if (!isLoading){

                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(isNetworkConnected()){

                    registration();

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
                }

            }
        });


    }


    //Runtime permission confirmation
    public void permissionconfirmation(){
        Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {

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


    public void registration(){
        progressBar.setVisibility(View.VISIBLE);
        isLoading = false;

        String url = getString(R.string.base_url)+getString(R.string.user_registration);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("success") || message.equals("exist")) {

                        //Locally saved on app
                        pref.edit().putString("phone", phone).apply();
                        pref.edit().putBoolean("registered", true).apply();

                        Intent intent = new Intent(UserRegistration.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        //Snackbar.make(findViewById(android.R.id.content),"Reg. Successfully",Snackbar.LENGTH_LONG).show();


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
                Toast.makeText(UserRegistration.this, (CharSequence) volleyError.toString(),Toast.LENGTH_SHORT).show();
                isLoading = true;
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                photo = getStringImage(bitmapImage);
                map.put("phone", phone);
                map.put("name", name);
                map.put("email", email);
                map.put("address", address);
                map.put("photo", photo);
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
}
