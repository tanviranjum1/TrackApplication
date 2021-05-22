package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import util.LoginAPI;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText friendCodeEdtTxt, friendPhoneEdtTxt;
    private ProgressBar add_FamProgressBar;
    private Button saveBtnView;

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_CODE = "personalcode";
    private static final String KEY_IMGURL = "imageURL";


    private FirebaseAuth firebaseAuth;

    FirebaseFirestore fStore;
    private static final String TAG = "AddActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.setTitle("Add Member");



        friendCodeEdtTxt = findViewById(R.id.friendCodeEdtTxt);
        friendPhoneEdtTxt = findViewById(R.id.friendPhoneEdtTxt);
        saveBtnView = findViewById(R.id.saveBtnView);
        add_FamProgressBar = findViewById(R.id.add_FamProgressBar);
        add_FamProgressBar.setVisibility(View.GONE);

        fStore = FirebaseFirestore.getInstance();

        saveBtnView.setOnClickListener(this);




    }

    public void storeDB(String fUserName) {

        // adding user's friend name as key and friend phone as value.
        DocumentReference documentReference1;
        documentReference1 = fStore.collection("users").document(LoginAPI.getInstance().getUserPhone());
        Map<String, Object> user = new HashMap<>();
        user.put(fUserName, friendPhoneEdtTxt.getText().toString().trim());
        documentReference1.set(user, SetOptions.merge());


        // add new collection named user's Phone and and it will have his friends as documents.
        DocumentReference documentReference2;
        String uniqueCollectionName = "Friends_" + LoginAPI.getInstance().getUserPhone();
        documentReference2 = fStore.collection(uniqueCollectionName).document(fUserName);
        Map<String, Object> mp2 = new HashMap<>();
        mp2.put("dummy1", "dummy2");
        documentReference2.set(mp2, SetOptions.merge());


        // add new collection
        DocumentReference documentReference3;
        String uniqueCollectionName2 = "TrackME_" + friendPhoneEdtTxt.getText().toString().trim();
        documentReference3 = fStore.collection(uniqueCollectionName2).document(LoginAPI.getInstance().getUserName());
        Map<String, Object> mp = new HashMap<>();
        //store username,phone,code and imgurl.
        mp.put(KEY_CODE, LoginAPI.getInstance().getUserCode());
        mp.put(KEY_IMGURL, LoginAPI.getInstance().getImgUrl());
        mp.put(KEY_PHONE, LoginAPI.getInstance().getUserPhone());

        documentReference3.set(mp, SetOptions.merge());

        //go to circle activity
        add_FamProgressBar.setVisibility(View.INVISIBLE);
        startActivity(new Intent(AddActivity.this, MyCircleActivity.class));
        finish();

    }

    @Override
    public void onClick(View v) {
        add_FamProgressBar.setVisibility(View.VISIBLE);

        String friendCode = friendCodeEdtTxt.getText().toString().trim();
        String friendPhone = friendPhoneEdtTxt.getText().toString().trim();

        if (!TextUtils.isEmpty(friendCode) && !TextUtils.isEmpty(friendPhone)) {


           /* DocumentReference documentReference = fStore.collection("users").document(friendPhoneEdtTxt.getText().toString().trim());
            //verify the code.
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString(KEY_CODE) != null) {

                        if (documentSnapshot.getString(KEY_CODE).equals(friendCodeEdtTxt.getText().toString().trim())) {
                            Toast.makeText(AddActivity.this, "User Found!", Toast.LENGTH_SHORT).show();

                            if (documentSnapshot.getString(KEY_USERNAME) != null) {
                                String fUserName = documentSnapshot.getString(KEY_USERNAME);
                                storeDB(fUserName);
                            }

                        }
                        else {
                            Toast.makeText(AddActivity.this, "No such user!", Toast.LENGTH_SHORT).show();
                            add_FamProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
            */


            DocumentReference docRef = fStore.collection("users").document(friendPhone);

            // just verify password
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.getString(KEY_CODE) != null) {
                        if (documentSnapshot.getString(KEY_CODE).equals(friendCodeEdtTxt.getText().toString().trim())) {

                            String fUserName = documentSnapshot.getString(KEY_USERNAME);
                            storeDB(fUserName);

                        } else {
                            Toast.makeText(AddActivity.this, "No such user!", Toast.LENGTH_SHORT).show();
                            add_FamProgressBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Toast.makeText(AddActivity.this, "No such user!", Toast.LENGTH_SHORT).show();
                        add_FamProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Error in adding: " + e.toString());
                    add_FamProgressBar.setVisibility(View.INVISIBLE);
                }
            });

        }
        else {
            add_FamProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please fill in all requirements", Toast.LENGTH_SHORT).show();
        }

    }



    }
