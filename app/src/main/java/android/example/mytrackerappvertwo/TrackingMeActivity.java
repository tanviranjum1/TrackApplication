package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import util.LoginAPI;

public class TrackingMeActivity extends AppCompatActivity {


    private ArrayList<Person> stringArrayList;


    private TextView tv_trackInfo;

    private FirebaseFirestore fStore;

    private static String TAG = "TrackingMeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_me);
        this.setTitle("Tracking Me");

        tv_trackInfo = findViewById(R.id.tv_trackInfo);

        fStore = FirebaseFirestore.getInstance();

        ListView trackMeList = findViewById(R.id.trackMeList);

        String colName2 = "TrackME_" + LoginAPI.getInstance().getUserPhone();
        //showing friends in a listView.
        fStore.collection(colName2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        stringArrayList = new ArrayList<Person>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String collected2=document.getId();
                                stringArrayList.add(new Person(collected2, R.drawable.ic_tracker));
                            }
                            PersonAdapter itemsAdapter =
                                    new PersonAdapter(getBaseContext(), stringArrayList, R.color.listViewColor);
                            trackMeList.setAdapter(itemsAdapter);
                        } else {
                            Log.d(TAG, "Error retreiving track me documents: ", task.getException());
                        }
                        if (stringArrayList.size() == 0) {
                            tv_trackInfo.setVisibility(View.VISIBLE);
                        }
                    }
                });
        trackMeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accessing name of the listView selected.
                TextView txtview = (TextView) view.findViewById(R.id.tv_personName);
                String tName = txtview.getText().toString();
                Intent intent = new Intent(TrackingMeActivity.this, TrackingMeProfile.class);
                intent.putExtra("key_trackerName", tName);
                startActivity(intent);
            }
        });
    }
}