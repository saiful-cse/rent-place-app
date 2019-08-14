package khujedaw.saiful.creativesaif.com.khujedaw.PhoneVerify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import khujedaw.saiful.creativesaif.com.khujedaw.MainActivity;
import khujedaw.saiful.creativesaif.com.khujedaw.MySingleton;
import khujedaw.saiful.creativesaif.com.khujedaw.R;
import khujedaw.saiful.creativesaif.com.khujedaw.User.UserRegistration;

public class PhoneVerifyStep2 extends AppCompatActivity {

    String mobile;
    CardView sendBtnCardView;
    EditText getPinNumber;
    TextView btnResendCode, tvTimeCounter, tvStatus;
    ProgressBar progressBar;

    //These are the objects needed
    //It is the verification id that will be sent to the user
    String mVerificationId;

    CountDownTimer countDownTimer;

    //firebase auth object
    FirebaseAuth mAuth;

    SharedPreferences pref;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify_step2);

        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        getPinNumber = findViewById(R.id.edGetPinNumber);
        sendBtnCardView = findViewById(R.id.btnSendCard);
        btnResendCode = findViewById(R.id.tvResendCode);
        tvTimeCounter = findViewById(R.id.tvCounter);
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);

        pref = getApplicationContext().getSharedPreferences("Registration", MODE_PRIVATE);

        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        sendVerificationCode(mobile);

        //Invisible resend button
        btnResendCode.setVisibility(View.GONE);

        sendBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = getPinNumber.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    getPinNumber.setError("Enter valid code");
                    getPinNumber.requestFocus();

                }else if(isNetworkConnected()){
                    //verifying the code entered manually
                    verifyVerificationCode(code);
                    progressBar.setVisibility(View.VISIBLE);

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Please! Check Internet Connection",Snackbar.LENGTH_LONG).show();
                }

            }
        });

        //resend code
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCode(mobile);
                countDownTimer.start();
                tvTimeCounter.setVisibility(View.VISIBLE);
                btnResendCode.setVisibility(View.GONE);
                tvStatus.setText("Please enter the 6 digit code sent to "+mobile);
            }
        });

        //Timer for code
        countDownTimer = new CountDownTimer(120*1000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                tvTimeCounter.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tvTimeCounter.setText("Timeout!! Please resend the code");
                btnResendCode.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.GONE);
            }
        }.start();

    }

    //the method is sending verification code
    //the country id is concatenated
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + mobile,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
        tvStatus.setText("Please enter the 6 digit code sent to "+mobile);
    }



    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                getPinNumber.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerifyStep2.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };



    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneVerifyStep2.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && isNetworkConnected() && isLoading) {

                            //after verification is successfull, then check use is exist!!
                            isExist();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                                progressBar.setVisibility(View.GONE);
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void isExist(){
        progressBar.setVisibility(View.VISIBLE);
        isLoading = false;

        String url = getString(R.string.base_url)+getString(R.string.user_exist_check)+mobile;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("exist")){

                        //Locally saved on app
                        pref.edit().putString("phone", mobile).apply();
                        pref.edit().putBoolean("registered", true).apply();

                        Intent intent = new Intent(PhoneVerifyStep2.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        Snackbar.make(findViewById(android.R.id.content),"You are already registered.",Snackbar.LENGTH_LONG).show();

                    }else if(message.equals("success")){

                        Intent intent = new Intent(PhoneVerifyStep2.this, UserRegistration.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("phone", mobile);
                        startActivity(intent);

                    } else{

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
                Toast.makeText(PhoneVerifyStep2.this, (CharSequence) volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(request);
    }


}
