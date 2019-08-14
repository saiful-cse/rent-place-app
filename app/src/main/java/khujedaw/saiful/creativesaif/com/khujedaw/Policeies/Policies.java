package khujedaw.saiful.creativesaif.com.khujedaw.Policeies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify.PhoneVerifyStep1;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class Policies extends AppCompatActivity {

    private TextView terms, policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policies);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        terms = findViewById(R.id.tvterms);
        policy = findViewById(R.id.tvpolicy);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Policies.this, Terms_Conditions.class));
            }
        });

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Policies.this, Privacy_Policy.class));
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
