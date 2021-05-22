package android.example.mytrackerappvertwo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int secondsDelayed = 5;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashScreen.this,
                        LoginActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
        /*
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        */

    }
}
