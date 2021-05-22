package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import util.LoginAPI;

public class TrackingMeProfile extends AppCompatActivity {


    private static final String KEY_PHONE = "phone";
    private static final String KEY_CODE = "personalcode";
    private static final String KEY_IMGURL = "imageURL";


    private TextView trackerNameTitle_tv;
    private TextView trackerPhone_tv;
    private TextView trackerCode_tv;
    private ImageView trackerImg_iv;
    private String tName;

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_me_profile);
        trackerNameTitle_tv = findViewById(R.id.trackerNameTitle_tv);
        trackerPhone_tv = findViewById(R.id.trackerPhone_tv);
        trackerCode_tv = findViewById(R.id.trackerCode_tv);
        trackerImg_iv = findViewById(R.id.trackerImg_iv);

        tName = getIntent().getStringExtra("key_trackerName");
        trackerNameTitle_tv.setText(tName + "\'s Details");

        database = FirebaseFirestore.getInstance();
        String colName3 = "TrackME_" + LoginAPI.getInstance().getUserPhone();

        database.collection(colName3).document(tName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString(KEY_PHONE) != null) {
                    trackerPhone_tv.setText(documentSnapshot.getString(KEY_PHONE));
                }
                if (documentSnapshot.getString(KEY_CODE) != null) {
                    trackerCode_tv.setText(documentSnapshot.getString(KEY_CODE));
                }
                // retrieve the image url.
                if (documentSnapshot.getString(KEY_IMGURL) != null) {
                    Picasso.get().load(documentSnapshot.getString(KEY_IMGURL)).fit().into(trackerImg_iv);
                }
            }
        });
    }
}