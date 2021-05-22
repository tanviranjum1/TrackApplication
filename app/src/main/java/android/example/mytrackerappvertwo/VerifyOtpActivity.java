package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import util.LoginAPI;

public class VerifyOtpActivity extends AppCompatActivity {


    private static final String TAG = "VerifyOtpActivity";
    private FirebaseAuth fAuth;
    private FirebaseUser user;


    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();


    private String phoneNumber;

    //for SOS intent pass.
    private String emergencyOne, emergencyTwo;
    private FirebaseStorage storage;

    private StorageReference storageReference;


    //db key
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EM = "emergency";
    private static final String KEY_EM_TWO = "emergencytwo";
    private static final String KEY_CODE = "personalcode";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IMGURL = "imageURL";
    //for verify pass:
    private static final String KEY_PASS = "pass";


   // private EditText OTPEdtTxtView;
    private EditText inputCode1,inputCode2, inputCode3, inputCode4, inputCode5,inputCode6;
    private Button verifyBtnView, resendBtnView;
    private ProgressDialog progressDialog;
    //private ProgressBar OTPPrgBar;

    // callback instance
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken token;

    private String verificationId;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        this.setTitle("OTP Verification");

        fAuth = fAuth.getInstance();

       // OTPEdtTxtView = findViewById(R.id.OTPEdtTxtView);
        inputCode1=findViewById(R.id.inputCode1);
        inputCode2=findViewById(R.id.inputCode2);
        inputCode3=findViewById(R.id.inputCode3);
        inputCode4=findViewById(R.id.inputCode4);
        inputCode5=findViewById(R.id.inputCode5);
        inputCode6=findViewById(R.id.inputCode6);

        verifyBtnView = findViewById(R.id.verifyBtnView);
        resendBtnView = findViewById(R.id.resendBtnView);
        //OTPPrgBar = findViewById(R.id.OTPPrgBar);
        progressDialog= new ProgressDialog(VerifyOtpActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );


        resendBtnView.setEnabled(false);


        storage=FirebaseStorage.getInstance();
        storageReference =storage.getReference();

        phoneNumber = getIntent().getStringExtra("keyPhoneNum").toString();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(VerifyOtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
//                OTPPrgBar.setVisibility(View.INVISIBLE);
                // OTPEdtTxtView.setVisibility(View.VISIBLE);

                progressDialog.dismiss();

                inputCode1.setVisibility(View.VISIBLE);
                inputCode2.setVisibility(View.VISIBLE);
                inputCode3.setVisibility(View.VISIBLE);
                inputCode4.setVisibility(View.VISIBLE);
                inputCode5.setVisibility(View.VISIBLE);
                inputCode6.setVisibility(View.VISIBLE);

                verifyBtnView.setVisibility(View.VISIBLE);
                resendBtnView.setVisibility(View.VISIBLE);
                resendBtnView.setEnabled(false);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendBtnView.setEnabled(true);
            }
        };


        verifyPhoneNumber(phoneNumber);

        resendBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move onto requesting otp
                verifyPhoneNumber(phoneNumber);
                resendBtnView.setEnabled(false);
            }
        });

        verifyBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verCode="";
                verCode+=inputCode1.getText().toString()+inputCode2.getText().toString()+inputCode3.getText().toString()+inputCode4.getText().toString()+inputCode5.getText().toString()+inputCode6.getText().toString();

                if (!inputCode1.getText().toString().isEmpty() && !inputCode2.getText().toString().isEmpty()
                        && !inputCode3.getText().toString().isEmpty()
                        && !inputCode4.getText().toString().isEmpty() &&
                        !inputCode5.getText().toString().isEmpty() &&
                        !inputCode6.getText().toString().isEmpty())
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verCode);
                    authenticateUser(credential);
                }
                else {
                    Toast.makeText(VerifyOtpActivity.this, "Please Fill in the OTP fields first", Toast.LENGTH_SHORT).show();
                    inputCode1.requestFocus();
                    return;
                }
            }
        });


    }




    public void verifyPhoneNumber(String phoneNumber) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fAuth)
                .setActivity(this).setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void authenticateUser(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

//               OTPPrgBar.setVisibility(View.INVISIBLE);
                user = fAuth.getCurrentUser();
                assert user != null;
                String currentUserId = user.getUid();


                String received = getIntent().getStringExtra("key");
                if (received.equals("101")) {
                    Intent intent = new Intent(VerifyOtpActivity.this, DashboardActivity.class);
                    userId = fAuth.getCurrentUser().getUid();

                    DocumentReference docRef = fStore.collection("LogIn").document(userId);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            LoginAPI loginApi = LoginAPI.getInstance();
                            loginApi.setUserPhone(phoneNumber);
                            if (documentSnapshot.getString(KEY_USERNAME) != null) {
                                loginApi.setUserName(documentSnapshot.getString(KEY_USERNAME));
                            }
                            if (documentSnapshot.getString(KEY_CODE) != null) {
                                loginApi.setUserCode(documentSnapshot.getString(KEY_CODE));
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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error in retrieving: " + e.toString());
                            progressDialog.dismiss();
//                            OTPPrgBar.setVisibility(View.GONE);
                        }
                    });
                }
                if (received.equals("111")) {
                    //receive the name, pass, phone and image uri from phone

                    String image_path = getIntent().getStringExtra("imagePath");
                    Uri newImageURI = Uri.parse(image_path);

                    StorageReference filepath = storageReference
                            .child("user_images")
                            .child("my_image_" + Timestamp.now().getSeconds());

                    filepath.putFile(newImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            OTPPrgBar.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String newUsername = getIntent().getStringExtra("keyNewName");
                                    String newUserpass = getIntent().getStringExtra("keyNewPass");
                                    String imageUrl = uri.toString();
                                    LoginAPI loginApi = LoginAPI.getInstance();
                                    loginApi.setUserPhone(phoneNumber);
                                    loginApi.setUserName(newUsername);
                                    String code = generateCode();
                                    loginApi.setUserCode(code);
                                    loginApi.setImgUrl(imageUrl);

                                    DocumentReference docRef = fStore.collection("LogIn").document(fAuth.getCurrentUser().getUid());
                                    Map<String, Object> userdata = new HashMap<>();
                                    userdata.put(KEY_CODE, LoginAPI.getInstance().getUserCode());
                                    userdata.put(KEY_PHONE, LoginAPI.getInstance().getUserPhone());
                                    userdata.put(KEY_IMGURL, imageUrl);
                                    userdata.put(KEY_USERNAME, LoginAPI.getInstance().getUserName());
                                    docRef.set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "successfull at storing");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            OTPPrgBar.setVisibility(View.INVISIBLE);
                                            progressDialog.dismiss();
                                            Log.d(TAG, "onFailure at storing: " + e.getMessage());
                                        }
                                    });
                                    DocumentReference docRef2 = fStore.collection("users").document(LoginAPI.getInstance().getUserPhone());
                                    Map<String, Object> userdata2 = new HashMap<>();
                                    userdata2.put(KEY_PASS, newUserpass);
                                    userdata2.put(KEY_CODE, LoginAPI.getInstance().getUserCode());
                                    userdata2.put(KEY_USERNAME, LoginAPI.getInstance().getUserName());

                                    docRef2.set(userdata2)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    OTPPrgBar.setVisibility(View.INVISIBLE);
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(getApplicationContext(), RegisterSecondActivity.class);
                                                    intent.putExtra("Key_UserId", fAuth.getCurrentUser().getUid());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            OTPPrgBar.setVisibility(View.INVISIBLE);
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "e.getMessage()", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            OTPPrgBar.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();

                        }
                    });
                }
            }
        });
    }


    private String generateCode() {
        Random randGenerator = new Random();
        String initials = LoginAPI.getInstance().getUserName();
        int number = 100000 + randGenerator.nextInt(900000);
        String generate = String.valueOf(number);
        int i = 0, ind = 0;
        String code = "";
        while (true) {
            //characters that are uppercased already ar turning into symbol.
            if (i == 3) {
                if ((int) initials.charAt(0) >= 65 && (int) initials.charAt(0) <= 90) {
                    code += (char) (initials.charAt(0));
                } else {
                    code += (char) (initials.charAt(0) - 32);
                }
                i++;
            } else if (i == 6) {
                code += (char) (initials.charAt(1) - 32);
                break;
            } else {
                code += generate.charAt(ind);
                i++;
                ind++;
            }
        }
        return code;
    }
}