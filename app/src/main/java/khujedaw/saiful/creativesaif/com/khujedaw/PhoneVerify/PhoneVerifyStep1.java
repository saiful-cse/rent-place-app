package khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import khujedaw.saiful.creativesaif.com.khujedaw.Policeies.Policies;
import khujedaw.saiful.creativesaif.com.khujedaw.Policeies.Terms_Conditions;
import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class PhoneVerifyStep1 extends AppCompatActivity {

    CardView cardView;
    EditText getNumber;
    CheckBox checkBox;
    Boolean check = false;
    String text;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify_step1);


        getNumber = findViewById(R.id.edGetNumber);
        cardView = findViewById(R.id.cardViewSendCode);
        checkBox = findViewById(R.id.checkBox);
        textView = findViewById(R.id.policiesText);


        text = "I agree to the 'Rent Place' Policies";

        Spannable span = Spannable.Factory.getInstance().newSpannable(text);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(PhoneVerifyStep1.this, "You have clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PhoneVerifyStep1.this, Policies.class));

            } }, 28, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(span);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = getNumber.getText().toString().trim();


                if(mobile.isEmpty() || mobile.length() < 11){
                    getNumber.setError("Enter a valid mobile");
                    getNumber.requestFocus();

                }else if(!checkBox.isChecked()){

                    Snackbar.make(findViewById(android.R.id.content),"Please! Confirm the policies.",Snackbar.LENGTH_LONG).show();

                } else if(isNetworkConnected()){
                    Intent intent = new Intent(PhoneVerifyStep1.this, PhoneVerifyStep2.class);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Please! Check Internet Connection",Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }


    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
