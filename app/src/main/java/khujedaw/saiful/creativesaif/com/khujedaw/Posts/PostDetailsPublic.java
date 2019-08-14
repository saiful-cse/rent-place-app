package khujedaw.saiful.creativesaif.com.khujedaw.Posts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.internal.Constants;

import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class PostDetailsPublic extends AppCompatActivity {

    Context context;
    Post post;
    private TextView  post_id,time, place_name, category, fee, place_address,
    description, name, email, phone, alt_phone;

    ImageView place_photo, photo, call1, call2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details_public);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        setId();

        post = getIntent().getExtras().getParcelable("postDetails");

        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.post_photo_path)+post.getPlace_photo())
                .into(place_photo);

        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.user_photo_path)+post.getPhoto())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.profile_icon)
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

        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Uri number = Uri.parse("tel:+88"+post.getPhone());
////                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
////                startActivity(callIntent);

                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:+88"+post.getPhone()));
                startActivity(callIntent);
            }
        });

        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri number = Uri.parse("tel:+88"+post.getAlt_phone());
//                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//                startActivity(callIntent);

                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:+88"+post.getAlt_phone()));
                startActivity(callIntent);
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

        call1 = findViewById(R.id.call1);
        call2 = findViewById(R.id.call2);

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
