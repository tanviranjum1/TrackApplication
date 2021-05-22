package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import util.LoginAPI;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 1;
    private Button registerBtnView;
    private EditText registerPasswordEdtTxt, registerPhoneEdtTxt, registerNameEdtTxt, registerOTPEditTxt;
    private ImageView addPhotoButton;
    private ProgressBar registerProgressBar;
    private ImageView avatarImgView;


    private FirebaseAuth firebaseAuth;

    private static String TAG = "RegisterActivity";
    private Uri imageUri;

    private String userPhoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Registration");


        firebaseAuth=FirebaseAuth.getInstance();


        registerPasswordEdtTxt = findViewById(R.id.registerPasswordEdtTxt);
        registerPhoneEdtTxt = findViewById(R.id.registerPhoneEdtTxt);
        registerNameEdtTxt = findViewById(R.id.registerNameEdtTxt);
        registerBtnView = findViewById(R.id.registerBtnView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        registerProgressBar.setVisibility(View.GONE);

        avatarImgView = findViewById(R.id.avatarImgView);

        addPhotoButton.setOnClickListener(this);
        registerBtnView.setOnClickListener(this);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtnView:
                userRegister();
                break;
            case R.id.addPhotoButton:
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, GALLERY_CODE);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, GALLERY_CODE);
                }
            break;
        }
    }


    private void userRegister() {
        registerProgressBar.setVisibility(View.VISIBLE);
        String name = registerNameEdtTxt.getText().toString().trim();
        String phone = registerPhoneEdtTxt.getText().toString().trim();
        String pass = registerPasswordEdtTxt.getText().toString().trim();

        if (name.isEmpty()) {
            registerNameEdtTxt.setError("Enter the name");
            registerNameEdtTxt.requestFocus();
            registerProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        if (phone.isEmpty()) {
            registerPhoneEdtTxt.setError("Enter the name");
            registerPhoneEdtTxt.requestFocus();
            registerProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        if (pass.isEmpty()) {
            registerPasswordEdtTxt.setError("Enter the name");
            registerPasswordEdtTxt.requestFocus();
            registerProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            registerProgressBar.setVisibility(View.INVISIBLE);
            return;
        }

        userPhoneNum = "+" + phone;

        // pass intent phone number.
        Intent intent = new Intent(getApplicationContext(), VerifyOtpActivity.class);
        String data = "111";
        intent.putExtra("key", data);
        intent.putExtra("keyNewName", name);
        intent.putExtra("keyPhoneNum", userPhoneNum);
        intent.putExtra("keyNewPass", pass);
        intent.putExtra("imagePath", imageUri.toString());
        registerProgressBar.setVisibility(View.INVISIBLE);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // we have the actual path to image.
                avatarImgView.setImageURI(imageUri); //show image
            }
        }
    }
}