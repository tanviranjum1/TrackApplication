package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.LoginAPI;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText signInPassEdtTxt;
    private ProgressBar progressBar;
    private Button sendOTPBtnView;
    private TextView createAcctTxtView;
    private CountryCodePicker countryCodeEdtTxt;
    private AutoCompleteTextView  signInPhoneEdtTxt;


    private FirebaseAuth fAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;


    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    //db key
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EM = "emergency";
    private static final String KEY_EM_TWO = "emergencytwo";
    private static final String KEY_CODE = "personalcode";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IMGURL = "imageURL";
    //for verify pass:
    private static final String KEY_PASS = "pass";

    private DocumentReference docRef;

    private static String TAG = "LoginActivity";

    //for SOS intent pass.
    private String emergencyOne, emergencyTwo;

    private String userPhoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Sign In");


        signInPhoneEdtTxt = findViewById(R.id.signInPhoneEdtTxt);
        countryCodeEdtTxt = (CountryCodePicker) findViewById(R.id.countryCodeEdtTxtView);
        signInPassEdtTxt = findViewById(R.id.signInPassEdtTxt);
        sendOTPBtnView = findViewById(R.id.sendOTPBtnView);
        createAcctTxtView = findViewById(R.id.createAcctTxtView);
        progressBar = findViewById(R.id.homePrgBar);


        fAuth = fAuth.getInstance();

        sendOTPBtnView.setOnClickListener(this);
        createAcctTxtView.setOnClickListener(this);

        countryCodeEdtTxt.registerCarrierNumberEditText(signInPhoneEdtTxt);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    //user is already loggedin..
                    retrieveEssentials();
                } else {
                    //no user yet...
                }
            }
        };

    }

    //if logged in then take to dashboard.
    @Override
    protected void onStart() {
        super.onStart();
        currentUser=fAuth.getCurrentUser();
        fAuth.addAuthStateListener(authStateListener);

//        if (fAuth.getCurrentUser() != null) {
//            //  fetch the userPhone,userName,userCode and userImage and emergency contacts.
//            retrieveEssentials();
//
//        }
    }

    private void retrieveEssentials() {
        String userId= fAuth.getCurrentUser().getUid();

        progressBar.setVisibility(View.VISIBLE);

        docRef = fStore.collection("LogIn").document(userId);

        // retrieve username,userPhone,userCode,imgUrl(later),emergencyOne and emergencyTwo.
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot documentSnapshot) {

              LoginAPI loginApi = LoginAPI.getInstance();
              Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
              progressBar.setVisibility(View.INVISIBLE);
              if (documentSnapshot.getString(KEY_PHONE) != null) {
                  loginApi.setUserPhone(documentSnapshot.getString(KEY_PHONE));
              }
              if (documentSnapshot.getString(KEY_CODE) != null) {
                  loginApi.setUserCode(documentSnapshot.getString(KEY_CODE));
              }
              if (documentSnapshot.getString(KEY_USERNAME) != null) {
                  loginApi.setUserName(documentSnapshot.getString(KEY_USERNAME));
              }
              if (documentSnapshot.getString(KEY_IMGURL) != null) {
                  loginApi.setImgUrl(documentSnapshot.getString(KEY_IMGURL));
              }
              if (documentSnapshot.getString(KEY_EM) != null) {
                  emergencyOne = documentSnapshot.getString(KEY_EM);
              }
              if (documentSnapshot.getString(KEY_EM_TWO) != null) {
                  emergencyTwo = documentSnapshot.getString(KEY_EM_TWO);
              }
              if (emergencyOne != null) {
                  intent.putExtra("key_EmergencyOne", emergencyOne);
              }
              if (emergencyTwo != null) {
                  intent.putExtra("key_EmergencyTwo", emergencyTwo);
              }
              startActivity(intent);
              finish();

          }
                                          }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error in retrieving: " + e.toString());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendOTPBtnView:
                userLogin();
                break;
            case R.id.createAcctTxtView:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
        }
    }


    public void userLogin() {

        progressBar.setVisibility(View.VISIBLE);

        String pNum = signInPhoneEdtTxt.getText().toString().trim();
        String pass = signInPassEdtTxt.getText().toString().trim();



        if (pNum.isEmpty()) {
            signInPhoneEdtTxt.setError("Enter the phone number");
            signInPhoneEdtTxt.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        if (pass.isEmpty()) {
            signInPassEdtTxt.setError("Enter the password");
            signInPassEdtTxt.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }


        userPhoneNum = (countryCodeEdtTxt.getFullNumberWithPlus().replace(" ", "")).toString();


        docRef = fStore.collection("users").document(userPhoneNum);

        // just verify password
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getString(KEY_PASS) != null) {
                    if (documentSnapshot.getString(KEY_PASS).equals(signInPassEdtTxt.getText().toString().trim())) {
                        Intent intent = new Intent(getApplicationContext(), VerifyOtpActivity.class);
                        intent.putExtra("keyPhoneNum", countryCodeEdtTxt.getFullNumberWithPlus().replace(" ", ""));
                        String data = "101";
                        intent.putExtra("key", data);
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        signInPassEdtTxt.setError("Wrong Password");
                        signInPassEdtTxt.requestFocus();
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                } else {
                    signInPassEdtTxt.setError("Wrong Password");
                    signInPassEdtTxt.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error in verifying password: " + e.toString());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (fAuth!=null)
        {
            fAuth.removeAuthStateListener(authStateListener);
        }
    }
}