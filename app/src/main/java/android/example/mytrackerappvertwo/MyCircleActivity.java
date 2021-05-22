package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import util.LoginAPI;

public class MyCircleActivity extends AppCompatActivity implements View.OnClickListener {


    private ArrayList<Person> names;
    private TextView tv_addInfo;
    private FloatingActionButton plusBtnView;
    private FirebaseFirestore fStore;

    private static String TAG = "MyCircleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);
        this.setTitle("My Circle");



        tv_addInfo = findViewById(R.id.tv_addInfo);
        plusBtnView = findViewById(R.id.plusBtnView);
        plusBtnView.setOnClickListener(this);

        fStore = FirebaseFirestore.getInstance();

        ListView listView = findViewById(R.id.listView);
        names = new ArrayList<>();

        String colName = "Friends_" + LoginAPI.getInstance().getUserPhone();
        //showing friends in a listView.
        fStore.collection(colName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        names = new ArrayList<Person>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String collected = document.getId();
                                names.add(new Person(collected, R.drawable.ic_baseline_search_24));
                            }
                            PersonAdapter itemsAdapter =
                                    new PersonAdapter(getBaseContext(), names, R.color.listViewColor);
                            //itemsAdapter = new ArrayAdapter<Person>(getBaseContext(), R.layout.list_items, R.id.tv_sample_list_item, names);
                            listView.setAdapter(itemsAdapter);
                        } else {
                            Log.d(TAG, "Error retreiving friends documents: ", task.getException());
                        }
                        if (names.size() == 0) {
                            tv_addInfo.setVisibility(View.VISIBLE);
                        }
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //accessing name of the listView selected.
                TextView txtview = (TextView) view.findViewById(R.id.tv_personName);
                String fname = txtview.getText().toString();
                DocumentReference documentReference = fStore.collection("users").document(LoginAPI.getInstance().getUserPhone());

                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getString(fname) != null) {
                            String fphone = documentSnapshot.getString(fname);
                            Intent intent = new Intent(getApplicationContext(), FamilyMapActivity.class);
                            intent.putExtra("key_famPhone", fphone);
                            intent.putExtra("key_famName", fname);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MyCircleActivity.this, AddActivity.class);
        startActivity(intent);
        finish();
    }


}