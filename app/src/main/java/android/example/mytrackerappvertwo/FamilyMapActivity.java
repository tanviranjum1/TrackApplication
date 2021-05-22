package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.List;

import util.LoginAPI;

public class FamilyMapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String TAG = "FamilyMapActivity";

    private static final String KEY_SIZE = "locationSize";
    private TextView address_TV;

    private Spinner spinnerTimestamp;


    private String[] timestamps = new String[]{"timestamp i", "timestamp ii", "timestamp iii", "timestamp iv", "timestamp v", "timestamp vi"};

    private int arr_length;
    private boolean[] bool_arr = new boolean[]{false, false, false, false, false, false};
    private LatLng tsOne, tsTwo, tsThree, tsFour, tsFive, tsLast;


    //Todo: create markersoptions for timestamps.
    private MarkerOptions tsOneMarkerOptions;
    private MarkerOptions tsTwoMarkerOptions;
    private MarkerOptions tsThreeMarkerOptions;
    private MarkerOptions tsFourMarkerOptions;
    private MarkerOptions tsFiveMarkerOptions;
    private MarkerOptions tsLastMarkerOptions;

    private String famName;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_map);

        Toolbar toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);

        //spinner obtain item and put in spinner
        address_TV = findViewById(R.id.address_TV);
        spinnerTimestamp = findViewById(R.id.spinnerTimestamp);
        spinnerTimestamp.setOnItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        //receive phone number and get lat long firebase and show in map
        String famPhone = getIntent().getStringExtra("key_famPhone");
        famName = getIntent().getStringExtra("key_famName");


        DocumentReference documentReference = db.collection("users").document(famPhone);

        // just retrieve the size of known locations
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getLong(KEY_SIZE) != null) {

                    arr_length = documentSnapshot.getLong(KEY_SIZE).intValue();
                    int i = 0;
                    int j = arr_length;

                    //show map of latest location:
                    String KEY_LAT = "lat " + j;
                    String KEY_LON = "lon " + j;
                    String KEY_TIME = "timeAdded " + j;
                    tsLast = new LatLng(documentSnapshot.getDouble(KEY_LAT), documentSnapshot.getDouble(KEY_LON));
                    timestamps[i] = documentSnapshot.getString(KEY_TIME);
                    bool_arr[i] = true;
                    showMap(i);

                    j--;
                    i++;

                    while (i <= 5) {
                        KEY_LAT = "lat " + j;
                        KEY_LON = "lon " + j;
                        KEY_TIME = "timeAdded " + j;


                        if (documentSnapshot.getDouble(KEY_LAT) != null && documentSnapshot.getDouble(KEY_LON) != null && documentSnapshot.getString(KEY_TIME) != null) {
                            bool_arr[i] = true;
                            timestamps[i] = documentSnapshot.getString(KEY_TIME);
                            if (i == 1) {
                                tsOne = new LatLng(documentSnapshot.getDouble(KEY_LAT), documentSnapshot.getDouble(KEY_LON));
                            } else if (i == 2) {
                                tsTwo = new LatLng(documentSnapshot.getDouble(KEY_LAT), documentSnapshot.getDouble(KEY_LON));
                            } else if (i == 3) {
                                tsThree = new LatLng(documentSnapshot.getDouble(KEY_LAT), documentSnapshot.getDouble(KEY_LON));
                            } else if (i == 4) {
                                tsFour = new LatLng(documentSnapshot.getDouble(KEY_LAT), documentSnapshot.getDouble(KEY_LON));
                            } else {
                                tsFive = new LatLng(documentSnapshot.getDouble(KEY_LAT), documentSnapshot.getDouble(KEY_LON));
                            }
                            Log.d(TAG,"i "+i);
                        }
                        else {
                            break;
                        }
                        j--;
                        i++;
                    }
                    ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.sample_spinner_item_view,
                            timestamps);
                    myAdapter.setDropDownViewResource(R.layout.sample_spinner_dropdown_item);
                    spinnerTimestamp.setAdapter(myAdapter);
                }
                Log.d(TAG, "Retrieved successfully: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error in retrieval: " + e.toString());
            }
        });
    }

    private void showMap(int val) {
        //Sync map
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Geocoder geocoder = new Geocoder(getApplicationContext());

                if (val == 0) {
                    tsLastMarkerOptions = new MarkerOptions().position(tsLast)
                            .title(famName + " was last here " + timestamps[0])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(tsLastMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsLast, 12));
                    // address not guranteed to work.
                    try {
                        List<Address> addresses = geocoder.getFromLocation(tsLast.latitude, tsLast.longitude, 1);
                        address_TV.setText(addresses.get(0).getAddressLine(0));
                        address_TV.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        address_TV.setText("Unable to get street addresss");
                        address_TV.setVisibility(View.VISIBLE);
                    }
                }
                if (val == 1) {
                    tsOneMarkerOptions = new MarkerOptions().position(tsOne)
                            .title(famName + " " + timestamps[1])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(tsOneMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsOne, 12));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(tsOne.latitude, tsOne.longitude, 1);
                        address_TV.setText(addresses.get(0).getAddressLine(0));
                        address_TV.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        address_TV.setText("Unable to get street addresss");
                        address_TV.setVisibility(View.VISIBLE);
                    }

                } else if (val == 2) {
                    tsTwoMarkerOptions = new MarkerOptions().position(tsTwo)
                            .title(famName + " " + timestamps[2])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    mMap.addMarker(tsTwoMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsTwo, 12));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(tsTwo.latitude, tsTwo.longitude, 1);
                        address_TV.setText(addresses.get(0).getAddressLine(0));
                        address_TV.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        address_TV.setText("Unable to get street addresss");
                        address_TV.setVisibility(View.VISIBLE);
                    }
                } else if (val == 3) {
                    tsThreeMarkerOptions = new MarkerOptions().position(tsThree)
                            .title(famName + " " + timestamps[3])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mMap.addMarker(tsThreeMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsThree, 12));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(tsThree.latitude, tsThree.longitude, 1);
                        address_TV.setText(addresses.get(0).getAddressLine(0));
                        address_TV.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        address_TV.setText("Unable to get street addresss");
                        address_TV.setVisibility(View.VISIBLE);
                    }
                } else if (val == 4) {
                    tsFourMarkerOptions = new MarkerOptions().position(tsFour)
                            .title(famName + " " + timestamps[4])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.addMarker(tsFourMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsFour, 12));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(tsFour.latitude, tsFour.longitude, 1);
                        address_TV.setText(addresses.get(0).getAddressLine(0));
                        address_TV.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        address_TV.setText("Unable to get street addresss");
                        address_TV.setVisibility(View.VISIBLE);
                    }
                } else if (val == 5) {
                    tsFiveMarkerOptions = new MarkerOptions().position(tsFive)
                            .title(famName + " " + timestamps[5])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(tsFiveMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsFive, 12));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(tsFive.latitude, tsFive.longitude, 1);
                        address_TV.setText(addresses.get(0).getAddressLine(0));
                        address_TV.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        address_TV.setText("Unable to get street addresss");
                        address_TV.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String timestampSelected = spinnerTimestamp.getSelectedItem().toString();
        int posSelected = position;
        if (bool_arr[posSelected]) {
            showMap(posSelected);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}