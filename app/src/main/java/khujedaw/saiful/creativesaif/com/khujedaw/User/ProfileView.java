package khujedaw.saiful.creativesaif.com.khujedaw.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class ProfileView extends AppCompatActivity {

    TextView textViewPhone, textViewName, textViewEmail, textViewAddress;
    ImageView profileImage;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        //Initialize
        textViewPhone = findViewById(R.id.tvPhone);
        textViewName = findViewById(R.id.tvName);
        textViewEmail = findViewById(R.id.tvEmail);
        textViewAddress = findViewById(R.id.tvAddress);
        profileImage = findViewById(R.id.profileImage);

        textViewPhone.setText(pref.getString("phone",null));
        textViewName.setText(pref.getString("name", null));
        textViewEmail.setText(pref.getString("email", null));
        textViewAddress.setText(pref.getString("address", null));

        Glide
                .with(this)
                .load(getString(R.string.base_url)+getString(R.string.user_photo_path)+pref.getString("photo", null))
                .placeholder(R.drawable.profile_icon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImage);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ProfileView.this, ProfileEdit.class));
                startActivity(new Intent(ProfileView.this, ProfileEdit.class));
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

}
