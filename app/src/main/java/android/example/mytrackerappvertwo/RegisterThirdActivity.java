package android.example.mytrackerappvertwo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import util.LoginAPI;

public class RegisterThirdActivity extends AppCompatActivity {



    private TextView myCodeTxtView,welcomeTxtView;
    private Button registerNextImgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_third);


        myCodeTxtView = findViewById(R.id.myCodeTxtView);
        welcomeTxtView = findViewById(R.id. welcomeTxtView);
        registerNextImgButton = findViewById(R.id.registerNextImgButton);

        welcomeTxtView.setText("Welcome " +LoginAPI.getInstance().getUserName());
        myCodeTxtView.setText(LoginAPI.getInstance().getUserCode());

        registerNextImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emergencyOne = getIntent().getStringExtra("key_EmergencyOne");
                String emergencyTwo = getIntent().getStringExtra("key_EmergencyTwo");
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.putExtra("key_EmergencyOne", emergencyOne);
                intent.putExtra("key_EmergencyTwo", emergencyTwo);
                startActivity(intent);
                finish();
            }
        });
    }
}