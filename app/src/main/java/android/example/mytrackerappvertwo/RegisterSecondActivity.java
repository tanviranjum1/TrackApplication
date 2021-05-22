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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import util.LoginAPI;

public class RegisterSecondActivity extends AppCompatActivity {


    private EditText emergencyOne_Et, emergencyTwo_Et;
    private Button setupEmergencyBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;


    private static final String KEY_EM = "emergency";
    private static final String KEY_EM_TWO = "emergencytwo";
    private static String TAG = "RegisterSecondActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second);
        this.setTitle("Registration");


        emergencyOne_Et = findViewById(R.id.emergencyOne_Et);
        emergencyTwo_Et = findViewById(R.id.emergencyTwo_Et);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        setupEmergencyBtn = findViewById(R.id.setupEmergencyBtn);

        setupEmergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emContact = emergencyOne_Et.getText().toString().trim();
                String emContactTwo = emergencyTwo_Et.getText().toString().trim();

                if (!TextUtils.isEmpty(emContact) && !TextUtils.isEmpty(emContactTwo)) {
                    DocumentReference refLogIn = fStore.collection("LogIn").document(getIntent().getStringExtra("Key_UserId"));

                    Map<String, Object> amap = new HashMap<>();
                    amap.put(KEY_EM, emergencyOne_Et.getText().toString().trim());
                    amap.put(KEY_EM_TWO, emergencyTwo_Et.getText().toString().trim());
                    refLogIn.set(amap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getApplicationContext(), RegisterThirdActivity.class);
                            intent.putExtra("key_EmergencyOne", emergencyOne_Et.getText().toString().trim());
                            intent.putExtra("key_EmergencyTwo", emergencyTwo_Et.getText().toString().trim());
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "ONFAILURE at storing " + e.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(RegisterSecondActivity.this, "Please fill in all Requirements", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}