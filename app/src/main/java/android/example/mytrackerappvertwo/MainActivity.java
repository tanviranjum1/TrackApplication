package android.example.mytrackerappvertwo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import util.LoginAPI;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainAct";
    FirebaseAuth fAuth;

    private TextView nameTxtView, phoneTxtView, codeTxtView, emergencyOneTxtView, emergencyTwoTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Main Activity");

        nameTxtView = findViewById(R.id.nameTxtView);
        phoneTxtView = findViewById(R.id.phoneTxtView);
        codeTxtView = findViewById(R.id.codeTxtView);
        emergencyOneTxtView = findViewById(R.id.emergencyOneTxtView);
        emergencyTwoTxtView = findViewById(R.id.emergencyTwoTxtView);

        fAuth = FirebaseAuth.getInstance();

        nameTxtView.setText(LoginAPI.getInstance().getUserName());
        phoneTxtView.setText(LoginAPI.getInstance().getUserPhone());
        codeTxtView.setText(LoginAPI.getInstance().getUserCode());
        emergencyOneTxtView.setText(getIntent().getStringExtra("key_EmergencyOne"));

        Log.d(TAG, "received name " + LoginAPI.getInstance().getUserName());
        Log.d(TAG, "received phone " + LoginAPI.getInstance().getUserPhone());
        Log.d(TAG, "received code " + LoginAPI.getInstance().getUserCode());
        Log.d(TAG, "received emergency one " + getIntent().getStringExtra("key_EmergencyOne"));
    }


    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();

    }
}