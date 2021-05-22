package android.example.mytrackerappvertwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.mytrackerappvertwo.LoginActivity;
import android.example.mytrackerappvertwo.MainActivity;
import android.example.mytrackerappvertwo.MyCircleActivity;
import android.example.mytrackerappvertwo.R;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.ThrowOnExtraProperties;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.LoginAPI;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;


public class DashboardActivity extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    private GoogleMap map;

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    public static final int DEFAULT_UPDATE_INTERVAL = 120;
    private static final int FAST_UPDATE_INTERVAL = 30;


    SupportMapFragment mapFragment;

    //Google's API for location services. the majority of app functions using this class.
    FusedLocationProviderClient fusedLocationProviderClient;


    private SwitchCompat locationSwitch;
    private static String KEY_SIZE = "locationSize";


    //variable to remember if we are tracking location or not.
    boolean updateOn = false;


    //Location request is a config file for all setting that influence fused location provider client.
    LocationRequest locationRequest;

    //event that is triggered whenever the update interval is met. 5 sec or 30 sec.
    LocationCallback locationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // save the location
            storeAndShow(locationResult.getLastLocation());
        }

    };

    private RelativeLayout relLayout;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();


    private long backPressedTime;
    private Toast backToast;

    private int cnt;
    //for sos
    private double curLat, curLong;
    private String emergency, emergencyTwo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynavigation_drawer);

// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        relLayout = findViewById(R.id.parentRelLayout);


        locationSwitch = findViewById(R.id.locationSwitch);


        // set all properties of LocationRequest
        locationRequest = LocationRequest.create();

        //how often does teh default location check occur?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        //how often does the location check occur when set to the most frequent update
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(PRIORITY_BALANCED_POWER_ACCURACY);


        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationSwitch.isChecked()) {
                    //turn on location tracking.
                    showSnackBar();
                    startLocationUpdates();
                    updateOn = true;


                } else {
                    //turn off location tracking
                    updateOn = false;
                    stopLocationUpdates();
                }

            }

        });
        updateGPS();


        //Check runtime permission
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //when permission granted
            //Call method
            updateGPS();

        } else {
            //When permission denied
            // Request permission
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }

        // sms request permission
        ActivityCompat.requestPermissions(DashboardActivity.this,new String[]{Manifest.permission.SEND_SMS},PackageManager.PERMISSION_GRANTED);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //navigation bar codes below.
        Toolbar toolbar = findViewById(R.id.toolbarId);
        toolbar.setTitleTextAppearance(this, R.style.NavigationToolbarFont);
        setSupportActionBar(toolbar);


        nav = findViewById(R.id.nav_menu_id);
        drawerLayout = findViewById(R.id.drawerId);

        showUserInNavHeader();

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_myCircle:
                        if (user!=null && mAuth!=null)
                        {
                            startActivity(new Intent(getApplicationContext(), MyCircleActivity.class));
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_trackingMe:
                        if (user!=null && mAuth!=null)
                        {
                           startActivity(new Intent(getApplicationContext(),  TrackingMeActivity.class));
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_shareCode:
                        Intent intentShare = new Intent(Intent.ACTION_SEND);
                        intentShare.setType("text/plain");
                        intentShare.putExtra(Intent.EXTRA_SUBJECT, "Regarding Sharing of Code");
                        intentShare.putExtra(Intent.EXTRA_TEXT, "Use this code " + LoginAPI.getInstance().getUserCode() + "to add me on MapsEye application");
                        startActivity(Intent.createChooser(intentShare, "Share code using .."));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.signOutMenuId:
                        if (user!=null && mAuth!=null)
                        {
                            if (updateOn)
                            {
                                updateOn=false;
                                stopLocationUpdates();
                            }
                            mAuth.signOut();
                            finish();
                            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
    }

    private void showSnackBar() {
        Snackbar.make(relLayout, "Your Closed ones can track you now", Snackbar.LENGTH_LONG)
                .setTextColor(getResources().getColor(android.R.color.holo_orange_light))
                .show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


    private void updateGPS() {
        // get permission for the user to track GPS
        // get the current location from the fused client
        // update the UI - i.e. set all properties in their associated text view items.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DashboardActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions. put the values of location. xx into the UI componenets.
                    //we will take the location class and update the different fields.

                    curLat=location.getLatitude();
                    curLong=location.getLongitude();
                    storeAndShow(location);


                }
            });
        } else {
            // permission not granted yet.
            // get permission if correct operating system. M is 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

    }


    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    //after permissions granted.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted to work properly", Toast.LENGTH_SHORT).show();
                    // if denied permission goes out of the application.
                    finish();
                }
                break;
        }
    }

    private void storeAndShow(Location location) {
        // put inside updateOn
        if (location != null) {
            //Sync map
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //Initialize Lat lng
                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
                    //Create marker options
                    MarkerOptions options = new MarkerOptions();
                    options.position(myPos);
                    options.title("You are here!");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    //Zoom map
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 13));
                    // add marker on map
                    googleMap.addMarker(options);
                }
            });
        }
        if (updateOn)
        {
            Log.d("UPDTESTER", "working");
            Toast.makeText(this, "lat and ln stored", Toast.LENGTH_SHORT).show();


            DocumentReference ref = fStore.collection("users").document(LoginAPI.getInstance().getUserPhone());


            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getLong(KEY_SIZE) != null) {
                        cnt = documentSnapshot.getLong(KEY_SIZE).intValue() + 1;
                    } else {
                        cnt = 1;
                    }

                    // store the lat, lon and timestamp to firestore every 1 min.
                    DocumentReference documentReference = fStore.collection("users").document(LoginAPI.getInstance().getUserPhone());
                    Map<String, Object> mmp = new HashMap<>();

                    String KEY_LAT = "lat " + cnt;
                    String KEY_LON = "lon " + cnt;
                    String KEY_TIME = "timeAdded " + cnt;



                    Calendar calendar = Calendar.getInstance();
                    Date currentTime = Calendar.getInstance().getTime();
                    String formattedTime = "at ";
                    formattedTime += DateFormat.getTimeInstance(DateFormat.SHORT).format(currentTime);
                    formattedTime += " on ";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MMM.yyyy");
                    String date = simpleDateFormat.format(calendar.getTime());
                    formattedTime += date;


                    mmp.put(KEY_LAT, location.getLatitude());
                    mmp.put(KEY_LON, location.getLongitude());
                    mmp.put(KEY_TIME, formattedTime);
                    mmp.put(KEY_SIZE, cnt);


                    documentReference.set(mmp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("DashboardAct", "stored timestamp");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DashboardAct", "exception arised: " + e.getMessage());
                        }
                    });
                }
            });

        }


    }




    //
    // setting the userprofile in navigation header.
    private void showUserInNavHeader() {
        View headerView = nav.getHeaderView(0);
        TextView navUsernameHeader = headerView.findViewById(R.id.navUsernameHeader);
        TextView navLoggedinHeader = headerView.findViewById(R.id.navLoggedinHeader);
        TextView navCodeHeader = headerView.findViewById(R.id.navCodeHeader);
        ImageView avatarUserHeader = headerView.findViewById(R.id.avatarUserHeader);
        if (LoginAPI.getInstance().getUserName()!=null)
        {
            Picasso.get().load(LoginAPI.getInstance().getImgUrl()).into(avatarUserHeader);
            navUsernameHeader.setText(LoginAPI.getInstance().getUserName().toUpperCase());
            navLoggedinHeader.setText(LoginAPI.getInstance().getUserPhone());
            navCodeHeader.setText("Code " + LoginAPI.getInstance().getUserCode());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sos_layout, menu);
        return true;
    }

    //
//    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sos_menu:
                emergency = getIntent().getStringExtra("key_EmergencyOne");
                emergencyTwo = getIntent().getStringExtra("key_EmergencyTwo");
                if (emergency != null && emergencyTwo != null) {
                        //showSnackBarTwo(emergency,emergencyTwo);

                    Toast toast=Toast.makeText(DashboardActivity.this,"SOS alert sent to "+ emergency+ " and "+emergencyTwo,Toast.LENGTH_LONG);

                    View view = toast.getView();
                    view.setBackgroundResource(R.color.red);

                    toast.show();
                    SmsManager smsManager = SmsManager.getDefault();


                    smsManager.sendTextMessage(emergency, null,
                            "Panic Alert from "+LoginAPI.getInstance().getUserName()+
                                    "! Visit https://www.google.com/maps/search/?api=1&query="+curLat+"," +curLong+
                                    " or view MapsEye application for more",
                             null, null);


                    smsManager.sendTextMessage(emergencyTwo, null,
                            "Panic Alert from "+LoginAPI.getInstance().getUserName()+
                                    "! Visit https://www.google.com/maps/search/?api=1&query="+curLat+"," +curLong+
                                    " or view MapsEye application for more",
                            null, null);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Sorry, Emergency Contact not Setup yet", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}

